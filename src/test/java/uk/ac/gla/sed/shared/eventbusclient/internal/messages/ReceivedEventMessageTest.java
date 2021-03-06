package uk.ac.gla.sed.shared.eventbusclient.internal.messages;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonObject;
import org.junit.jupiter.api.Test;
import uk.ac.gla.sed.shared.eventbusclient.api.Consistency;
import uk.ac.gla.sed.shared.eventbusclient.api.Event;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ReceivedEventMessageTest {
    private static final String testEventType = "TestEventType";
    private static final long testCorrelationId = 584937848;
    private static final JsonObject testEventData = Json.object().asObject().set("TestField", "TestValue");
    private static final JsonObject testConsistency = new Consistency("test", "*").getFullConsistencyObject();

    private static final JsonObject exampleMessageJson = Json.object().asObject()
            .set(MessageType.MESSAGE_FIELD_NAME, MessageType.EVENT.toString())
            .set(Event.EVENT_TYPE_FIELD, testEventType)
            .set(Event.CORRELATION_ID_FIELD, testCorrelationId)
            .set(Event.EVENT_DATA_FIELD, testEventData)
            .set(Event.CONSISTENCY_FIELD, testConsistency);

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
