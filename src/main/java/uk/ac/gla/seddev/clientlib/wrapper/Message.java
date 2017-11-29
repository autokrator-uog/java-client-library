package uk.ac.gla.seddev.clientlib.wrapper;

import java.util.List;

/**
 * At current this message class is designed around New Event Message. This is only so that we get to a basic stage
 * to test. It is intended that in the future, a Message should never be sent by the wrapper, only an extension
 * of the Message class. (Will change this soon but would like to discuss event schema first.)
 */

public class Message{

    private String type;

    public Message(){
        this.type = null;
    }

    public Message(String type){
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
