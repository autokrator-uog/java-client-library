package uk.gla.sed.shared.eventbusclient.internal.messages;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonObject;
import org.junit.jupiter.api.Test;
import uk.ac.gla.sed.shared.eventbusclient.api.Event;
import uk.ac.gla.sed.shared.eventbusclient.internal.messages.InvalidMessageException;
import uk.ac.gla.sed.shared.eventbusclient.internal.messages.MessageType;
import uk.ac.gla.sed.shared.eventbusclient.internal.messages.ReceivedEventMessage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ReceivedEventMessageTest {
    private static String testEventType = "TestEventType";
    private static JsonObject testEventData = Json.object().asObject().set("TestField", "TestValue");

    private static JsonObject exampleMessageJson = Json.object().asObject()
            .set(MessageType.MESSAGE_FIELD_NAME, MessageType.EVENT.toString())
            .set(Event.EVENT_TYPE_FIELD, testEventType)
            .set(Event.EVENT_DATA_FIELD, testEventData);

    @Test
    void testValidMessage() {
        ReceivedEventMessage testObject = new ReceivedEventMessage(exampleMessageJson.toString());

        assertEquals(MessageType.EVENT, testObject.getType());
        assertEquals(exampleMessageJson, testObject.getMessageJsonObject());

        Event receivedEvent = testObject.getReceivedEvent();
        JsonObject expectedEventJson = Json.object()
                .merge(exampleMessageJson)
                .remove(MessageType.MESSAGE_FIELD_NAME)
                .asObject();

        assertEquals(expectedEventJson, receivedEvent.getFullEventObject());
        assertEquals(testEventType, receivedEvent.getType());
        assertEquals(testEventData, receivedEvent.getData());
    }

    @Test
    void testIncorrectButValidEventType() {
        JsonObject badEventTypeObject = Json.object().asObject()
                .merge(exampleMessageJson)
                .set(MessageType.MESSAGE_FIELD_NAME, MessageType.QUERY.toString());

        assertThrows(InvalidMessageException.class, () -> {
            ReceivedEventMessage testObject = new ReceivedEventMessage(badEventTypeObject.toString());
        });
    }
}
