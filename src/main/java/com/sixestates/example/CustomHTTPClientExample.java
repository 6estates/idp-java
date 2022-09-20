package com.sixestates.example;

import com.sixestates.Idp;
import com.sixestates.http.IdpRestClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.config.SocketConfig;

public class CustomHTTPClientExample {
    public static final String TOKEN = "XXXXXXX";
    public static final String FILE_NAME = "acount_statement_mandiri.pdf";
    public static final String FILE_PATH = "/home//lay/Documents/acount_statement_mandiri.pdf" ;
    public static final String FILE_TYPE = "CBKS";
    private static String customer = System.getenv("IDP_CUSTOMER");
    private static String customerParam = System.getenv("IDP_CUSTOMERPARAM");

    public static void main(String[] args) {
        Idp.init(TOKEN);
        // Custom http client conf
        final int CONNECTION_TIMEOUT = 10000;
        final int SOCKET_TIMEOUT = 30500;
        RequestConfig requestConfig = RequestConfig
                .custom()
                .setConnectTimeout(CONNECTION_TIMEOUT)
                .setSocketTimeout(SOCKET_TIMEOUT)
                .build();

        SocketConfig socketConfig = SocketConfig
                .custom()
                .setSoTimeout(SOCKET_TIMEOUT)
                .build();

        IdpRestClient idpRestClient = new IdpRestClient(customer,customerParam,TOKEN, false, requestConfig, socketConfig);
        Idp.setRestClient(idpRestClient);
        // Do submit or request result
    }
}
