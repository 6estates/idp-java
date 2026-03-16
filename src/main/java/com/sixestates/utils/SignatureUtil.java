package com.sixestates.utils;

import com.sixestates.exception.SignatureVerificationException;

/**
 * @author kechen, 27/09/24.
 */
public class SignatureUtil {

    /**
     * verify APP task callback mode 0 or 1 signature
     *
     * <p>
     * Modes 2 can use method {@link #verifyAppHeaderForMode2} and Mode3 can use method {@link #verifyAppHeaderForMode3},
     * you can also use this method, but should organize payload content according to the API documentation.
     * </p>
     *
     * @param payload            the original request body
     * @param sigHeaderSignature IDP sign header
     * @param secret             webhook secret
     */
    public static void verifyAppHeader(byte[] payload, String sigHeaderSignature, String secret) {
        String expectedSignature = null;
        try {
            expectedSignature = EncryptUtil.computeHmacSha256(secret, payload);
        } catch (Exception e) {
            throw new SignatureVerificationException(
                    "Unable to compute signature for payload", sigHeaderSignature);
        }
        if (!StringUtils.equals(expectedSignature, sigHeaderSignature)) {
            throw new SignatureVerificationException(
                    "Signature found not match the expected signature for payload", sigHeaderSignature);
        }
    }

    /**
     * verify APP task callback mode 2 signature
     *
     * @param resultBytes        extraction result content bytes
     * @param fileBytes          upload file bytes
     * @param sigHeaderSignature IDP sign header
     * @param secret             webhook secret
     */
    public static void verifyAppHeaderForMode2(byte[] resultBytes, byte[] fileBytes, String sigHeaderSignature, String secret) {
        byte[] combinedArray = new byte[resultBytes.length + fileBytes.length];
        System.arraycopy(resultBytes, 0, combinedArray, 0, resultBytes.length);
        System.arraycopy(fileBytes, 0, combinedArray, resultBytes.length, fileBytes.length);
        verifyAppHeader(combinedArray, sigHeaderSignature, secret);
    }

    /**
     * verify APP task callback mode 3 signature
     *
     * @param resultBytes        extraction json result content bytes
     * @param fileBytes          upload file bytes
     * @param resultInExcelBytes named resultInExcel in callback parameters : fields in an Excel format, like the export Excel file in IDP system task result page
     * @param resultInJsonBytes  named resultInJson in callback parameters : fields in a json format, like the export json file in IDP system task result page
     * @param sigHeaderSignature IDP sign header
     * @param secret             webhook secret
     */
    public static void verifyAppHeaderForMode3(byte[] resultBytes, byte[] fileBytes,
                                               byte[] resultInExcelBytes, byte[] resultInJsonBytes,
                                               String sigHeaderSignature, String secret) {
        byte[] combinedArray = new byte[resultBytes.length + fileBytes.length
                + resultInExcelBytes.length + resultInJsonBytes.length];
        System.arraycopy(resultBytes, 0, combinedArray, 0, resultBytes.length);
        System.arraycopy(fileBytes, 0, combinedArray, resultBytes.length, fileBytes.length);
        System.arraycopy(resultInExcelBytes, 0, combinedArray,
                resultBytes.length + fileBytes.length, resultInExcelBytes.length);
        System.arraycopy(resultInJsonBytes, 0, combinedArray,
                resultBytes.length + fileBytes.length + resultInExcelBytes.length, resultInJsonBytes.length);
        verifyAppHeader(combinedArray, sigHeaderSignature, secret);
    }

}
