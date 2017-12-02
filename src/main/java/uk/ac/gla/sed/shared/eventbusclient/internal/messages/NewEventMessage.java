package uk.ac.gla.sed.shared.eventbusclient.internal.messages;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonArray;
import uk.ac.gla.sed.shared.eventbusclient.api.Event;

import java.util.List;

public class NewEventMessage extends Message {
    public NewEventMessage(List<Event> events) {
        this.type = MessageType.NEW;

        messageJsonObject = Json.object();

        JsonArray eventsJsonArray = Json.array().asArray();
        for (Event event : events) {
            eventsJsonArray.add(event.getFullEventObject());
        }

        messageJsonObject.set("events", eventsJsonArray);
    }
}
