package com.sixestates;


import com.sixestates.exception.AuthenticationException;
import com.sixestates.http.IdpRestClient;



/**
 * Singleton class to initialize Idp environment.
 */
public class Idp {

    public static final String VERSION = "8.29.0";
    public static final String JAVA_VERSION = System.getProperty("java.version");

    private static String customer = System.getenv("IDP_CUSTOMER");
    private static String customerParam = System.getenv("IDP_CUSTOMERPARAM");
    private static String token; // customer used if this is null
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
    public static synchronized String getToken() {return token;}

    public static synchronized String getCustomer() {return customer;}

    public static synchronized String getCustomerParam() {return customerParam;}

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
     * Set the auth token.
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
        if (Idp.token == null) {
            throw new AuthenticationException(
                    "IdpRestClient was used before token and AuthToken were set, please call Idp.init()"
            );
        }

        return new IdpRestClient(customer,customerParam,token);
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
