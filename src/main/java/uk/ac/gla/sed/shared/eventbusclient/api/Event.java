package uk.ac.gla.sed.shared.eventbusclient.api;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.eclipsesource.json.ParseException;

/**
 * The main base class for all Events sent to the Event Bus.
 * <p>
 * RECOMMENDATION: Make subclasses of this that call:
 * - the JSON constructor (String jsonRepresentation) for events that are RECEIVABLE by your code
 * - the Simple constructor (String type, JsonObject data) for events that are SENDABLE by your code
 */
public class Event {
    public static final String EVENT_TYPE_FIELD = "event_type";
    public static final String CORRELATION_ID_FIELD = "correlation_id";
    public static final String EVENT_DATA_FIELD = "data";
    public static final String CONSISTENCY_FIELD = "consistency";

    @SuppressWarnings("WeakerAccess")
    protected String type;

    @SuppressWarnings("WeakerAccess")
    protected long correlationId;

    @SuppressWarnings("WeakerAccess")
    protected JsonObject data;

    protected Consistency consistency;

    /**
     * Constructor for creating an event from scratch in Java.
     *
     * @param type the event_type for this event
     * @param data the data for the event as a com.eclipsesource.json.JsonObject
     */
    public Event(String type, JsonObject data, Consistency consistency) {
        this.type = type;
        this.correlationId = 0;
        this.data = data;
        this.consistency = consistency;
    }

    /**
     * Constructor for creating an event object given a serialized JSON representation.
     *
     * @param jsonSerialized the JSON serialized string to initialise the event.
     */
    public Event(String jsonSerialized) {
        JsonValue json;
        try {
            json = Json.parse(jsonSerialized);
        } catch (ParseException e) {
            throw new InvalidEventException("Event is not valid JSON!", e);
        }

        // ensure it's a valid json object
        if (!json.isObject()) {
            throw new InvalidEventException("Event is not valid - the event should be a JSON object and it isn't!");
        }
        JsonObject eventJsonObject = json.asObject();

        // =================================================
        // extract the event_type and ensure it's a string
        JsonValue eventTypeValue = eventJsonObject.get(EVENT_TYPE_FIELD);
        if (eventTypeValue == null) {
            throw new InvalidEventException(String.format("Event is not valid - there is no '%s' property specified and there should be!", EVENT_TYPE_FIELD));
        }
        if (!eventTypeValue.isString()) {
            throw new InvalidEventException(String.format("Event is not valid - the '%s' property should be a string!", EVENT_TYPE_FIELD));
        }
        this.type = eventJsonObject.get(EVENT_TYPE_FIELD).asString();

        // =================================================
        // extract data and ensure it's a JSON object
        JsonValue correlationIdValue = eventJsonObject.get(CORRELATION_ID_FIELD);
        if (correlationIdValue == null) {
            throw new InvalidEventException(String.format("Event is not valid - there is no '%s' property specified and there should be!", CORRELATION_ID_FIELD));
        }
        if (!correlationIdValue.isNumber()) {
            throw new InvalidEventException(String.format("Event is not valid - the '%s' property should be a JSON number and it isn't!", CORRELATION_ID_FIELD));
        }
        this.correlationId = eventJsonObject.get(CORRELATION_ID_FIELD).asLong();

        // =================================================
        // extract data and ensure it's a JSON object
        JsonValue dataValue = eventJsonObject.get(EVENT_DATA_FIELD);
        if (dataValue == null) {
            throw new InvalidEventException(String.format("Event is not valid - there is no '%s' property specified and there should be!", EVENT_DATA_FIELD));
        }
        if (!dataValue.isObject()) {
            throw new InvalidEventException(String.format("Event is not valid - the '%s' property should be a JSON Object and it isn't!", EVENT_DATA_FIELD));
        }
        this.data = eventJsonObject.get(EVENT_DATA_FIELD).asObject();


        // =================================================
        // extract data and ensure it's a JSON object
        JsonValue consistencyData = eventJsonObject.get(CONSISTENCY_FIELD);
        if (consistencyData == null) {
            throw new InvalidEventException(String.format("Event is not valid - there is no '%s' property specified and there should be!", CONSISTENCY_FIELD));
        }
        if (!consistencyData.isObject()) {
            throw new InvalidEventException(String.format("Event is not valid - the '%s' property should be a JSON Object and it isn't!", CONSISTENCY_FIELD));
        }
        this.consistency = new Consistency(eventJsonObject.get(CONSISTENCY_FIELD).toString());
    }

    @Override
    public String toString() {
        return getFullEventObject().toString();
    }

    public String getType() {
        return type;
    }

    protected long getCorrelationId() {
        return correlationId;
    }

    protected void setCorrelationId(long correlationId) {
        this.correlationId = correlationId;
    }

    public JsonObject getData() {
        return data;
    }

    public Consistency getConsistency() {
        return consistency;
    }

    /**
     * Get the full representation of the event as a JSON object.
     *
     * @return a com.eclipsesource.json.JsonObject representing the Event
     */
    public JsonObject getFullEventObject() {
        JsonObject eventJson = Json.object().asObject();
        eventJson.set(EVENT_TYPE_FIELD, type);
        eventJson.set(CORRELATION_ID_FIELD, correlationId);
        eventJson.set(EVENT_DATA_FIELD, data);
        eventJson.set(CONSISTENCY_FIELD, consistency.getFullConsistencyObject());
        return eventJson;
    }
}
