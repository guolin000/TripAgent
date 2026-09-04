package com.yupi.yuaiagent.app;

import jakarta.annotation.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;

@SpringBootTest
class TripAppTest {

    @Resource
    private TripApp tripApp;

    @Test
    void testChat() {
        String chatId = UUID.randomUUID().toString();
        // 第一轮
        String message = "你好，我准备去北京旅游，我叫小明。";
        String answer = tripApp.doChat(message, chatId);
        // 第二轮
        message = "我准备和女朋友一起去，大概玩5天，预算8000元，比较喜欢历史建筑和美食。";
        answer = tripApp.doChat(message, chatId);
        Assertions.assertNotNull(answer);
        // 第三轮
        message = "你还记得我们准备去哪里旅游、玩几天以及我的兴趣吗？";
        answer = tripApp.doChat(message, chatId);
        Assertions.assertNotNull(answer);
    }

    @Test
    void doChatWithReport() {
        String chatId = UUID.randomUUID().toString();
        String message =
                "你好，我准备和女朋友去北京旅游5天，" +
                "预算8000元，喜欢历史建筑、美食和城市观光，" +
                "希望行程不要太赶，请帮我们制定一份详细的旅游规划。";
        TripApp.TripReport loveReport = tripApp.doChatWithReport(message, chatId);
        Assertions.assertNotNull(loveReport);
    }

    @Test
    void doChatWithRag() {
        String chatId = UUID.randomUUID().toString();
        String message = "我准备和女朋友去北京旅游5天，" +
                "第一次去北京，应该怎么合理安排每天的景点？" +
                "我们不喜欢行程太赶，希望一天不要安排太多景点。";
        String answer = tripApp.doChatWithRag(message, chatId);
        Assertions.assertNotNull(answer);
    }

    @Test
    void doChatWithTools() {
        // 测试天气查询
        testMessage("我和女朋友准备去北京旅游，" +
                "帮我查询北京最近几天的天气，" +
                "然后根据天气情况建议我们适合安排哪些户外景点。");

        // 测试景点搜索
        testMessage("我和女朋友第一次去北京，" +
                "帮我们推荐一些适合情侣游玩的景点，" +
                "最好兼顾历史文化和拍照体验。");
        // 测试餐厅搜索
        testMessage( "我们晚上想在故宫附近吃北京菜，" +
                "帮我们找几家适合情侣吃饭的餐厅，" +
                "预算人均150元左右。" );

        // 测试资源下载：图片下载
        testMessage("直接下载一张适合做手机壁纸的故宫图片为文件");

        // 测试终端操作：执行代码
        testMessage("执行 Python3 脚本来生成数据分析报告");

        // 测试文件操作：保存用户档案
        testMessage("保存我的旅游规划为文件，包含景点、餐厅和活动信息");

        // 测试 PDF 生成
        testMessage("生成一份‘国庆北京旅游’PDF，包含景点、餐厅和活动信息");
    }

    private void testMessage(String message) {
        String chatId = UUID.randomUUID().toString();
        String answer = tripApp.doChatWithTools(message, chatId);
        Assertions.assertNotNull(answer);
    }

    @Test
    void doChatWithMcp() {
        String chatId = UUID.randomUUID().toString();
         //测试地图 MCP
        String message = "查询北京实时天气与花期建议  ";
        String answer =  tripApp.doChatWithMcp(message, chatId);
        Assertions.assertNotNull(answer);
//        // 测试图片搜索 MCP
//        String message = "帮我搜索一些天安门的图片";
//        String answer =  tripApp.doChatWithMcp(message, chatId);
//        Assertions.assertNotNull(answer);
    }
}
