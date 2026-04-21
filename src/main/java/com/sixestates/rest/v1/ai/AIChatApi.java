package com.sixestates.rest.v1.ai;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.fastjson.annotation.JSONField;
import com.sixestates.Idp;
import com.sixestates.exception.ApiConnectionException;
import com.sixestates.exception.ApiException;
import com.sixestates.exception.RestException;
import com.sixestates.http.HttpMethod;
import com.sixestates.http.IdpRestClient;
import com.sixestates.http.Request;
import com.sixestates.http.Response;
import com.sixestates.type.IdpResponse;
import org.apache.http.HttpHeaders;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * AI Chat API Submitter
 */
public class AIChatApi {

    private AIChatApi() {
    }

    /**
     * Create chat completion
     */
    public static IdpResponse<ChatCompletionResponse> createChatCompletion(ChatCompletionRequest request) {
        IdpRestClient client = Idp.getRestClient();
        String url = Idp.getAIChatUrl();

        Request apiRequest = new Request(HttpMethod.POST, url);
        apiRequest.addHeaderParam(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.getMimeType());

        String jsonBody = JSON.toJSONString(request);
        apiRequest.setHttpEntity(new StringEntity(jsonBody, ContentType.APPLICATION_JSON));

        Response response = client.request(apiRequest);
        if (response == null) {
            throw new ApiConnectionException("AI Chat request failed: Unable to connect to server");
        } else if (!IdpRestClient.SUCCESS.test(response.getStatusCode())) {
            RestException restException = RestException.fromJson(response.getStream(), client.getObjectMapper());
            throw new ApiException(restException != null ? restException.getMessage() : "Server Error");
        }

        return JSON.parseObject(response.getContent(), new TypeReference<IdpResponse<ChatCompletionResponse>>() {
        });
    }

    // --- Request Models ---

    public static class ChatCompletionRequest {
        private String model;
        private List<Message> messages = new ArrayList<>();
        private Double temperature;
        @JSONField(name = "top_p")
        private Double topP;
        @JSONField(name = "max_tokens")
        private Integer maxTokens;
        private Double n;
        @JSONField(name = "presence_penalty")
        private Double presencePenalty;
        @JSONField(name = "frequency_penalty")
        private Double frequencyPenalty;
        @JSONField(name = "reasoning_effort")
        private String reasoningEffort;

        public ChatCompletionRequest(String model) {
            this.model = model;
        }

        public void addMessage(String role, String content) {
            this.messages.add(new Message(role, content));
        }

        // Getters and Setters...
        public String getModel() {
            return model;
        }

        public void setModel(String model) {
            this.model = model;
        }

        public List<Message> getMessages() {
            return messages;
        }

        public void setMessages(List<Message> messages) {
            this.messages = messages;
        }

        public Double getTemperature() {
            return temperature;
        }

        public void setTemperature(Double temperature) {
            this.temperature = temperature;
        }

        public Double getTopP() {
            return topP;
        }

        public void setTopP(Double topP) {
            this.topP = topP;
        }

        public Integer getMaxTokens() {
            return maxTokens;
        }

        public void setMaxTokens(Integer maxTokens) {
            this.maxTokens = maxTokens;
        }

        public Double getN() {
            return n;
        }

        public void setN(Double n) {
            this.n = n;
        }

        public Double getPresencePenalty() {
            return presencePenalty;
        }

        public void setPresencePenalty(Double presencePenalty) {
            this.presencePenalty = presencePenalty;
        }

        public Double getFrequencyPenalty() {
            return frequencyPenalty;
        }

        public void setFrequencyPenalty(Double frequencyPenalty) {
            this.frequencyPenalty = frequencyPenalty;
        }

        public String getReasoningEffort() {
            return reasoningEffort;
        }

        public void setReasoningEffort(String reasoningEffort) {
            this.reasoningEffort = reasoningEffort;
        }
    }

    public static class Message {
        private String role; // user, assistant
        private String content;

        public Message() {
        }

        public Message(String role, String content) {
            this.role = role;
            this.content = content;
        }

        public String getRole() {
            return role;
        }

        public void setRole(String role) {
            this.role = role;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }
    }

    // --- Response Models ---

    public static class ChatCompletionResponse {
        private String model;
        private Long created;
        private List<Choice> choices;
        private Usage usage;

        public String getModel() {
            return model;
        }

        public void setModel(String model) {
            this.model = model;
        }

        public List<Choice> getChoices() {
            return choices;
        }

        public void setChoices(List<Choice> choices) {
            this.choices = choices;
        }

        public Usage getUsage() {
            return usage;
        }

        public void setUsage(Usage usage) {
            this.usage = usage;
        }

        public Long getCreated() {
            return created;
        }

        public void setCreated(Long created) {
            this.created = created;
        }
    }

    public static class Choice {
        private Integer index;
        private List<Message> message; // 接口返回的是 List<Message>
        @JSONField(name = "finish_reason")
        private String finishReason;

        public Integer getIndex() {
            return index;
        }

        public void setIndex(Integer index) {
            this.index = index;
        }

        public List<Message> getMessage() {
            return message;
        }

        public void setMessage(List<Message> message) {
            this.message = message;
        }

        public String getFinishReason() {
            return finishReason;
        }

        public void setFinishReason(String finishReason) {
            this.finishReason = finishReason;
        }
    }

    public static class Usage {
        @JSONField(name = "prompt_tokens")
        private Integer promptTokens;
        @JSONField(name = "completion_tokens")
        private Integer completionTokens;
        @JSONField(name = "total_tokens")
        private Integer totalTokens;

        public void setPromptTokens(Integer promptTokens) {
            this.promptTokens = promptTokens;
        }

        public void setCompletionTokens(Integer completionTokens) {
            this.completionTokens = completionTokens;
        }

        public void setTotalTokens(Integer totalTokens) {
            this.totalTokens = totalTokens;
        }

        public Integer getPromptTokens() {
            return promptTokens;
        }

        public Integer getCompletionTokens() {
            return completionTokens;
        }

        public Integer getTotalTokens() {
            return totalTokens;
        }
    }
}
