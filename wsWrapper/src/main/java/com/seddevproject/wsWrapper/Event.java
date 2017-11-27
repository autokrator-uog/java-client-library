package com.seddevproject.wsWrapper;

import java.util.ArrayList;
import java.util.List;


public class Event {

    private String event_type;
    private List<String> data;

    public Event(){
        this.event_type = null;
        this.data = new ArrayList<String>();
    }

    public Event(String event_type, List<String> data){
        this.event_type = event_type;
        this.data = data;
    }

    public List<String> getData() {
        return data;
    }

    public String getEvent_type() {
        return event_type;
    }



}
