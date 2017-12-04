package uk.ac.gla.sed.shared.eventbusclient.internal.messages;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonArray;
import uk.ac.gla.sed.shared.eventbusclient.api.Event;

import java.util.List;

public class NewEventMessage extends Message {
    public static final String EVENTS_FIELD_NAME = "events";

    public NewEventMessage(List<Event> events) {
        super(MessageType.NEW, Json.object());

        if (events.isEmpty()) {
            throw new InvalidMessageException("The list of events must not be empty!");
        }

        JsonArray eventsJsonArray = Json.array().asArray();
        for (Event event : events) {
            eventsJsonArray.add(event.getFullEventObject());
        }

        messageJsonObject.set(EVENTS_FIELD_NAME, eventsJsonArray);
    }
}
