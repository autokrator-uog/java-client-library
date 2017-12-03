package uk.ac.gla.sed.shared.eventbusclient.api;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;


public class Event {
    public static final String EVENT_TYPE_FIELD = "event_type";
    public static final String EVENT_DATA_FIELD = "data";

    @SuppressWarnings("WeakerAccess")
    protected String type;

    @SuppressWarnings("WeakerAccess")
    protected JsonObject data;

    public Event(String type, JsonObject data) {
        this.type = type;
        this.data = data;
    }

    public Event(String jsonSerialized) {
        JsonValue json = Json.parse(jsonSerialized);

        // ensure it's a valid json object
        if (!json.isObject()) {
            throw new RuntimeException("Event is not valid JSON - would be an object and isn't!");
        }
        JsonObject eventJsonObject = json.asObject();

        // extract the event_type and ensure it's a string
        if (!eventJsonObject.get(EVENT_TYPE_FIELD).isString()) {
            throw new RuntimeException("Event is not valid JSON - 'event_type' should be a string!");
        }
        this.type = eventJsonObject.get(EVENT_TYPE_FIELD).asString();

        // extract data and ensure it's a JSON object
        if (!eventJsonObject.get(EVENT_DATA_FIELD).isObject()) {
            throw new RuntimeException("Event is not valid JSON - 'data' should be a JSON Object!");
        }
        this.data = eventJsonObject.get(EVENT_DATA_FIELD).asObject();
    }

    @Override
    public String toString() {
        return getFullEventObject().toString();
    }

    public String getType() {
        return type;
    }

    public JsonObject getData() {
        return data;
    }

    public JsonObject getFullEventObject() {
        JsonObject eventJson = Json.object().asObject();
        eventJson.set("event_type", type);
        eventJson.set("data", data);
        return eventJson;
    }
}
