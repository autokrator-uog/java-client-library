package uk.ac.gla.sed.shared.eventbusclienttestapp;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonObject;
import uk.ac.gla.sed.shared.eventbusclient.api.Event;
import uk.ac.gla.sed.shared.eventbusclient.api.EventBusClient;

import java.util.Date;

class TestProduce {

    public static void main(String[] args) throws Exception {

        EventBusClient client = new EventBusClient("ws://127.0.0.1:8081");
        client.start();

        int i = 0;
        while (true) {
            JsonObject body = Json.object().asObject();
            body.set("TransactionID", String.valueOf(i++));
            body.set("FromAccountID", "1");
            body.set("ToAccountID", "2");
            body.set("Amount", "2000.0");

            Event newEvent = new Event("PendingTransaction", body);

            try {
                System.out.println(String.format("[%s] Sending event...", new Date().toString()));
                client.sendEvent(newEvent);
                Thread.sleep(15000);
            } catch (Exception e) {
                break;
            }
        }

        client.stop();
    }
}