package uk.ac.gla.sed.shared.eventbusclient.api;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

public class Event {
    protected String type;
    protected JsonObject data;

    public Event() {
    }

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
        if (!eventJsonObject.get("event_type").isString()) {
            throw new RuntimeException("Event is not valid JSON - 'event_type' should be a string!");
        }
        this.type = eventJsonObject.get("event_type").asString();

        // extract data and ensure it's a JSON object
        if (!eventJsonObject.get("data").isObject()) {
            throw new RuntimeException("Event is not valid JSON - 'data' should be a JSON Object!");
        }
        this.data = eventJsonObject.get("data").asObject();
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
