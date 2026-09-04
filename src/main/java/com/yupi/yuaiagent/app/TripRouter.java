package com.yupi.yuaiagent.app;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class TripRouter {

    private final ChatClient routerChatClient;

    private static final String ROUTER_PROMPT = """
            你是一个旅游AI Agent智能路由器。
            你的任务不是回答用户问题，而是判断当前用户问题需要使用哪些能力。
            你可以选择以下能力：      
            GENERAL：
            普通闲聊、简单问候、不需要外部信息的问题。
            
            RAG：
            旅游知识库。
            适用于相对静态的旅游知识，例如：
            - 景点历史
            - 景点介绍
            - 旅游文化
            - 旅游攻略知识
            - 旅游注意事项
            - 目的地基本介绍
            
            ALL_TOOLS：
            系统普通业务工具。
            
            AMAP_MCP：
            高德地图 MCP。
            适用于需要实时地理信息的问题，例如：
            - 天气
            - 景点搜索
            - POI搜索
            - 地理编码
            - 路线规划
            - 距离查询
            - 周边搜索
            - 驾车路线
            - 公交路线
            - 步行路线
            - 旅游行程规划
            
            路由规则：
            
            1. 如果用户要求旅游规划、一日游、两日游、三日游等，
               选择 AMAP_MCP,RAG
            
            2. 如果用户询问天气，
               选择 AMAP_MCP。
            
            3. 如果用户要求查询景点、附近景点、POI，
               选择 AMAP_MCP。
            
            4. 如果用户要求路线、距离、怎么去，
               选择 AMAP_MCP。
            
            5. 如果用户需要实时旅游信息，
               选择 AMAP_MCP。
            
            6. 如果用户询问景点历史、文化背景、旅游知识，
               选择 RAG。
            
            7. 如果一个问题同时需要实时旅游信息和知识库，
               可以同时选择 AMAP_MCP 和 RAG。
            
            8. 如果用户需要系统业务工具，
               选择 ALL_TOOLS。
            
            9. 如果只是普通聊天，
               选择 GENERAL。
            
            10. 如果当前问题依赖之前的对话，
                必须结合历史对话进行判断。
            
            例如：
            
            历史：
            用户：我准备去杭州玩三天。
            
            当前问题：
            用户：第二天怎么安排？
            
            应该选择：
            AMAP_MCP,RAG
            
            因为当前问题依赖历史上下文，实际上是在继续进行杭州旅游规划。
            
            再例如：
            
            历史：
            用户：我准备去北京玩。
            
            当前问题：
            用户：故宫是谁修建的？
            
            应该选择：
            RAG
            
            只返回JSON，不要输出任何其他内容。
            """;

    public TripRouter(ChatModel chatModel) {
        this.routerChatClient = ChatClient.builder(chatModel)
                .defaultSystem(ROUTER_PROMPT)
                .build();
    }

    /**
     * 根据当前问题 + 历史对话进行路由
     */
    public TripRoute route(
            String message,
            String chatHistory
    ) {

        String routerMessage = """
                ===== 历史对话 =====
                %s
                
                ===== 当前用户问题 =====
                %s
                
                请结合历史对话判断当前问题需要使用哪些能力。
                """.formatted(
                chatHistory,
                message
        );

        try {

            TripRoute route = routerChatClient
                    .prompt()
                    .user(routerMessage)
                    .call()
                    .entity(TripRoute.class);

            log.info(
                    "Trip Router: message={}, routes={}, reason={}",
                    message,
                    route.routes(),
                    route.reason()
            );

            return route;

        } catch (Exception e) {

            log.error("Trip Router failed", e);

            // 路由失败时，如果是旅游问题，
            // 默认使用高德 MCP
            return new TripRoute(
                    java.util.List.of(
                            TripRouteType.AMAP_MCP
                    ),
                    "Router调用失败，默认使用高德MCP"
            );
        }
    }
}