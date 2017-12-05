package uk.ac.gla.sed.shared.eventbusclient.internal.messages;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.eclipsesource.json.ParseException;

public class Message {
    MessageType type;
    JsonObject messageJsonObject;

    public Message(MessageType type, JsonObject messageJsonObject) {
        this.type = type;
        this.messageJsonObject = messageJsonObject;

        if (messageJsonObject.getString(MessageType.MESSAGE_FIELD_NAME, "").equals("")) {
            messageJsonObject.set(MessageType.MESSAGE_FIELD_NAME, type.toString());
        }
    }

    public Message(String jsonSerialized) throws InvalidMessageException {
        JsonValue json;
        try {
            json = Json.parse(jsonSerialized);
        } catch (ParseException e) {
            throw new InvalidMessageException("Invalid JSON: " + jsonSerialized, e);
        }

        // ensure it's a valid json object
        if (!json.isObject()) {
            throw new InvalidMessageException("Message is not valid JSON - would be an object and isn't!");
        }
        this.messageJsonObject = json.asObject();

        // get the type and ensure it's a string
        JsonValue typeVal = messageJsonObject.get(MessageType.MESSAGE_FIELD_NAME);
        if (typeVal == null || !typeVal.isString()) {
            throw new InvalidMessageException(String.format("Message is not valid JSON - '%s' should be a string!", MessageType.MESSAGE_FIELD_NAME));
        }
        String msgType = messageJsonObject.get(MessageType.MESSAGE_FIELD_NAME).asString();

        // ensure the 'type' is a valid MessageType
        try {
            this.type = MessageType.getMessageTypeFromString(msgType);
        } catch (IllegalArgumentException cause) {
            throw new InvalidMessageException(String.format("Message type %s is not valid", msgType), cause);
        }
    }

    public MessageType getType() {
        return this.type;
    }

    public JsonObject getMessageJsonObject() {
        return messageJsonObject;
    }

    @Override
    public String toString() {
        return messageJsonObject.toString();
    }
}
