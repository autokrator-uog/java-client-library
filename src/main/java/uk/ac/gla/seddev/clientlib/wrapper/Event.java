package uk.ac.gla.seddev.clientlib.wrapper;

import java.util.ArrayList;
import java.util.List;


public class Event {

    private String event_type;
    private Data data;

    public Event(){
        this.event_type = null;
        this.data = null;
    }

    public Event(String event_type, Data data){
        this.event_type = event_type;
        this.data = data;
    }

    public Data getData() {
        return data;
    }

    public String getEvent_type() {
        return event_type;
    }



}
