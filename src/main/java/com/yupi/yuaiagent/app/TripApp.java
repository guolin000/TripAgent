package com.yupi.yuaiagent.app;

import com.yupi.yuaiagent.advisor.MyLoggerAdvisor;
import com.yupi.yuaiagent.rag.QueryRewriter;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.InMemoryChatMemoryRepository;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.stream.Collectors;

@Component
@Slf4j
public class TripApp {

    private final ChatClient chatClient;

    private static final String SYSTEM_PROMPT = "扮演专业且经验丰富的中国国内AI旅游规划大师。开场向用户表明身份，告知用户可以咨询旅行规划、景点、美食、交通及住宿等问题。" +
            "围绕用户的旅行目的地、出行时间、旅行天数、同行人员、预算及兴趣偏好进行提问；" +
            "根据用户需求制定个性化旅行方案，合理安排每日景点、游玩路线、交通方式、餐饮及住宿。" +
            "遇到实时信息时优先调用相关工具获取准确数据，并综合用户偏好动态调整行程，最终给出清晰、实用且可执行的旅游方案。";

    @Resource
    private TripRouter tripRouter;


    /**
     * 对话记忆
     */
    private final MessageWindowChatMemory chatMemory;

    /**
     * 初始化 ChatClient
     *
     * @param dashscopeChatModel
     */
    public TripApp(ChatModel dashscopeChatModel) {
//        // 初始化基于文件的对话记忆
//        String fileDir = System.getProperty("user.dir") + "/tmp/chat-memory";
//        ChatMemory chatMemory = new FileBasedChatMemory(fileDir);
        // 初始化基于内存的对话记忆
        this.chatMemory = MessageWindowChatMemory.builder()
                .chatMemoryRepository(
                        new InMemoryChatMemoryRepository()
                )
                .maxMessages(20)
                .build();
        chatClient = ChatClient.builder(dashscopeChatModel)
                .defaultSystem(SYSTEM_PROMPT)
                .defaultAdvisors(
                        MessageChatMemoryAdvisor.builder(chatMemory).build(),
                        // 自定义日志 Advisor，可按需开启
                        new MyLoggerAdvisor()
//                        // 自定义推理增强 Advisor，可按需开启
//                       ,new ReReadingAdvisor()
                )
                .build();
    }

    /**
     * AI 基础对话（支持多轮对话记忆）
     *
     * @param message
     * @param chatId
     * @return AI 回复
     */
    public String doChat(String message, String chatId) {
        ChatResponse chatResponse = chatClient
                .prompt()
                .user(message)
                .advisors(spec -> spec.param(ChatMemory.CONVERSATION_ID, chatId))
                .call()
                .chatResponse();
        String content = chatResponse.getResult().getOutput().getText();
        log.info("content: {}", content);
        return content;
    }

    /**
     * AI 基础对话（支持多轮对话记忆，SSE 流式传输）
     *
     * @param message
     * @param chatId
     * @return
     */
    public Flux<String> doChatByStream(
            String message,
            String chatId
    ) {

        // ==============================
        // 1. 获取历史对话
        // ==============================

        String chatHistory = getChatHistory(chatId);

        // ==============================
        // 2. Router
        // ==============================
        TripRoute route =
                tripRouter.route(
                        message,
                        chatHistory
                );
        log.info(
                "Trip route: routes={}, reason={}",
                route.routes(),
                route.reason()
        );
        // ==============================
        // 3. 根据路由决定能力
        // ==============================
        List<TripRouteType> routes = route.routes();

        // 多能力组合
        if (routes.size() > 1) {
            return doChatWithMultipleCapabilities(
                    message,
                    chatId,
                    routes
            );
        }

        // 单能力
        TripRouteType routeType = routes.get(0);

        return switch (routeType) {
            case RAG -> doChatByRagStream(message, chatId);
            case ALL_TOOLS -> doChatWithToolsStream(message, chatId);
            case AMAP_MCP -> doChatWithMcpStream(message, chatId);
            case GENERAL -> doChatGeneralStream(message, chatId);
        };
    }


