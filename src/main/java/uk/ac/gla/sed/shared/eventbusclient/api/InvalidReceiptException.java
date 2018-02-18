package uk.ac.gla.sed.shared.eventbusclient.api;

@SuppressWarnings("WeakerAccess")
public class InvalidReceiptException extends RuntimeException {
    InvalidReceiptException(String message) {
        super(message);
    }

    InvalidReceiptException(String message, Throwable cause) {
        super(message, cause);
    }

    InvalidReceiptException(Throwable cause) {
        super(cause);
    }
}
