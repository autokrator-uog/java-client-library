package uk.ac.gla.seddev.clientlib.wrapper;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class TestMessage {

    public static void main(String[] args){
        Gson gson = new Gson();

        List<Event> events = new ArrayList<>();
        Data d = new Data();
        Event e = new Event("deposit", d);
        events.add(e);
        Message message = new New("new", events);
        String out = gson.toJson(message);
        System.out.println(out);
    }

}
