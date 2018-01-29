package uk.ac.gla.sed.shared.eventbusclient.api;

public class ReturningEvent {

    private Event event;
    private Status status;

    public ReturningEvent(Event event, Status status){
        this.event = event;
        this.status = status;
    }

    public Event getEvent() {
        return event;
    }

    public Status getStatus() {
        return status;
    }
}
