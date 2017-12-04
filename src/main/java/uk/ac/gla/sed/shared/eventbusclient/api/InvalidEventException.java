package uk.ac.gla.sed.shared.eventbusclient.api;

@SuppressWarnings("WeakerAccess")
public class InvalidEventException extends RuntimeException {
    InvalidEventException(String message) {
        super(message);
    }

    InvalidEventException(String message, Throwable cause) {
        super(message, cause);
    }

    InvalidEventException(Throwable cause) {
        super(cause);
    }
}
