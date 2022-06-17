package com.sixestates.http;

import com.alibaba.fastjson.JSONObject;
import com.sixestates.utils.FileStoreUtils;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.*;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;


public class CallBackHttpHandler implements HttpHandler {
    private static final Logger logger = LoggerFactory.getLogger(CallBackHttpHandler.class);

    private ConcurrentHashMap<String, String> jsonMap;
    private ConcurrentHashMap<String, byte[]> fileMap;

    /**
     * Create a new CallBackHttpHandler.
     *
     * @param jsonMap key is the taskId, value is the callback json string
     * @param fileMap key is the file name, value is the file bytes
     */
    public CallBackHttpHandler(ConcurrentHashMap<String, String> jsonMap, ConcurrentHashMap<String, byte[]> fileMap) {
        this.jsonMap = jsonMap;
        this.fileMap = fileMap;
    }

    @Override
    public void handle(HttpExchange httpExchange){
        logger.debug("Receive a callback request!");
        try {
            Headers headers = httpExchange.getRequestHeaders();
            String contentTypeStr = headers.get("Content-type").get(0);
            logger.debug(contentTypeStr);
            boolean modeType =contentTypeStr.contains("application/json");
            if (modeType) {
                jsonCallBackExtract(httpExchange);
            }else{
                formDataCallBackExtract(httpExchange);
            }
            handleResponse(httpExchange);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Handle response
     *
     * @param httpExchange
     * @throws Exception
     */
    private void handleResponse(HttpExchange httpExchange) throws Exception {
        StringBuilder responseContent = new StringBuilder();
        responseContent.append("<html>")
                .append("<body>")
                .append("done")
                .append("</body>")
                .append("</html>");
        String responseContentStr = responseContent.toString();

        byte[] responseContentByte = responseContentStr.getBytes("utf-8");
        httpExchange.getResponseHeaders().add("Content-Type:", "text/html;charset=utf-8");
        httpExchange.sendResponseHeaders(200, responseContentByte.length);
        OutputStream out = httpExchange.getResponseBody();
        out.write(responseContentByte);
        out.flush();
        out.close();
    }

    /**
     * Handle mode0 or mode1 callback request
     *
     * @param httpExchange
     * @throws Exception
     */
    private void jsonCallBackExtract(HttpExchange httpExchange) throws Exception {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(httpExchange.getRequestBody(), "utf-8"));
        StringBuilder requestBodyContent = new StringBuilder();
        String line = null;

        while ((line = bufferedReader.readLine()) != null) {
            requestBodyContent.append(line);
        }
        String jsonBodyStr = requestBodyContent.toString();
        if(this.jsonMap != null) {
            String taskId = JSONObject.parseObject(jsonBodyStr).getJSONObject("data").getString("taskId");
            String fields = JSONObject.parseObject(jsonBodyStr).getJSONObject("data").getString("fields");
            this.jsonMap.put(taskId, jsonBodyStr);
            logger.debug("takeId: " + taskId);
        }
    }

    /**
     * Handle mode2 callback request
     *
     * @param httpExchange
     * @throws Exception
     */
    private void formDataCallBackExtract(HttpExchange httpExchange) throws Exception {
        Headers headers = httpExchange.getRequestHeaders();
        int length = Integer.parseInt(headers.getFirst("Content-length"));
        InputStream in = httpExchange.getRequestBody();
        byte[] body = IOUtils.toByteArray(in, length);
        List<HttpRequestFormResolver.ParamItem> params = HttpRequestFormResolver.resolveForm(headers, body);
        for (HttpRequestFormResolver.ParamItem paramItem : params) {
            byte[] fileBytes = Arrays.copyOfRange( body, paramItem.getStartIndex(), paramItem.getEndIndex() - 2);
            if(paramItem.getFilename().contains("json")) {
                String jsonBodyStr = new String(fileBytes);
                if(this.jsonMap != null) {
                    String taskId = JSONObject.parseObject(jsonBodyStr).getJSONObject("data").getString("taskId");
                    String fields = JSONObject.parseObject(jsonBodyStr).getJSONObject("data").getString("fields");
                    this.jsonMap.put(taskId, jsonBodyStr);
                    logger.debug("taskId: " + taskId);
                }
            } else {
                if(this.fileMap != null) {
                    this.fileMap.put(paramItem.getFilename(),fileBytes);
                    logger.debug("Receive a file: " + paramItem.getFilename());
                }
                //FileStoreUtils.localFileSystemStore("/home/Documents/tmp/", paramItem.getFilename(), fileBytes);
            }
        }
    }
}