package uk.ac.gla.sed.shared.eventbusclient.internal.messages;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonArray;

import java.util.ArrayList;

public class RegisterMessage extends Message {
    public static final String FIELD_CLIENT_TYPE = "client_type";
    public static final String FIELD_EVENT_TYPES = "event_types";

    public RegisterMessage(String clientType, ArrayList<String> eventTypes) {
        super(MessageType.REGISTER, Json.object());

        JsonArray eventTypesJson = Json.array().asArray();

        for (String eventType: eventTypes) {
            eventTypesJson.add(eventType);
        }

        messageJsonObject.set(FIELD_CLIENT_TYPE, clientType);
        messageJsonObject.set(FIELD_EVENT_TYPES, eventTypesJson);
    }
}
