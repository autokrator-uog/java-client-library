package uk.ac.gla.sed.shared.eventbusclient.internal.messages;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.ac.gla.sed.shared.eventbusclient.api.Event;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class NewEventMessageTest {
    private static final JsonObject testEventBody = Json.object().asObject()
            .set("Test", "test");

    private List<Event> eventList;

    @BeforeEach
    void setUp() {
        eventList = new ArrayList<>();
        eventList.add(new Event("TestEvent", testEventBody));
    }

    @Test
    void testConstructor() {
        JsonObject expectedJson = Json.object().asObject()
                .set(MessageType.MESSAGE_FIELD_NAME, MessageType.NEW.toString())
                .set("events", Json.array().asArray().add(eventList.get(0).getFullEventObject()));

        NewEventMessage messageUnderTest = new NewEventMessage(eventList);
        assertEquals(MessageType.NEW, messageUnderTest.getType());
        assertEquals(expectedJson, messageUnderTest.getMessageJsonObject());
    }

    @Test
    void testConstructorWithEmptyList() {
        assertThrows(InvalidMessageException.class, () -> {
            NewEventMessage messageUnderTest = new NewEventMessage(new ArrayList<>());
        });
    }
}
