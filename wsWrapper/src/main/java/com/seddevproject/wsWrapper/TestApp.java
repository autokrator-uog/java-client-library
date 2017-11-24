package com.seddevproject.wsWrapper;

import java.net.URI;
import java.net.URISyntaxException;
import com.google.gson.Gson;

public class TestApp {

    public static void main(String[] args) {
        try {
            // open websocket
            final wsWrapperEndpoint clientEndPoint = new wsWrapperEndpoint(new URI("ws://127.0.0.1:8081"));

            // add listener
            clientEndPoint.addMessageHandler(new wsWrapperEndpoint.MessageHandler() {
                public void handleMessage(String message) {
                    System.out.println(message);
                }
            });

            
            Gson gson = new Gson();
            Object f = gson.fromJson("{\"type\": \"new\",\"events\": [{\"event_type\": \"deposit\",\"data\": {}},{\"event_type\": \"withdraw1\",\"data\": {}}]}\n", Object.class);
            System.out.println(f.toString());
            String out = gson.toJson(f);
            // send message to websocket
            System.out.println(out);
            clientEndPoint.sendMessage("{\"type\": \"new\",\"events\": [{\"event_type\": \"deposit\",\"data\": {}},{\"event_type\": \"withdraw1\",\"data\": {}}]}\n");
            // wait 5 seconds for messages from websocket
            Thread.sleep(5000);

        } catch (InterruptedException ex) {
            System.err.println("InterruptedException exception: " + ex.getMessage());
        } catch (URISyntaxException ex) {
            System.err.println("URISyntaxException exception: " + ex.getMessage());
        }
    }
}
