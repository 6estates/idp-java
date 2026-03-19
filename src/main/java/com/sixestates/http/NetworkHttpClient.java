package com.sixestates.http;

import com.alibaba.fastjson.JSON;
import com.sixestates.exception.ApiException;
import com.sixestates.type.FileInfo;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.client.utils.HttpClientUtils;
import org.apache.http.config.SocketConfig;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class NetworkHttpClient extends HttpClient {
    private static final Logger logger = LoggerFactory.getLogger(NetworkHttpClient.class);
    private final org.apache.http.client.HttpClient client;

    /**
     * Create a new HTTP Client.
     */
    public NetworkHttpClient() {
        this(DEFAULT_REQUEST_CONFIG);
    }

    /**
     * Create a new HTTP Client with a custom request config.
     *
     * @param requestConfig a RequestConfig.
     */
    public NetworkHttpClient(final RequestConfig requestConfig) {
        this(requestConfig, DEFAULT_SOCKET_CONFIG);
    }

    /**
     * Create a new HTTP Client with a custom request and socket config.
     *
     * @param requestConfig a RequestConfig.
     * @param socketConfig  a SocketConfig.
     */
    public NetworkHttpClient(final RequestConfig requestConfig, final SocketConfig socketConfig) {
        Collection<BasicHeader> headers = Arrays.asList(
                new BasicHeader(HttpHeaders.ACCEPT, "application/json"),
                new BasicHeader(HttpHeaders.ACCEPT_ENCODING, "utf-8")
        );


        org.apache.http.impl.client.HttpClientBuilder clientBuilder = HttpClientBuilder.create();
        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
        connectionManager.setDefaultSocketConfig(socketConfig);
        connectionManager.setDefaultMaxPerRoute(10);
        connectionManager.setMaxTotal(10 * 2);

        client = clientBuilder
                .setConnectionManager(connectionManager)
                .setDefaultRequestConfig(requestConfig)
                .setDefaultHeaders(headers)
                .setRedirectStrategy(this.getRedirectStrategy())
                .build();
    }


    /**
     * Create a new HTTP Client using custom configuration.
     * @param clientBuilder an HttpClientBuilder.
     */
    public NetworkHttpClient(HttpClientBuilder clientBuilder) {
        Collection<BasicHeader> headers = Arrays.asList(
                new BasicHeader(HttpHeaders.ACCEPT, "*/*")
        );

        client = clientBuilder
                .setDefaultHeaders(headers)
                .setRedirectStrategy(this.getRedirectStrategy())
                .build();
    }

    /**
     * Make a request.
     *
     * @param request request to make
     * @return Response of the HTTP request
     */
    @Override
    public Response makeRequest(final Request request) {
        HttpMethod method = request.getMethod();
        RequestBuilder builder = RequestBuilder.create(method.toString())
            .setUri(request.constructURL().toString());

        // 添加认证头
        if (request.requiresAuthentication()) {
            String token = request.getToken();
            if (token != null && !token.isEmpty()) {
                if (request.getIsOauth()) {
                    builder.addHeader("Authorization", token);
                } else {
                    builder.addHeader("X-ACCESS-TOKEN", token);
                }
            }
        }

        // 添加自定义请求头
        Map<String, List<String>> headerParams = request.getHeaderParams();
        if (headerParams != null) {
            for (Map.Entry<String, List<String>> entry : headerParams.entrySet()) {
                String headerName = entry.getKey();
                List<String> headerValues = entry.getValue();
                if (headerName != null && headerValues != null) {
                    for (String value : headerValues) {
                        if (value != null) {
                            builder.addHeader(headerName, value);
                        }
                    }
                }
            }
        }

        // 处理请求体
        if (method == HttpMethod.POST) {
            // 处理不同的 Content-Type
            List<String> contentType = headerParams.get(HttpHeaders.CONTENT_TYPE);
            if (contentType != null && contentType.contains("application/json")) {
                // JSON 请求
                handleJsonBody(builder, request);
            } else {
                // 默认使用 form-urlencoded
                handleFormUrlEncodedBody(builder, request);
            }
        }

        HttpResponse response = null;

        try {
            response = client.execute(builder.build());
            HttpEntity entity = response.getEntity();

            // 安全地获取响应内容
            InputStream contentStream = null;
            if (entity != null) {
                try {
                    contentStream = new BufferedHttpEntity(entity).getContent();
                } catch (IOException e) {
                    // 如果无法获取内容流，使用空流但记录状态码
                    contentStream = new ByteArrayInputStream(new byte[0]);
                }
            }

            return new Response(
                contentStream,
                response.getStatusLine().getStatusCode(),
                response.getAllHeaders()
            );
        } catch (IOException e) {
            throw new ApiException("Request failed: " + e.getMessage(), e);
        } finally {
            // 确保响应被正确关闭
            HttpClientUtils.closeQuietly(response);
        }
    }

    /**
     * 处理 JSON 请求体
     */
    private void handleJsonBody(RequestBuilder builder, Request request) {
        // 从 postParams 转换为 JSON
        Map<String, List<String>> postParams = request.getPostParams();
        if (postParams != null && !postParams.isEmpty()) {
            try {
                Map<String, Object> jsonParams = new HashMap<>();
                for (Map.Entry<String, List<String>> entry : postParams.entrySet()) {
                    String key = entry.getKey();
                    List<String> values = entry.getValue();
                    if (key != null && values != null && !values.isEmpty()) {
                        // 只取第一个值，因为 curl 示例是单值
                        jsonParams.put(key, values.get(0));
                    }
                }

                String jsonBody = JSON.toJSONString(jsonParams);
                builder.setEntity(new StringEntity(jsonBody, StandardCharsets.UTF_8));
            } catch (Exception e) {
                throw new ApiException("Failed to create JSON request body", e);
            }
        }
    }

    /**
     * 处理表单编码请求体
     */
    private void handleFormUrlEncodedBody(RequestBuilder builder, Request request) {
        Map<String, List<String>> postParams = request.getPostParams();
        if (postParams != null && !postParams.isEmpty()) {
            List<NameValuePair> params = new ArrayList<>();
            for (Map.Entry<String, List<String>> entry : postParams.entrySet()) {
                String key = entry.getKey();
                List<String> values = entry.getValue();
                if (key != null && values != null) {
                    for (String value : values) {
                        if (value != null) {
                            params.add(new BasicNameValuePair(key, value));
                        }
                    }
                }
            }
            if (!params.isEmpty()) {
                builder.setEntity(new UrlEncodedFormEntity(params, StandardCharsets.UTF_8));
            }
        }
    }


    /**
     * Make a Idp Submit file post request.
     *
     * @param request request to make
     * @return Response of the HTTP request
     */
    @Override
    public Response makeSubmitRequest(final Request request) {

        // Construct Http body
        HttpPost httpPost = new HttpPost(request.getUrl());
        HttpEntity entity = null;
        if (request.getHttpEntity() != null) {
            entity = request.getHttpEntity();
            httpPost.setEntity(entity);
        } else {
            MultipartEntityBuilder builder = MultipartEntityBuilder.create();
            builder.setCharset(java.nio.charset.Charset.forName("UTF-8"));
            builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
            Map<String, List<FileInfo>> fileInfoMap = request.getFileInfoMap();
            if (fileInfoMap != null) {
                for (Map.Entry<String, List<FileInfo>> entry : fileInfoMap.entrySet()) {
                    for (FileInfo fileInfo : entry.getValue()) {
                        builder.addBinaryBody(entry.getKey(), fileInfo.getInputStream(),
                            ContentType.MULTIPART_FORM_DATA, fileInfo.getFileName());
                    }
                }
            } else if (request.getPostParams().get("fileName") != null) {
                String fileName = request.getPostParams().get("fileName").get(0);
                if (request.getPostParams().containsKey("filePath")) {
                    String filePath = request.getPostParams().get("filePath").get(0);
                    logger.debug("filePath: " + filePath);
                    File file = new File(filePath);
                    builder.addBinaryBody("file", file, ContentType.MULTIPART_FORM_DATA, fileName);
                } else if (request.getInputStream() != null) {
                    builder.addBinaryBody("file", request.getInputStream(), ContentType.MULTIPART_FORM_DATA, fileName);
                }
            }
            for (Map.Entry<String, List<String>> entry : request.getPostParams().entrySet()) {
                for (String value : entry.getValue()) {
                    builder.addTextBody(entry.getKey(), value);
                }
            }
            entity = builder.build();
            httpPost.setEntity(entity);
        }

        if(request.getIsOauth()) {
            httpPost.addHeader("Authorization", request.getToken());
        }else {
            httpPost.addHeader("X-ACCESS-TOKEN", request.getToken());
        }

        HttpResponse response = null;
        try {
            response = client.execute(httpPost);
            HttpEntity respEntity = response.getEntity();
            return new Response(
                // Consume the entire HTTP response before returning the stream
                entity == null ? null : new BufferedHttpEntity(respEntity).getContent(),
                response.getStatusLine().getStatusCode(),
                response.getAllHeaders()
            );
        } catch (IOException e) {
            throw new ApiException(e.getMessage(), e);
        } finally {
            // Ensure this response is properly closed
            HttpClientUtils.closeQuietly(response);
        }
    }
}
