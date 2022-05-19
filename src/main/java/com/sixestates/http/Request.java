package com.sixestates.http;

import com.sixestates.exception.ApiException;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;

public class Request {

    private static final String DEFAULT_REGION = "EN";
    private final HttpMethod method;
    private final String url;
    private final Map<String, List<String>> postParams;
    private final Map<String, List<String>> headerParams;
    private  InputStream inputStream;
    private boolean isSubmit = false;
    private  String token;



    /**
     * Create a new API request.
     *
     * @param method HTTP method
     * @param url    url of request
     */
    public Request(final HttpMethod method, final String url, InputStream inputStream) {
        this.method = method;
        this.url = url;
        this.inputStream = inputStream;
        this.postParams = new HashMap<>();
        this.headerParams = new HashMap<>();
    }

    public Request(final HttpMethod method, final String url) {
        this.method = method;
        this.url = url;
        this.postParams = new HashMap<>();
        this.headerParams = new HashMap<>();
    }

    public HttpMethod getMethod() {
        return this.method;
    }

    public String getUrl() {
        return this.url;
    }

    public InputStream getInputStream() {
        return this.inputStream;
    }

    public String getToken() {
        return this.token;
    }

    public boolean getIsSubmit() {
        return this.isSubmit;
    }

    public void setIsSubmit(boolean isSubmit) {
        this.isSubmit = isSubmit;
    }

    public void setAuth(final String token) {
        this.token = token;
    }

    public boolean requiresAuthentication() {
        return token != null ;
    }

    /**
     * Build the URL for the request.
     *
     * @return URL for the request
     */
    public URL constructURL() {
        String stringUri = buildURL();
        try {
            URI uri = new URI(stringUri);
            return uri.toURL();
        } catch (final URISyntaxException e) {
            throw new ApiException("Bad URI: " + e.getMessage());
        } catch (final MalformedURLException e) {
            throw new ApiException("Bad URL: " + e.getMessage());
        }
    }

    private String buildURL() {
        try {
            final URL parsedUrl = new URL(url);
            String host = parsedUrl.getHost();
            //todo check URL
            return url;
        } catch (final MalformedURLException e) {
            throw new ApiException("Bad URL: " + e.getMessage());
        }
    }


    /**
     * Add a form parameter.
     *
     * @param name  name of parameter
     * @param value value of parameter
     */
    public void addPostParam(final String name, final String value) {
        addParam(postParams, name, value);
    }

    /**
     * Add a header parameter.
     *
     * @param name  name of parameter
     * @param value value of parameter
     */
    public void addHeaderParam(final String name, final String value) {
        addParam(headerParams, name, value);
    }

    private void addParam(final Map<String, List<String>> params, final String name, final String value) {
        if (!params.containsKey(name)) {
            params.put(name, new ArrayList<String>());
        }

        params.get(name).add(value);
    }


    public Map<String, List<String>> getPostParams() {
        return postParams;
    }

    public Map<String, List<String>> getHeaderParams() {
        return headerParams;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Request other = (Request) o;
        return Objects.equals(this.method, other.method) &&
                Objects.equals(this.buildURL(), other.buildURL()) &&
                Objects.equals(this.postParams, other.postParams) &&
                Objects.equals(this.headerParams, other.headerParams);
    }
}
