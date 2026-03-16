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
    private static String host = "https://idp-sea.6estates.com/customer";
    private static String submitUrl = host + "/extraction/fields/async";
    private static String historyListUrl = host + "/extraction/history/list";
    private static String toHitlUrl = host + "/extraction/task/to_hitl";
    private static String extractUrl = host + "/extraction/field/async/result/";
    private static String oauthUrl = "https://oauth-sea.6estates.com/oauth/token?grant_type=client_bind";
    private static String oauthSafeUrl = "https://oauth-sea.6estates.com/api/token";
    private static String lang = System.getenv("IDP_LANG");
    private static volatile IdpRestClient restClient;
    private static String faasAnalysisUrl = host + "/extraction/faas/analysis";

    private static String faasAnalysisStatusUrl = host + "/extraction/faas/analysis/status";

    private static String faasAnalysisExportUrl = host + "/extraction/faas/analysis/export";
    private static String faasAnalysisResultUrl = host + "/extraction/faas/analysis/result/";
    private static String faasAnalysisAdditionUrl = host + "/extraction/faas/analysis/addition";
    private static String splitExtractionUrl = host + "/extraction/split/ext/fields/async";

    private static String splitExtractionStatusUrl = host + "/extraction/split/ext/status";

    private static String splitExtractionDownloadUrl = host + "/extraction/split/ext/download/zip";

    private static String quotaUrl = host + "/extraction/quota";

    private static String documentAgentAnalysisUrl = host + "/extraction/doc_agent/analysis";

    private static String documentAgentStatusUrl = host + "/extraction/doc_agent/status";

    private static String documentAgentExportUrl = host + "/extraction/doc_agent/analysis/export";

    private static String syncCardExtractionUrl = host + "/extraction/fields/sync/cards";

    private Idp() {}

    public static synchronized void setSyncCardExtractionUrl(final String syncCardExtractionUrl) {
        Idp.syncCardExtractionUrl = syncCardExtractionUrl;
    }

    public static synchronized String getSyncCardExtractionUrl() {
        return syncCardExtractionUrl;
    }

    public static synchronized void setDocumentAgentExportUrl(final String documentAgentExportUrl) {
        Idp.documentAgentExportUrl = documentAgentExportUrl;
    }

    public static synchronized String getDocumentAgentExportUrl() {
        return documentAgentExportUrl;
    }

    public static synchronized void setDocumentAgentStatusUrl(final String documentAgentStatusUrl) {
        Idp.documentAgentStatusUrl = documentAgentStatusUrl;
    }

    public static synchronized String getDocumentAgentStatusUrl() {
        return documentAgentStatusUrl;
    }

    public static synchronized void setDocumentAgentAnalysisUrl(final String documentAgentAnalysisUrl) {
        Idp.documentAgentAnalysisUrl = documentAgentAnalysisUrl;
    }

    public static synchronized String getDocumentAgentAnalysisUrl() {
        return documentAgentAnalysisUrl;
    }

    public static synchronized void setQuotaUrl(final String quotaUrl) {
        Idp.quotaUrl = quotaUrl;
    }

    public static synchronized String getQuotaUrl() {
        return quotaUrl;
    }

    public static synchronized void setSplitExtractionDownloadUrl(final String splitExtractionDownloadUrl) {
        Idp.splitExtractionDownloadUrl = splitExtractionDownloadUrl;
    }

    public static synchronized String getSplitExtractionDownloadUrl() {
        return splitExtractionDownloadUrl;
    }

    public static synchronized void setSplitExtractionStatusUrl(final String splitExtractionStatusUrl) {
        Idp.splitExtractionStatusUrl = splitExtractionStatusUrl;
    }

    public static synchronized String getSplitExtractionStatusUrl() {
        return splitExtractionStatusUrl;
    }

    public static synchronized void setSplitExtractionUrl(final String splitExtractionUrl) {
        Idp.splitExtractionUrl = splitExtractionUrl;
    }

    public static synchronized String getSplitExtractionUrl() {
        return splitExtractionUrl;
    }

    public static synchronized void setFaasAnalysisAdditionUrl(final String faasAnalysisAdditionUrl) {
        Idp.faasAnalysisAdditionUrl = faasAnalysisAdditionUrl;
    }

    public static synchronized String getFaasAnalysisAdditionUrl() {
        return faasAnalysisAdditionUrl;
    }

    public static synchronized void setFaasAnalysisResultUrl(final String faasAnalysisResultUrl) {
        Idp.faasAnalysisResultUrl = faasAnalysisResultUrl;
    }

    public static synchronized String getFaasAnalysisResultUrl() {
        return faasAnalysisResultUrl;
    }

    public static synchronized void setFaasAnalysisExportUrl(final String faasAnalysisExportUrl) {
        Idp.faasAnalysisExportUrl = faasAnalysisExportUrl;
    }

    public static synchronized String getFaasAnalysisExportUrl() {
        return faasAnalysisExportUrl;
    }

    public static synchronized void setFaasAnalysisStatusUrl(final String faasAnalysisStatusUrl) {
        Idp.faasAnalysisStatusUrl = faasAnalysisStatusUrl;
    }

    public static synchronized String getFaasAnalysisStatusUrl() {
        return faasAnalysisStatusUrl;
    }

    public static synchronized void setFaasAnalysisUrl(final String faasAnalysisUrl) {
        Idp.faasAnalysisUrl = faasAnalysisUrl;
    }

    public static synchronized String getFaasAnalysisUrl() {
        return faasAnalysisUrl;
    }

    public static synchronized void setToHitlUrl(final String toHitlUrl) {
        Idp.toHitlUrl = toHitlUrl;
    }

    public static synchronized String getToHitlUrl() {
        return toHitlUrl;
    }

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

    public static synchronized String getOauthSafeUrl() {
        return oauthSafeUrl;
    }

    public static synchronized void setOauthSafeUrl(String oauthSafeUrl) {
        Idp.oauthSafeUrl = oauthSafeUrl;
    }

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
        if (!isOauth) {
            return new IdpRestClient(customer, customerParam, token, false);
        } else {
            return new IdpRestClient(customer,customerParam,authorization, true);
        }
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


    public static String getHistoryListUrl() {
        return historyListUrl;
    }
}
