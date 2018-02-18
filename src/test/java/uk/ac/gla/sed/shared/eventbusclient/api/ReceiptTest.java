package uk.ac.gla.sed.shared.eventbusclient.api;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonObject;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;


public class ReceiptTest {
    private final String testChecksum = "aaf4c61ddcc5e8a2dabede0f3b482cd9aea9434d";

    private final JsonObject fullJsonObject = Json.object().asObject()
            .set("checksum", testChecksum)
            .set("status",Status.SUCCESS.toString());

    @Test
    void testSimpleConstructor() {
        Receipt receipt = new Receipt( testChecksum, Status.SUCCESS.toString());

        assertEquals(testChecksum, receipt.getChecksum());
        assertEquals(Status.SUCCESS.toString(), receipt.getStatus());
    }

    @Test
    void testToString() {
        Receipt receipt = new Receipt( testChecksum, Status.SUCCESS.toString());
        assertEquals(fullJsonObject.toString(), receipt.toString());
    }

    @Test
    void testFullObjectSerialization() {
        Receipt receipt = new Receipt( testChecksum, Status.SUCCESS.toString());
        JsonObject serialized = receipt.getFullReceiptObject();

        assertEquals(fullJsonObject, serialized);
    }

    @Test
    void testDeserializingConstructor() {
        Receipt deserializedReceipt = new Receipt(fullJsonObject.toString());

        assertEquals(testChecksum, deserializedReceipt.getChecksum());
        assertEquals(Status.SUCCESS.toString(), deserializedReceipt.getStatus());
    }

    @Test
    void testDeserializingConstructorInvalidJson() {
        final String someInvalidJson = "]";

        assertThrows(InvalidReceiptException.class, () -> {
            Receipt deserailizedReceipt = new Receipt(someInvalidJson);
        });
    }

    @Test
    void testDeserializingConstructorNoStatus() {
        final String someInvalidJson = Json.object().asObject()
                .set("checksum", testChecksum).toString();

        assertThrows(InvalidReceiptException.class, () -> {
            Receipt deserializedReceipt = new Receipt(someInvalidJson);
        });
    }

    @Test
    void testDeserializingConstructorNoChecksum() {
        final String someInvalidJson = Json.object().asObject()
                .set("status", Status.SUCCESS.toString()).toString();

        assertThrows(InvalidReceiptException.class, () -> {
            Receipt deserializedReceipt = new Receipt(someInvalidJson);
        });
    }

}
