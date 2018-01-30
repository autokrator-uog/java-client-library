package uk.ac.gla.sed.shared.eventbusclient.api;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.eclipsesource.json.ParseException;

public class Consistency {
    private String key;
    private String value;

    public static final String CONSISTENCY_KEY_FIELD = "key";
    public static final String CONSISTENCY_VALUE_FIELD = "value";

    public Consistency(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public Consistency(String jsonSerialized) {
        JsonValue json;
        try {
            json = Json.parse(jsonSerialized);
        } catch (ParseException e) {
            throw new InvalidEventException("Consistency is not valid JSON!", e);
        }

        if (!json.isObject()) {
            throw new InvalidEventException("Consistency is not valid - the event should be a JSON object and it isn't!");
        }
        JsonObject eventJsonObject = json.asObject();

        // =================================================
        // extract the event_type and ensure it's a string
        JsonValue consistencyKey = eventJsonObject.get(CONSISTENCY_KEY_FIELD);
        if (consistencyKey == null) {
            throw new InvalidEventException(String.format("Event is not valid - there is no '%s' property specified and there should be!", CONSISTENCY_KEY_FIELD));
        }
        if (!consistencyKey.isString()) {
            throw new InvalidEventException(String.format("Event is not valid - the '%s' property should be a string!", CONSISTENCY_KEY_FIELD));
        }
        this.key = eventJsonObject.get(CONSISTENCY_KEY_FIELD).asString();

        // =================================================
        // extract the consistency value and ensure it's a string
        JsonValue consistencyValue = eventJsonObject.get(CONSISTENCY_VALUE_FIELD);
        if (consistencyValue == null) {
            throw new InvalidEventException(String.format("Event is not valid - there is no '%s' property specified and there should be!", CONSISTENCY_VALUE_FIELD));
        }
        /*if (!consistencyValue.isString()) {
            throw new InvalidEventException(String.format("Event is not valid - the '%s' property should be a string!", CONSISTENCY_VALUE_FIELD));
        }    */
        if (consistencyValue.isNumber()) {
            this.value = String.valueOf(eventJsonObject.get(CONSISTENCY_VALUE_FIELD).asInt());
        } else{
            this.value = eventJsonObject.get(CONSISTENCY_VALUE_FIELD).asString();
        }
    }
    public JsonObject getFullConsistencyObject() {
        JsonObject consistencyJson = Json.object().asObject();
        consistencyJson.set(CONSISTENCY_KEY_FIELD, key);
        consistencyJson.set(CONSISTENCY_VALUE_FIELD, value);
        return consistencyJson;
    }
}
