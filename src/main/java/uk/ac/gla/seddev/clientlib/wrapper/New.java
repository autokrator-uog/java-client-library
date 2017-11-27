package uk.ac.gla.seddev.clientlib.wrapper;

import java.util.List;

public class New extends Message {

    List<Event> events;

    public New(String type, List<Event> events){
        super(type);
        this.events = events;
    }
}
