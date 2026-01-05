package com.sixestates.type;

/**
 * @author yec
 * @description
 * @Data 2025/12/30
 */
/**
 * 通用的API响应类，支持泛型数据
 *
 * @param <T> 数据类型
 */
public class IdpResponse<T> {
    private Integer status;
    private Integer errorCode;
    private String message;
    private T data;

    // Constructors
    public IdpResponse() {}

    public IdpResponse(Integer status, Integer errorCode, String message, T data) {
        this.status = status;
        this.errorCode = errorCode;
        this.message = message;
        this.data = data;
    }

    // Getters and Setters
    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(Integer errorCode) {
        this.errorCode = errorCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    /**
     * Check if the response is successful.
     *
     * @return true if successful, false otherwise
     */
    public boolean isSuccessful() {
        return status != null && status == 200 ;
    }

    @Override
    public String toString() {
        return "IdpResponse{" +
            "status=" + status +
            ", errorCode=" + errorCode +
            ", message='" + message + '\'' +
            ", data=" + data +
            '}';
    }
}