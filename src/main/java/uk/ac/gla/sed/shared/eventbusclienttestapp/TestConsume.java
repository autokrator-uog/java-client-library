package uk.ac.gla.sed.shared.eventbusclienttestapp;

import uk.ac.gla.sed.shared.eventbusclient.api.Event;
import uk.ac.gla.sed.shared.eventbusclient.api.EventBusClient;

import java.util.concurrent.TimeUnit;

class TestConsume {

    public static void main(String[] args) {
        EventBusClient client = new EventBusClient("ws://127.0.0.1:8081");
        client.start();

        while (true) {
            try {
                Event newEvent = client.getIncomingEventsQueue().poll(30, TimeUnit.SECONDS);
                if (newEvent == null) {
                    System.out.println(client.getIncomingEventsQueue().isEmpty());
                    System.out.println("Nothing yet....");
                    continue;
                }

                // simulate consuming the event
                System.out.println(newEvent);

            } catch (InterruptedException interrupt) {
                System.err.println("Interrupt...");
                break;
            }
        }

        System.out.println("Stopping...");
        client.stop();
    }
}
