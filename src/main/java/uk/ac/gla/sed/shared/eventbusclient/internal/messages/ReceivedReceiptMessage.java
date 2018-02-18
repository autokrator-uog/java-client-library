package uk.ac.gla.sed.shared.eventbusclient.internal.messages;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import uk.ac.gla.sed.shared.eventbusclient.api.Receipt;

import java.util.ArrayList;
import java.util.List;

public class ReceivedReceiptMessage extends Message {
    private List<Receipt> receivedReceipt;

    public ReceivedReceiptMessage(String jsonSerialized) throws InvalidMessageException {
        super(jsonSerialized);

        List<Receipt> receiptsIn = new ArrayList<>();

        if (this.type != MessageType.RECEIPT) {
            throw new InvalidMessageException("Message is not of correct type.");
        }


        JsonObject clone = Json.object().asObject().merge(messageJsonObject);
        clone.remove(MessageType.MESSAGE_FIELD_NAME);

        for (JsonValue receipt : clone.get("receipts").asArray()){
            receiptsIn.add(new Receipt(receipt.toString()));
        }
        this.receivedReceipt = receiptsIn;
    }

    public List<Receipt> getReceivedReceipts() {
        return this.receivedReceipt;
    }
}