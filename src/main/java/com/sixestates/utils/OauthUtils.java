package com.sixestates.utils;

import java.net.URL;
import java.net.URLConnection;
import com.alibaba.fastjson.JSON;
import com.sixestates.Idp;
import com.sixestates.exception.ApiConnectionException;
import com.sixestates.exception.AuthenticationException;
import com.sixestates.type.CreateTokenVO;
import com.sixestates.type.OauthDTO;
import java.io.*;
import java.util.HashMap;

public class OauthUtils {
    public static OauthDTO getIDPAuthorization(String authorizationToken) {
        PrintWriter out = null;
        BufferedReader in = null;
        String result = "";
        try {
            URL realUrl = new URL(Idp.getOauthUrl());
            URLConnection conn = realUrl.openConnection();
            conn.setRequestProperty("Authorization", authorizationToken);
            conn.setDoOutput(true);
            conn.setDoInput(true);
            OutputStreamWriter outWriter = new OutputStreamWriter(conn.getOutputStream(), "utf-8");
            out = new PrintWriter(outWriter);
            out.flush();
            in = new BufferedReader(
                    new InputStreamReader(conn.getInputStream(),"UTF-8"));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            System.out.println("Post request err."+e);
            throw new ApiConnectionException("Authorization failed: Unable to connect to OAuth server");
        }
        finally{
            try{
                if(out!=null){
                    out.close();
                }
                if(in!=null){
                    in.close();
                }
            }
            catch(IOException ex){
                ex.printStackTrace();
            }
        }

        return convert(result);
    }

    public static OauthDTO getIDPAuthorization(String clientId, String clientSecret) {
        CreateTokenVO createTokenVO = new CreateTokenVO();
        createTokenVO.setClientId(clientId);
        long timestamp = System.currentTimeMillis();
        createTokenVO.setTimestamp(timestamp);
        createTokenVO.setSignature(generateSignature(clientId, clientSecret, timestamp));

        String result = HttpClientUtil.postStr(Idp.getOauthSafeUrl(), new HashMap<>(), JSON.toJSONString(createTokenVO));
        return convert(result);
    }

    private static OauthDTO convert(String result) {
        OauthDTO oauthDTO = null;
        try {
            oauthDTO = JSON.parseObject(result, OauthDTO.class);
        }catch (Exception e) {
            System.out.println("Response err."+e);
            throw new ApiConnectionException("The IDP Authorization is error, please re-send the request to get new IDP Authorization.");
        }

        if(oauthDTO == null) {
            throw new ApiConnectionException("The IDP Authorization is error, please re-send the request to get new IDP Authorization.");
        }

        if(oauthDTO.getData().isExpired()) {
            throw new AuthenticationException("This IDP Authorization is expired, please re-send the request to get new IDP Authorization.");
        }
        return oauthDTO;
    }

    private static String generateSignature(String clientId, String clientSecret, Long timestamp) {
        try {
            return EncryptUtil.buildSHA256Str(clientId + clientSecret + timestamp);
        } catch (Exception e) {
            throw new RuntimeException("build encrypt signature error", e);
        }
    }

}
