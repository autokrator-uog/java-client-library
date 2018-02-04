package uk.ac.gla.sed.shared.eventbusclienttestapp;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonObject;
import uk.ac.gla.sed.shared.eventbusclient.api.Consistency;
import uk.ac.gla.sed.shared.eventbusclient.api.Event;
import uk.ac.gla.sed.shared.eventbusclient.api.EventBusClient;
import uk.ac.gla.sed.shared.eventbusclient.internal.messages.RegisterMessage;

import java.util.ArrayList;
import java.util.Date;

class TestProduce {

    public static void main(String[] args) throws Exception {

        EventBusClient client = new EventBusClient("ws://127.0.0.1:8081");
        client.start();

        int i = 0;
        while (true) {
            System.out.println(client.getIncomingEventsQueue().isEmpty());
            JsonObject body = Json.object().asObject();
            body.set("TransactionID", String.valueOf(i++));
            body.set("FromAccountID", "1");
            body.set("ToAccountID", "2");
            body.set("Amount", "2000.0");

            ArrayList<String> interestedEvents = new ArrayList<>();
            interestedEvents.add("PendingTransaction");
            interestedEvents.add("AccountCreationRequest");
            RegisterMessage reg = new RegisterMessage("accounts", interestedEvents);
            client.register(reg);

            Event newEvent = new Event("PendingTransaction", body, new Consistency("acc1","*"));

            try {
                System.out.println(String.format("[%s] Sending event...", new Date().toString()));
                client.sendEvent(newEvent, null);
                Thread.sleep(15000);
            } catch (Exception e) {
                break;
            }
        }

        client.stop();
    }
}