    private Flux<String> doChatWithMultipleCapabilities(
            String message,
            String chatId,
            List<TripRouteType> routes
    ) {

        var prompt = chatClient
                .prompt()
                .user(message)
                .advisors(spec ->
                        spec.param(
                                ChatMemory.CONVERSATION_ID,
                                chatId
                        )
                )
                .advisors(new MyLoggerAdvisor());

        if (routes.contains(TripRouteType.RAG)) {

            prompt = prompt.advisors(
                    QuestionAnswerAdvisor
                            .builder(tripAppVectorStore)
                            .build()
            );
        }

        if (routes.contains(TripRouteType.ALL_TOOLS)) {

            prompt = prompt.toolCallbacks(allTools);
        }

        if (routes.contains(TripRouteType.AMAP_MCP)) {

            prompt = prompt.toolCallbacks(
                    toolCallbackProvider
            );
        }

        return prompt
                .stream()
                .content();
    }
    private Flux<String> doChatGeneralStream(
            String message,
            String chatId
    ) {

        return chatClient
                .prompt()
                .user(message)
                .advisors(spec ->
                        spec.param(
                                ChatMemory.CONVERSATION_ID,
                                chatId
                        )
                )
                .stream()
                .content();
    }
    record TripReport(String title, List<String> suggestions) {

    }
    /**
     * AI 旅游规划报告功能（实战结构化输出）
     *
     * @param message
     * @param chatId
     * @return
     */
    public TripReport doChatWithReport(String message, String chatId) {
        TripReport tripReport = chatClient
                .prompt()
                .system(SYSTEM_PROMPT + "每次完成旅游规划后生成旅游规划报告，标题为{用户名}的旅行规划，"+
                        "需要包含目的地、行程概览、每日行程及实用建议。")
                .user(message)
                .advisors(spec -> spec.param(ChatMemory.CONVERSATION_ID, chatId))
                .call()
                .entity(TripReport.class);
            log.info("tripReport: {}", tripReport);
        return tripReport;
    }

    // =========================
    // AI 旅游知识库 RAG
    // =========================
    /**
     *  旅游知识库向量存储
     */
    @Resource
    private VectorStore tripAppVectorStore;
    /**
     *  旅游知识库 RAG 云检索增强服务
     */
    @Resource
    private Advisor tripAppRagCloudAdvisor;
    /**
     *  旅游知识库 RAG 向量存储
     */
    @Resource
    private VectorStore pgVectorVectorStore;
    /**
     *  旅游知识库查询重写
     */

    @Resource
    private QueryRewriter queryRewriter;

    /**
     * 和 RAG 知识库进行对话
     *
     * @param message
     * @param chatId
     * @return
     */
    public String doChatWithRag(String message, String chatId) {
        // 查询重写
        String rewrittenMessage = queryRewriter.doQueryRewrite(message);
        ChatResponse chatResponse = chatClient
                .prompt()
                // 使用改写后的查询
                .user(rewrittenMessage)
                .advisors(spec -> spec.param(ChatMemory.CONVERSATION_ID, chatId))
                // 开启日志，便于观察效果
                .advisors(new MyLoggerAdvisor())
                // 应用 RAG 知识库问答
                .advisors(QuestionAnswerAdvisor.builder(tripAppVectorStore)
                        .build())
                // 应用 RAG 检索增强服务（基于云知识库服务）
//                .advisors(loveAppRagCloudAdvisor)
                // 应用 RAG 检索增强服务（基于 PgVector 向量存储）
//                .advisors(new QuestionAnswerAdvisor(pgVectorVectorStore))
                // 应用自定义的 RAG 检索增强服务（文档查询器 + 上下文增强器）
//                .advisors(
//                        LoveAppRagCustomAdvisorFactory.createLoveAppRagCustomAdvisor(
//                                loveAppVectorStore, "单身"
//                        )
//                )
                .call()
                .chatResponse();
        String content = chatResponse.getResult().getOutput().getText();
        log.info("content: {}", content);
        return content;
    }

