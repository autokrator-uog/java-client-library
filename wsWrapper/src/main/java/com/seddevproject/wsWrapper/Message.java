package com.seddevproject.wsWrapper;

import java.util.ArrayList;
import java.util.List;

/*
At current this message class is designed around New Event Message. This is only so that we get to a basic stage
to test. It is intended that in the future, a Message should never be sent by the wrapper, only an extension
of the Message class. (Will change this soon but would like to discuss event schema first.)
 */

public class Message{

    private String type;
    private List<Event> events;

    public Message(){
        this.type = null;
        this.events = new ArrayList<>();
    }

    public Message(String type, List<Event> events){
        this.type = type;
        this.events = events;
    }

    public String getType() {
        return type;
    }

    public List<Event> getEvents() {
        return events;
    }
}
