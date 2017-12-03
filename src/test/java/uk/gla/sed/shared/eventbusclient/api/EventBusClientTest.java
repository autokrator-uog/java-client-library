package uk.gla.sed.shared.eventbusclient.api;

import org.junit.jupiter.api.BeforeEach;
import uk.ac.gla.sed.shared.eventbusclient.api.EventBusClient;
import uk.ac.gla.sed.shared.eventbusclient.internal.websockets.wsWrapper;

import java.net.URI;

public class EventBusClientTest {
    private EventBusClient client;

    @BeforeEach
    void setUp() {
        client = new EventBusClient(
                new wsWrapper(URI.create("ws://127.0.0.1"))
        );
    }
}