    private Flux<String> doChatByRagStream(
            String message,
            String chatId
    ) {

        // 查询重写
        String rewrittenMessage =
                queryRewriter.doQueryRewrite(message);

        log.info(
                "RAG query rewrite: {} -> {}",
                message,
                rewrittenMessage
        );

        return chatClient
                .prompt()
                .user(rewrittenMessage)
                .advisors(spec ->
                        spec.param(
                                ChatMemory.CONVERSATION_ID,
                                chatId
                        )
                )
                .advisors(new MyLoggerAdvisor())

                // RAG
                .advisors(
                        QuestionAnswerAdvisor
                                .builder(tripAppVectorStore)
                                .build()
                )

                // Stream
                .stream()
                .content();
    }
    // AI 调用工具能力
    @Resource
    private ToolCallback[] allTools;

    /**
     * TripAgent 旅游规划工具调用
     *
     * @param message
     * @param chatId
     * @return
     */
    public String doChatWithTools(String message, String chatId) {
        ChatResponse chatResponse = chatClient
                .prompt()
                .user(message)
                .advisors(spec -> spec.param(ChatMemory.CONVERSATION_ID, chatId))
                // 开启日志，便于观察效果
                .advisors(new MyLoggerAdvisor())
                .toolCallbacks(allTools)
                .call()
                .chatResponse();
        String content = chatResponse.getResult().getOutput().getText();
        log.info("content: {}", content);
        return content;
    }

    private Flux<String> doChatWithToolsStream(
            String message,
            String chatId
    ) {

        return chatClient
                .prompt()
                .user(message)
                .advisors(spec ->
                        spec.param(
                                ChatMemory.CONVERSATION_ID,
                                chatId
                        )
                )
                .advisors(new MyLoggerAdvisor())

                .toolCallbacks(allTools)

                .stream()
                .content();
    }
    // AI 调用 MCP 服务

    @Resource
    private ToolCallbackProvider toolCallbackProvider;

    /**
     * TripAgent MCP 服务调用
     *
     * @param message
     * @param chatId
     * @return
     */
    public String doChatWithMcp(String message, String chatId) {
        //打印toolCallbackProvider中的工具列表
        log.info("toolCallbackProvider: {}", toolCallbackProvider.getToolCallbacks());
        ChatResponse chatResponse = chatClient
                .prompt()
                .user(message)
                .advisors(spec -> spec.param(ChatMemory.CONVERSATION_ID, chatId))
                // 开启日志，便于观察效果
                .advisors(new MyLoggerAdvisor())
                .toolCallbacks(toolCallbackProvider)
                .call()
                .chatResponse();
        String content = chatResponse.getResult().getOutput().getText();
        log.info("content: {}", content);
        return content;
    }

    private Flux<String> doChatWithMcpStream(
            String message,
            String chatId
    ) {

        log.info(
                "Using AMap MCP tools: {}",
                toolCallbackProvider.getToolCallbacks()
        );

        return chatClient
                .prompt()
                .user(message)
                .advisors(spec ->
                        spec.param(
                                ChatMemory.CONVERSATION_ID,
                                chatId
                        )
                )
                .advisors(new MyLoggerAdvisor())

                // 高德 MCP
                .toolCallbacks(toolCallbackProvider)

                // Stream
                .stream()
                .content();
    }
    private String getChatHistory(String chatId) {

        List<Message> messages = chatMemory.get(chatId);

        if (messages == null || messages.isEmpty()) {
            return "";
        }

        return messages.stream()
                .map(message ->
                        message.getMessageType() + ": "
                                + message.getText()
                )
                .collect(Collectors.joining("\n"));
    }
}
