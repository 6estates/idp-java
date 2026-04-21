package com.sixestates.example;

import com.sixestates.Idp;
import com.sixestates.rest.v1.ai.AIChatApi;
import com.sixestates.type.IdpResponse;

import java.util.List;

import static com.sixestates.example.Example.TOKEN;

public class AIChatExample {

    public static void main(String[] args) {
        Idp.init(TOKEN);
        testSimpleChat();
    }

    public static void testSimpleChat() {
        // 1. 初始化请求并选择模型
        AIChatApi.ChatCompletionRequest request = new AIChatApi.ChatCompletionRequest("model_6e_ext_v4");

        // 2. 添加对话上下文
        request.addMessage("user", "where is china?");
        request.addMessage("assistant", "China is located in the east of Asia...");
        request.addMessage("user", "what is its population?");

        // 3. 设置可选参数
        request.setTemperature(0.7);
        request.setMaxTokens(500);

        try {
            // 4. 调用接口
            IdpResponse<AIChatApi.ChatCompletionResponse> response = AIChatApi.createChatCompletion(request);

            if (response.isSuccessful()) {
                AIChatApi.ChatCompletionResponse data = response.getData();

                // 5. 获取模型返回的消息
                if (data.getChoices() != null && !data.getChoices().isEmpty()) {
                    List<AIChatApi.Message> replyMessages = data.getChoices().get(0).getMessage();
                    for (AIChatApi.Message msg : replyMessages) {
                        System.out.println(msg.getRole() + ": " + msg.getContent());
                    }
                }

                // 6. 打印 Token 使用情况
                AIChatApi.Usage usage = data.getUsage();
                System.out.println("Tokens used: " + usage.getTotalTokens());
            } else {
                System.err.println("Chat failed: " + response.getMessage());
            }
        } catch (Exception e) {
            System.err.println("Error occurred: " + e.getMessage());
        }
    }
}