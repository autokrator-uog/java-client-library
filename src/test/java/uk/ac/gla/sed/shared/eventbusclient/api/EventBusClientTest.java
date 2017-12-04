package uk.ac.gla.sed.shared.eventbusclient.api;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import uk.ac.gla.sed.shared.eventbusclient.internal.messages.Message;
import uk.ac.gla.sed.shared.eventbusclient.internal.messages.MessageType;
import uk.ac.gla.sed.shared.eventbusclient.internal.websockets.wsWrapper;

import javax.websocket.CloseReason;
import java.net.URI;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

class EventBusClientTest {
    private static final String testEventType = "TestEventType";
    private static final JsonObject testEventData = Json.object().asObject().set("TestField", "TestValue");
    private static final JsonObject exampleMessageJson = Json.object().asObject()
            .set(MessageType.MESSAGE_FIELD_NAME, MessageType.EVENT.toString())
            .set(Event.EVENT_TYPE_FIELD, testEventType)
            .set(Event.EVENT_DATA_FIELD, testEventData);
    private EventBusClient client;
    private wsWrapper wrapper;

    @BeforeEach
    void setUp() {
        wrapper = mock(wsWrapper.class);
        when(wrapper.getConnectionURI()).thenReturn(URI.create("ws://127.0.0.1"));

        client = new EventBusClient(wrapper);
        Mockito.reset(wrapper);
    }

    @AfterEach
    void tearDown() {
//        try {
//            client.stop();
//        } catch (Exception e) {
//            System.err.println(e);
//        }
        client.stop();
    }

    @Test
    void testCreateWithURI() {
        String wsURI = "ws://127.0.0.1";

        EventBusClient myClient = new EventBusClient(wsURI);

        assertEquals(wsURI, myClient.getEventBusURI());
    }

    @Test
    void testSendEvent() {
        Event e = new Event("TestEventType", Json.object().asObject());

        client.sendEvent(e);

        assertTrue(client.getEventsInOutQueue().contains(e));
    }

    @Test
    void testHandleMessageReceivedEvent() {
        Message m = new Message(exampleMessageJson.toString());

        client.handleMessage(m);

        Event headOfQueue = client.getIncomingEventsQueue().peek();
        assertEquals(testEventType, headOfQueue.getType());
        assertEquals(testEventData, headOfQueue.getData());
    }

    @Test
    void testHandleMessageOtherTypeOfMessage() {
        JsonObject otherExample = Json.object().asObject()
                .merge(exampleMessageJson)
                .set(MessageType.MESSAGE_FIELD_NAME, MessageType.QUERY.toString());
        Message m = new Message(otherExample.toString());

        client.handleMessage(m);

        assertTrue(client.getIncomingEventsQueue().isEmpty());
    }

    @Test
    void testStart() {
        client.start();

        verify(wrapper, atLeastOnce()).setMessageHandler(client);
        verify(wrapper, atLeastOnce()).setCloseHandler(client);
    }

    @Test
    void testStop() {
        client.start();
        Mockito.reset(wrapper); // start counts from zero

        client.stop();

        verify(wrapper, atLeastOnce()).setMessageHandler(null);
        verify(wrapper, atLeastOnce()).setCloseHandler(null);
    }

    @Test
    void testHandleClose() {
        client = spy(client);
        client.start();
        CloseReason reason = new CloseReason(CloseReason.CloseCodes.CLOSED_ABNORMALLY, "TEST REASON!");

        client.handleClose(reason);

        verify(client, atLeastOnce()).stop();
    }
}
