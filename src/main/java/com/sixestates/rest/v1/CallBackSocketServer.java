package com.sixestates.rest.v1;

import com.sixestates.http.CallBackHttpHandler;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;

public class CallBackSocketServer {
    private HttpServer httpServer;
    private ConcurrentHashMap<String, String> jsonStrMap;
    private ConcurrentHashMap<String, byte[]> fileBytesMap;


    /**
     * Construct a new CallBackSocketServer.
     *
     * @param host Server host
     * @param port Server port
     */
    public CallBackSocketServer(String host, int port) throws IOException {
        httpServer = HttpServer.create(new InetSocketAddress(host, port), 0);
        this.jsonStrMap = new ConcurrentHashMap<String, String>();
        this.fileBytesMap = new ConcurrentHashMap<String, byte[]>();
    }

    /**
     * Start a callback socket server asynchronously.
     */
    public void asynStartServer() {
        CallBackHttpHandler callBackHttpHandler = new CallBackHttpHandler(this.jsonStrMap, this.fileBytesMap);
        httpServer.createContext("/", callBackHttpHandler);
        httpServer.setExecutor(Executors.newFixedThreadPool(10));
        httpServer.start();
    }

    /**
     * Start a callback socket server asynchronously.
     *
     * @param path Server host path
     */
    public void asynStartServer(String path) {
        CallBackHttpHandler callBackHttpHandler = new CallBackHttpHandler(this.jsonStrMap, this.fileBytesMap);
        httpServer.createContext(path, callBackHttpHandler);
        httpServer.setExecutor(Executors.newFixedThreadPool(10));
        httpServer.start();
    }

    /**
     * Close the callback socket server.
     */
    public void stopServer() {
        httpServer.stop(0);
    }

    public ConcurrentHashMap<String, String>  getJsonStrMap() {return jsonStrMap;}
    public ConcurrentHashMap<String, byte[]>  getFileBytesMap() {return fileBytesMap;}
}
