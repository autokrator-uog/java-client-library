package uk.ac.gla.sed.shared.eventbusclient.internal.messages;

public class InvalidMessageException extends RuntimeException {
    InvalidMessageException(String message) {
        super(message);
    }

    InvalidMessageException(String message, Throwable cause) {
        super(message, cause);
    }
}
