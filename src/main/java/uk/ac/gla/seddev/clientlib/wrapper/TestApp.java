package uk.ac.gla.seddev.clientlib.wrapper;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;

public class TestApp {

    public static void main(String[] args) {
        try {
            // open websocket
            final wsWrapper clientEndPoint = new wsWrapper(new URI("ws://127.0.0.1:8081"));

            // add listener
            clientEndPoint.addMessageHandler(new wsWrapper.MessageHandler() {
                public void handleMessage(String message) {
                    System.out.println(message);
                }
            });

            Gson gson = new Gson();

            List<Event> events = new ArrayList<>();
            Data d = new Data();
            Event e = new Event("deposit", d);
            events.add(e);
            Message message = new New("new", events);
            String out = gson.toJson(message);
            System.out.println(out);
            clientEndPoint.sendMessage(out);
            Thread.sleep(50000);

        } catch (InterruptedException ex) {
            System.err.println("InterruptedException exception: " + ex.getMessage());
        } catch (URISyntaxException ex) {
            System.err.println("URISyntaxException exception: " + ex.getMessage());
        }
    }
}
