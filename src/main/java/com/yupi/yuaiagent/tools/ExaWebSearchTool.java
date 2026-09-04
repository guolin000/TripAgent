package com.yupi.yuaiagent.tools;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public class ExaWebSearchTool {

    private final String exaApiKey;

    private static final String SEARCH_API_URL = "https://api.exa.ai/search";

    public ExaWebSearchTool(String exaApiKey) {
        this.exaApiKey = exaApiKey;
    }

    /**
     * 执行网络搜索
     *
     * @param searchQuery 搜索内容
     * @return 搜索结果摘要列表
     */
    @Tool(description = "使用EXA提供的Web Search功能进行网络搜索。如果出现搜索失败，可以尝试多次调用该工具")
    public String exaSearch(
            @ToolParam(description = "搜索内容")
            String searchQuery) {
        log.info("api:{}", exaApiKey);
        log.info("调用 EXA API 搜索关键词：{}", searchQuery);

        try {
            // 1. 构建请求参数 Map，包含 contents.text.maxCharacters
            Map<String, Object> paramMap = new HashMap<>();
            paramMap.put("query", searchQuery);

            Map<String, Object> contents = new HashMap<>();
            Map<String, Object> text = new HashMap<>();
            text.put("maxCharacters", 1000); // 控制文本长度
            contents.put("text", text);
            paramMap.put("contents", contents);

            // 2. 转换为 JSON 字符串
            String requestBodyJson = JSONUtil.toJsonStr(paramMap);

            // 3. 发送 POST 请求
            HttpResponse response = HttpRequest.post(SEARCH_API_URL)
                    .header("x-api-key", exaApiKey)
                    .header("Content-Type", "application/json")
                    .body(requestBodyJson)
                    .execute();

            // 4. 获取响应状态码和内容
            int status = response.getStatus();
            String body = response.body();

            if (status == 200 && ObjectUtil.isNotEmpty(body)) {
                JSONObject jsonResponse = JSONUtil.parseObj(body);
                JSONArray resultsArray = jsonResponse.getJSONArray("results");
                if (resultsArray != null && !resultsArray.isEmpty()) {
                    StringBuilder resultBuilder = new StringBuilder();

                    for (int i = 0; i < resultsArray.size(); i++) {
                        JSONObject result = resultsArray.getJSONObject(i);

                        String title = result.getStr("title");
                        String url = result.getStr("url");
                        String snippet = "";

                        // 获取 text.snippet（如果存在）
                        if (result.containsKey("text") && result.get("text") instanceof JSONObject) {
                            JSONObject textObj = result.getJSONObject("text");
                            snippet = textObj.getStr("snippet");
                        } else {
                            snippet = "无摘要信息";
                        }

                        // 拼接结果
                        resultBuilder.append("【结果 ").append(i + 1).append("】\n");
                        resultBuilder.append("标题: ").append(title).append("\n");
                        resultBuilder.append("链接: ").append(url).append("\n");
                        resultBuilder.append("摘要: ").append(snippet).append("\n\n");
                    }

                    return resultBuilder.toString();
                } else {
                    return "未找到相关结果";
                }
            } else {
                log.error("请求失败，状态码：{}，响应内容：{}", status, body);
                return "请求失败或无返回内容";
            }
        } catch (Exception e) {
            log.error("调用 EXA 搜索服务时发生错误", e);
            throw new RuntimeException("调用 EXA 搜索请求出现错误", e);
        }
    }
}
