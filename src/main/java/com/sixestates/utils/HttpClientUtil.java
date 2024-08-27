package com.sixestates.utils;

import com.sixestates.exception.ApiException;
import com.sixestates.http.IdpRestClient;
import org.apache.commons.io.Charsets;
import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HeaderElementIterator;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.utils.HttpClientUtils;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicHeaderElementIterator;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author kechen, 23/08/24.
 */
public class HttpClientUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(HttpClientUtil.class);
    private static CloseableHttpClient httpClient;

    static {
        httpClient = createHttpClient(20, 300);
    }

    public static CloseableHttpClient createHttpClient(int maxPerRoute, int timeout) {
        PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
        cm.setDefaultMaxPerRoute(maxPerRoute);

        final RequestConfig requestConfig = RequestConfig.custom()
                .setSocketTimeout(timeout * 1000)
                .setConnectTimeout(timeout * 1000)
                .setConnectionRequestTimeout(timeout * 1000)
                .build();

        ConnectionKeepAliveStrategy connectionKeepAliveStrategy = (HttpResponse response, HttpContext context) -> {
            HeaderElementIterator it = new BasicHeaderElementIterator(response.headerIterator(HTTP.CONN_KEEP_ALIVE));
            while (it.hasNext()) {
                HeaderElement he = it.nextElement();
                String param = he.getName();
                String value = he.getValue();
                if (value != null && "timeout".equalsIgnoreCase(param)) {
                    try {
                        return Long.parseLong(value) * 1000;
                    } catch (final NumberFormatException ignore) {
                        throw new ApiException(ignore.getMessage(), ignore);
                    }
                }
            }
            return 300 * 1000;
        };
        return HttpClients.custom().setConnectionManager(cm)
                .evictExpiredConnections()
                .evictIdleConnections(180, TimeUnit.SECONDS)
                .setKeepAliveStrategy(connectionKeepAliveStrategy)
                .setDefaultRequestConfig(requestConfig).build();
    }


    public static String postStr(String url,
                                 Map<String, String> header,
                                 String paramBody) {
        HttpPost httpPost = new HttpPost(url);
        for (Map.Entry<String, String> entry : header.entrySet()) {
            httpPost.setHeader(entry.getKey(), entry.getValue());
        }

        httpPost.setEntity(new StringEntity(paramBody, ContentType.APPLICATION_JSON));
        return getResponse(httpPost);
    }


    private static void setCustomTimeoutConfig(Integer timeoutSec, HttpRequestBase request) {
        if (timeoutSec != null && timeoutSec > 0) {
            final int timeoutMills = timeoutSec * 1000;
            RequestConfig requestConfig = RequestConfig.custom()
                    .setConnectionRequestTimeout(timeoutMills)
                    .setSocketTimeout(timeoutMills)
                    .setConnectTimeout(timeoutMills)
                    .build();
            request.setConfig(requestConfig);
        }
    }


    public static String getResponse(HttpRequestBase request) {
        HttpResponse response = null;
        try {
            response = httpClient.execute(request);
            InputStream content = response.getEntity().getContent();
            String responseBody = IOUtils.toString(content, Charsets.UTF_8);
            int statusCode = response.getStatusLine().getStatusCode();
            if (IdpRestClient.SUCCESS.test(statusCode)) {
                return responseBody;
            } else {
                LOGGER.error("errorCode:{}, error response:{}, url:{}", statusCode, responseBody, request.getURI());
                for (Header allHeader : response.getAllHeaders()) {
                    LOGGER.error("response header, {}: {}", allHeader.getName(), allHeader.getValue());
                }
                throw new ApiException(responseBody);
            }

        } catch (IOException e) {
            throw new ApiException(e.getMessage(), e);
        } finally {
            HttpClientUtils.closeQuietly(response);
        }
    }
}
