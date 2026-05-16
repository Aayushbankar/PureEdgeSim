package com.mechalikh.pureedgesim.gui;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.file.Files;

public class GUIServer {
    private HttpServer server;
    private SimulationData data;
    private int port;
    private Runnable restartTask;

    public GUIServer(SimulationData data, int port, Runnable restartTask) {
        this.data = data;
        this.port = port;
        this.restartTask = restartTask;
    }

    public void start() throws IOException {
        server = HttpServer.create(new InetSocketAddress(port), 0);
        
        // API Endpoint for stats
        server.createContext("/api/stats", new HttpHandler() {
            @Override
            public void handle(HttpExchange exchange) throws IOException {
                sendJsonResponse(exchange, data.toJson().getBytes());
            }
        });

        // API Endpoint for config
        server.createContext("/api/config", new HttpHandler() {
            @Override
            public void handle(HttpExchange exchange) throws IOException {
                if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                    java.util.Scanner s = new java.util.Scanner(exchange.getRequestBody()).useDelimiter("\\A");
                    String body = s.hasNext() ? s.next() : "";
                    // Very basic JSON-like parser for demo
                    String[] pairs = body.replace("{", "").replace("}", "").split(",");
                    for (String pair : pairs) {
                        String[] kv = pair.split(":");
                        if (kv.length == 2) {
                            String k = kv[0].trim().replace("\"", "");
                            String v = kv[1].trim().replace("\"", "");
                            ConfigManager.updateParameter(k, v);
                        }
                    }
                }
                sendJsonResponse(exchange, "{\"status\":\"ok\"}".getBytes());
            }
        });

        // API Endpoint for restart
        server.createContext("/api/restart", new HttpHandler() {
            @Override
            public void handle(HttpExchange exchange) throws IOException {
                if (restartTask != null) {
                    new Thread(restartTask).start();
                    sendJsonResponse(exchange, "{\"status\":\"restarting\"}".getBytes());
                } else {
                    sendJsonResponse(exchange, "{\"status\":\"error\", \"message\":\"No restart task defined\"}".getBytes());
                }
            }
        });

        // Serve static files from 'PureEdgeSim/gui_web'
        server.createContext("/", new StaticFileHandler());

        server.setExecutor(null);
        server.start();
        System.out.println("GUI Server started at http://localhost:" + port);
    }

    private void sendJsonResponse(HttpExchange exchange, byte[] response) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        exchange.sendResponseHeaders(200, response.length);
        OutputStream os = exchange.getResponseBody();
        os.write(response);
        os.close();
    }

    public void stop() {
        if (server != null) {
            server.stop(0);
        }
    }

    static class StaticFileHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String path = exchange.getRequestURI().getPath();
            if (path.equals("/")) {
                path = "/index.html";
            }

            // This path should point to the web dashboard files
            File file = new File("PureEdgeSim/gui_web" + path);
            if (!file.exists() || file.isDirectory()) {
                // Fallback for SPA routing: serve index.html
                file = new File("PureEdgeSim/gui_web/index.html");
            }

            if (file.exists()) {
                String contentType = Files.probeContentType(file.toPath());
                if (contentType == null) {
                    if (path.endsWith(".js")) contentType = "application/javascript";
                    else if (path.endsWith(".css")) contentType = "text/css";
                }
                
                exchange.getResponseHeaders().set("Content-Type", contentType != null ? contentType : "text/plain");
                exchange.sendResponseHeaders(200, file.length());
                OutputStream os = exchange.getResponseBody();
                Files.copy(file.toPath(), os);
                os.close();
            } else {
                String response = "404 (Not Found)\nPlease build the frontend first using 'npm run build' in 'pure-edge-gui' folder.";
                exchange.sendResponseHeaders(404, response.length());
                OutputStream os = exchange.getResponseBody();
                os.write(response.getBytes());
                os.close();
            }
        }
    }
}
