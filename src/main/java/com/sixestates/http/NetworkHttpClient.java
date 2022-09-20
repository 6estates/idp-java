package com.sixestates.http;

import com.sixestates.Idp;
import com.sixestates.exception.ApiException;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.client.utils.HttpClientUtils;
import org.apache.http.config.SocketConfig;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicHeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    public Response makeRequest(final Request request) {

        HttpMethod method = request.getMethod();
        RequestBuilder builder = RequestBuilder.create(method.toString())
                .setUri(request.constructURL().toString());

        if (request.requiresAuthentication()) {
            if(request.getIsOauth()){
                builder.addHeader("Authorization", request.getToken());
            }
            else {
                builder.addHeader("X-ACCESS-TOKEN", request.getToken());
            }
        }

        for (Map.Entry<String, List<String>> entry : request.getHeaderParams().entrySet()) {
            for (String value : entry.getValue()) {
                builder.addHeader(entry.getKey(), value);
            }
        }

        if (method == HttpMethod.POST) {
            builder.addHeader(HttpHeaders.CONTENT_TYPE, "application/x-www-form-urlencoded");

            for (Map.Entry<String, List<String>> entry : request.getPostParams().entrySet()) {
                for (String value : entry.getValue()) {
                    builder.addParameter(entry.getKey(), value);
                }
            }
        }

        HttpResponse response = null;

        try {
            response = client.execute(builder.build());
            HttpEntity entity = response.getEntity();
            return new Response(
                    // Consume the entire HTTP response before returning the stream
                    entity == null ? null : new BufferedHttpEntity(entity).getContent(),
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

    /**
     * Make a Idp Submit file post request.
     *
     * @param request request to make
     * @return Response of the HTTP request
     */
    public Response makeSubmitRequest(final Request request) {

        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        builder.setCharset(java.nio.charset.Charset.forName("UTF-8"));
        builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);

        String fileName = request.getPostParams().get("fileName").get(0);
        String filePath = request.getPostParams().get("filePath").get(0);
        String fileType = request.getPostParams().get("fileType").get(0);
        if(filePath != null) {
            logger.debug("filePath: " + filePath);
            File file = new File(filePath);
            builder.addBinaryBody("file", file, ContentType.MULTIPART_FORM_DATA, fileName);
        }else if(request.getInputStream() != null) {
            builder.addBinaryBody("file", request.getInputStream(), ContentType.MULTIPART_FORM_DATA, fileName);
        }
        builder.addTextBody("fileType", fileType);
        if(request.getPostParams().containsKey("customer")) {
            builder.addTextBody("customer", request.getPostParams().get("customer").get(0));
        }

        if(request.getPostParams().containsKey("customerParam")) {
            builder.addTextBody("customerParam", request.getPostParams().get("customerParam").get(0));
        }

        if(request.getPostParams().containsKey("callback")) {
            String callBackUrl = request.getPostParams().get("callback").get(0);
            String callBackMode = request.getPostParams().get("callbackMode").get(0);
            builder.addTextBody("callback", callBackUrl);
            builder.addTextBody("callbackMode", callBackMode);
        }

        if(request.getPostParams().containsKey("hitl")) {
            builder.addTextBody("hitl", "true");
        }
        
        if(request.getPostParams().containsKey("autoChecks")) {
            builder.addTextBody("autoChecks", request.getPostParams().get("autoChecks").get(0));
        }


        // Construct Http body
        HttpPost httpPost = new HttpPost(request.getUrl());
        HttpEntity entity = builder.build();
        httpPost.setEntity(entity);
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
