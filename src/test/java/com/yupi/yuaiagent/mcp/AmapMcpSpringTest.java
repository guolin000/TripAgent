package com.yupi.yuaiagent.mcp;

import io.modelcontextprotocol.client.McpSyncClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class AmapMcpSpringTest {

    @Autowired
    private List<McpSyncClient> mcpSyncClients;

    @Test
    void testAmapMcp() {

        System.out.println("=================================");
        System.out.println("开始测试高德 MCP");
        System.out.println("=================================");

        System.out.println("MCP Client 数量: " + mcpSyncClients.size());

        for (McpSyncClient client : mcpSyncClients) {

            System.out.println("---------------------------------");
            System.out.println("Server Info:");
            System.out.println(client.getServerInfo());

            System.out.println("---------------------------------");
            System.out.println("MCP Tools:");

            var result = client.listTools();

            result.tools().forEach(tool -> {
                System.out.println("Tool Name: " + tool.name());
                System.out.println("Description: " + tool.description());
                System.out.println("---------------------------------");
            });
        }

        System.out.println("=================================");
        System.out.println("MCP 测试完成");
        System.out.println("=================================");
    }
}