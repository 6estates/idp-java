package com.sixestates.http;

public enum HttpMethod {
    GET("GET"),
    POST("POST"),
    PUT("PUT"),
    DELETE("DELETE"),
    HEAD("HEAD"),
    OPTIONS("OPTIONS");

    private final String method;

    HttpMethod(final String method) {
        this.method = method;
    }

    public String toString() {
        return method;
    }
}

