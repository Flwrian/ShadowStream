package fr.flwrian.handler;

import java.io.IOException;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public class ShadowHandler implements HttpHandler{

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        System.out.println(exchange.getLocalAddress());
    }
    
}
