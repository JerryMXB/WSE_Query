package com.wse;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerResponse;

import java.io.IOException;


public class Server extends AbstractVerticle {
    @Override
    public void start() {
        HttpServer server = vertx.createHttpServer();
        Query query = new Query();
        server.requestHandler(request -> {
            // This handler gets called for each request that arrives on the server
            HttpServerResponse response = request.response();
            String wordId = request.getParam("word");
            StringBuilder sb = new StringBuilder();
            try {
                String[] docs = query.query(wordId);
                for (String s : docs) {
                    sb.append(s);
                    sb.append(' ');
                }
            } catch (IOException ioe) {
                System.out.println(ioe.getMessage());
            }
            response.putHeader("content-type", "text/plain");
            response.putHeader("Access-Control-Allow-Origin", "*");

            // Write to the response and end it
            response.end(sb.toString());
        });
        server.listen(8080);
    }
}
