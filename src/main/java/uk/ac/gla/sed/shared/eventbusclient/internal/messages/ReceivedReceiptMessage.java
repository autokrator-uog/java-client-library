package uk.ac.gla.sed.shared.eventbusclient.internal.messages;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonObject;
import uk.ac.gla.sed.shared.eventbusclient.api.Receipt;

public class ReceivedReceiptMessage extends Message {
    private Receipt receivedReceipt;

    public ReceivedReceiptMessage(String jsonSerialized) throws InvalidMessageException {
        super(jsonSerialized);

        if (this.type != MessageType.RECEIPT) {
            throw new InvalidMessageException("Message is not of correct type.");
        }

        JsonObject clone = Json.object().asObject().merge(messageJsonObject);
        clone.remove(MessageType.MESSAGE_FIELD_NAME);

        this.receivedReceipt = new Receipt(clone.toString());
    }

    public Receipt getReceivedReceipt() {
        return this.receivedReceipt;
    }
}