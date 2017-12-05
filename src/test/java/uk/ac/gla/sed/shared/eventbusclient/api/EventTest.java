package uk.ac.gla.sed.shared.eventbusclient.api;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonObject;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class EventTest {
    private final String testEventType = "TestEventType";
    private final JsonObject testData = Json.object().asObject()
            .set("TestField", "TestValue");
    private final JsonObject fullJsonObject = Json.object().asObject()
            .set("event_type", testEventType)
            .set("data", testData);

    @Test
    void testSimpleConstructor() {
        Event event = new Event(testEventType, testData);

        assertEquals(testEventType, event.getType());
        assertEquals(testData, event.getData());
    }

    @Test
    void testToString() {
        Event event = new Event(testEventType, testData);
        assertEquals(fullJsonObject.toString(), event.toString());
    }

    @Test
    void testFullObjectSerialization() {
        Event event = new Event(testEventType, testData);
        JsonObject serialized = event.getFullEventObject();

        assertEquals(fullJsonObject, serialized);
    }

    @Test
    void testDeserializingConstructor() {
        Event deserailizedEvent = new Event(fullJsonObject.toString());


        assertEquals(testEventType, deserailizedEvent.getType());
        assertEquals(testData, deserailizedEvent.getData());
    }


    @Test
    void testDeserializingConstructorInvalidJson() {
        final String someInvalidJson = "]";

        assertThrows(InvalidEventException.class, () -> {
            Event deserailizedEvent = new Event(someInvalidJson);
        });
    }


    @Test
    void testDeserializingConstructorNotJsonObject() {
        final String someInvalidJson = "[]";

        assertThrows(InvalidEventException.class, () -> {
            Event deserailizedEvent = new Event(someInvalidJson);
        });
    }

    @Test
    void testDeserializingConstructorNoEventType() {
        final String someInvalidJson = Json.object().asObject()
                .set(Event.EVENT_DATA_FIELD, testData)
                .toString();

        assertThrows(InvalidEventException.class, () -> {
            Event deserailizedEvent = new Event(someInvalidJson);
        });
    }

    @Test
    void testDeserializingConstructorNoData() {
        final String someInvalidJson = Json.object().asObject()
                .set(Event.EVENT_TYPE_FIELD, testEventType)
                .toString();

        assertThrows(InvalidEventException.class, () -> {
            Event deserailizedEvent = new Event(someInvalidJson);
        });
    }

    @Test
    void testDeserializingConstructorDataIsNotObject() {
        final String someInvalidJson = Json.object().asObject()
                .set(Event.EVENT_TYPE_FIELD, testEventType)
                .set(Event.EVENT_DATA_FIELD, "")
                .toString();

        assertThrows(InvalidEventException.class, () -> {
            Event deserailizedEvent = new Event(someInvalidJson);
        });
    }


    @Test
    void testDeserializingConstructorEventTypeIsNotString() {
        final String someInvalidJson = Json.object().asObject()
                .set(Event.EVENT_TYPE_FIELD, Json.array())
                .set(Event.EVENT_DATA_FIELD, testData)
                .toString();

        assertThrows(InvalidEventException.class, () -> {
            Event deserailizedEvent = new Event(someInvalidJson);
        });
    }
}
