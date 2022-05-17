package com.sixestates.http;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Predicate;
import java.util.Map;
import java.util.List;

public class IdpRestClient {

    public static final int HTTP_STATUS_CODE_CREATED = 201;
    public static final int HTTP_STATUS_CODE_NO_CONTENT = 204;
    public static final int HTTP_STATUS_CODE_OK = 200;
    public static final Predicate<Integer> SUCCESS = i -> i != null && i >= 200 && i < 400;

    private final ObjectMapper objectMapper;
    private final String customer;
    private final String customerParam;
    private final String token;
    private final HttpClient httpClient;
    private static final Logger logger = LoggerFactory.getLogger(IdpRestClient.class);

    public IdpRestClient(final String customer, final String customerParam, final String token) {
        this.customer = customer;
        this.customerParam = customerParam;
        this.token = token;
        this.objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        this.httpClient = new NetworkHttpClient();
    }

    /**
     * Make a request to Idp.
     *
     * @param request request to make
     * @return Response object
     */
    public Response request(final Request request) {
        request.setAuth(token);
        logRequest(request);
        Response response = httpClient.reliableRequest(request);

        if (logger.isInfoEnabled()) {
            logger.info("status code: {}", response.getStatusCode());
            org.apache.http.Header[] responseHeaders = response.getHeaders();
            logger.info("response headers:");
            for (int i = 0; i < responseHeaders.length; i++) {
                logger.info("responseHeader: {}", responseHeaders[i]);
            }
        }

        return response;
    }

    public String getToken() {
        return token;
    }

    public String getCustomer() {
        return customer;
    }

    public String getCustomerParam() {
        return customerParam;
    }

    public ObjectMapper getObjectMapper() {
        return objectMapper;
    }

    public HttpClient getHttpClient() {
        return httpClient;
    }



    /**
     * Logging debug information about HTTP request.
     */
    public void logRequest(final Request request) {
        if (logger.isInfoEnabled()) {
            logger.info("-- BEGIN Idp API Request --");
            logger.info("request method: " + request.getMethod());
            logger.info("request URL: " + request.constructURL().toString());
            final Map<String, List<String>> headerParams = request.getHeaderParams();


            if (headerParams != null && !headerParams.isEmpty()) {
                logger.info("header parameters: ");
                for (String key : headerParams.keySet()) {
                    if (!key.toLowerCase().contains("authorization")) {
                        logger.info(key + ": " + headerParams.get(key));
                    }
                }
            }

            logger.info("-- END Idp API Request --");
        }
    }

}

