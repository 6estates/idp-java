package com.sixestates;


import com.sixestates.exception.AuthenticationException;
import com.sixestates.http.IdpRestClient;



/**
 * Singleton class to initialize Idp environment.
 */
public class Idp {

    public static final String VERSION = "8.0.8";
    public static final String JAVA_VERSION = System.getProperty("java.version");

    private static String customer = System.getenv("IDP_CUSTOMER");
    private static String customerParam = System.getenv("IDP_CUSTOMERPARAM");
    private static String token; // customer used if this is null
    private static String authorization; //oauth2.0 authorization token
    private static boolean isOauth; //oauth2.0 authorization token
    private static String submitUrl = "https://idp-sea.6estates.com/customer/extraction/fields/async";
    private static String extractUrl = "https://idp-sea.6estates.com/customer/extraction/field/async/result/";
    private static String oauthUrl = "https://oauth-sea.6estates.com/oauth/token?grant_type=client_bind";
    private static String lang = System.getenv("IDP_LANG");
    private static volatile IdpRestClient restClient;


    private Idp() {}



    /**
     * Initialize the Idp environment.
     *
     * @param customer   account to use
     * @param customerParam   auth token for the account
     * @param token account sid to use
     */
    public static synchronized void init(final String customer, final String customerParam, final String token) {
        Idp.setCustomer(customer);
        Idp.setCustomerParam(customerParam);
        Idp.setToken(token);
    }

    /**
     * Initialize the Idp environment.
     *

     * @param token account sid to use
     */
    public static synchronized void init( final String token) {
        Idp.setToken(token);
        Idp.isOauth = false;
    }

    /**
     * Initialize the Idp environment.
     *

     * @param authorization account sid to use
     */
    public static synchronized void initAuthorization( final String authorization) {
        Idp.setAuthorization(authorization);
        Idp.isOauth = true;
    }


    /**
     * Set the customer.
     *
     * @param customer account to use
     * @throws AuthenticationException if customer is null
     */
    public static synchronized void setCustomer(final String customer) {
        if (customer == null) {
            throw new AuthenticationException("customer can not be null");
        }

        if (!customer.equals(Idp.customer)) {
            Idp.invalidate();
        }

        Idp.customer = customer;
    }

    /**
     * Set the account sid.
     *
     * @param token account sid to use
     * @throws AuthenticationException if account sid is null
     */
    public static synchronized void setToken(final String token) {
        if (token == null) {
            throw new AuthenticationException("Token can not be null");
        }

        if (!token.equals(Idp.token)) {
            Idp.invalidate();
        }

        Idp.token = token;
    }

    /**
     * Set the account sid.
     *
     * @param authorization account sid to use
     * @throws AuthenticationException if account sid is null
     */
    public static synchronized void setAuthorization(final String authorization) {
        if (authorization == null) {
            throw new AuthenticationException("Authorization can not be null");
        }

        if (!authorization.equals(Idp.authorization)) {
            Idp.invalidate();
        }

        Idp.authorization = authorization;
    }

    /**
     * Set the CustomerParam.
     *
     * @param customerParam to use
     * @throws AuthenticationException if customerParam is null
     */
    public static synchronized void setCustomerParam(final String customerParam) {
        if (customerParam == null) {
            throw new AuthenticationException("customerParam can not be null");
        }

        if (!customerParam.equals(Idp.customerParam)) {
            Idp.invalidate();
        }

        Idp.customerParam = customerParam;
    }

    /**
     * Set the lang.
     *
     * @param lang Lang to make request
     */
    public static synchronized void setLang(final String lang) {
        Idp.lang = lang;
    }

    public static synchronized void setSubmitUrl(final String submitUrl) {
        Idp.submitUrl = submitUrl;
    }

    public static synchronized void setExtractUrl(final String extractUrl) {
        Idp.extractUrl = extractUrl;
    }

    public static synchronized void setOauthUrl(final String oauthUrl) {
        Idp.oauthUrl = oauthUrl;
    }

    public static synchronized String getToken() {return token;}

    public static synchronized String getSubmitUrl() {return submitUrl;}

    public static synchronized String getExtractUrl() {return extractUrl;}

    public static synchronized String getOauthUrl() {return oauthUrl;}

    public static synchronized String getCustomer() {return customer;}

    public static synchronized String getCustomerParam() {return customerParam;}


    /**
     * Returns (and initializes if not initialized) the Idp Rest Client.
     *
     * @return the Idp Rest Client
     * @throws AuthenticationException if initialization required and either token or authToken is null
     */
    public static IdpRestClient getRestClient() {
        if (Idp.restClient == null) {
            synchronized (Idp.class) {
                if (Idp.restClient == null) {
                    Idp.restClient = buildRestClient();
                }
            }
        }

        return Idp.restClient;
    }

    private static IdpRestClient buildRestClient() {
        if (Idp.token == null && Idp.authorization == null) {
            throw new AuthenticationException(
                    "IdpRestClient was used before token and AuthToken were set, please call Idp.init() or Idp.initAuthorization."
            );
        }
        if(!isOauth)
        return new IdpRestClient(customer,customerParam,token, false);
        else
            return new IdpRestClient(customer,customerParam,authorization, true);
    }

    /**
     * Use a custom rest client.
     *
     * @param restClient rest client to use
     */
    public static void setRestClient(final IdpRestClient restClient) {
        synchronized (Idp.class) {
            Idp.restClient = restClient;
        }
    }


    /**
     * Invalidates the volatile state held in the Idp singleton.
     */
    private static void invalidate() {
        Idp.restClient = null;
    }


}
