package com.sixestates.type;

/**
 * @author kechen, 19/08/24.
 */
public class CreateTokenVO {
    private String clientId;

    private Long timestamp;

    /**
     * clientId+clientSecret+timestamp  using SHA 256 Hash
     */
    private String signature;

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }
}
