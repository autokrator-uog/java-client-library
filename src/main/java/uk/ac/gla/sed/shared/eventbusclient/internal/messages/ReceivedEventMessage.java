package uk.ac.gla.sed.shared.eventbusclient.internal.messages;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonObject;
import uk.ac.gla.sed.shared.eventbusclient.api.Event;

public class ReceivedEventMessage extends Message {
    private Event receivedEvent;

    public ReceivedEventMessage(String jsonSerialized) throws InvalidMessageException {
        super(jsonSerialized);

        if (this.type != MessageType.EVENT) {
            throw new InvalidMessageException("Message is not of correct type.");
        }

        JsonObject clone = Json.object().asObject().merge(messageJsonObject);
        clone.remove(MessageType.MESSAGE_FIELD_NAME);

        this.receivedEvent = new Event(clone.toString());
    }

    public Event getReceivedEvent() {
        return this.receivedEvent;
    }
}
