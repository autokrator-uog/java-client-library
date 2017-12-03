package uk.gla.sed.shared.eventbusclient.internal.messages;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.ac.gla.sed.shared.eventbusclient.internal.messages.InvalidMessageException;
import uk.ac.gla.sed.shared.eventbusclient.internal.messages.Message;
import uk.ac.gla.sed.shared.eventbusclient.internal.messages.MessageType;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class MessageTest {
    private static MessageType testType = MessageType.NEW;
    private static JsonObject testMessageObj = Json.object().asObject()
            .set("TestField", "TestValue");
    private static JsonObject fullJson = Json.object().asObject()
            .set(MessageType.MESSAGE_FIELD_NAME, testType.toString())
            .merge(testMessageObj);

    private Message message;

    @BeforeEach
    void setUp() {
        message = new Message(testType, testMessageObj);
    }

    @Test
    void testSimpleConstructor() {
        assertEquals(testType, message.getType());
        assertEquals(testMessageObj, message.getMessageJsonObject());
    }

    @Test
    void testJsonDeserializationConstructorValidJson() {
        Message deserialized = new Message(fullJson.toString());
        assertEquals(testType, deserialized.getType());
        assertEquals(fullJson, deserialized.getMessageJsonObject());
    }

    @Test
    void testDeserializationInvalidJSON() {
        final String someBadJson = "[";

        assertThrows(InvalidMessageException.class, () -> {
            Message deserialized = new Message(someBadJson);
        });
    }

    @Test
    void testDeserializationJSONArray() {
        final String someBadJson = "[]";

        assertThrows(InvalidMessageException.class, () -> {
            Message deserialized = new Message(someBadJson);
        });
    }


    @Test
    void testDeserializationMessageDoesntHaveTypeField() {
        final String someBadJson = "{}";

        assertThrows(InvalidMessageException.class, () -> {
            Message deserialized = new Message(someBadJson);
        });
    }

    @Test
    void testDeserializationMessageHasInvalidType() {
        final String someBadJson = Json.object().asObject()
                .set(MessageType.MESSAGE_FIELD_NAME, "someinvalidtypethatdoesntexist")
                .toString();

        assertThrows(InvalidMessageException.class, () -> {
            Message deserialized = new Message(someBadJson);
        });
    }

    @Test
    void testDeserializationMessageHasMessageTypeFieldAsNotAString() {
        final String someBadJson = Json.object().asObject()
                .set(MessageType.MESSAGE_FIELD_NAME, Json.object().set("Test", "Test"))
                .toString();

        assertThrows(InvalidMessageException.class, () -> {
            Message deserialized = new Message(someBadJson);
        });
    }

    @Test
    void testSerialization() {
        JsonObject serialized = Json.parse(message.toString()).asObject();

        List<String> serializedNames = new ArrayList<>(serialized.names());
        serializedNames.sort(Comparator.naturalOrder());

        List<String> expectedNames = new ArrayList<>(fullJson.names());
        expectedNames.sort(Comparator.naturalOrder());

        assertEquals(expectedNames, serializedNames);

        assertEquals(
                fullJson.getString(MessageType.MESSAGE_FIELD_NAME, " "),
                serialized.getString(MessageType.MESSAGE_FIELD_NAME, "")
        );
        assertEquals(
                fullJson.getString("TestField", " "),
                serialized.getString("TestField", "")
        );
    }
}
