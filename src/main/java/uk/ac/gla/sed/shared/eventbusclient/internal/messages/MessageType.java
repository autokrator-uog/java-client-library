package uk.ac.gla.sed.shared.eventbusclient.internal.messages;

public enum MessageType {
    NEW("new"),
    EVENT("event"),
    QUERY("query"),
    RECEIPT("receipt"),
    REGISTER("register");

    public static final String MESSAGE_FIELD_NAME = "message_type";
    private final String text;

    MessageType(String text) {
        this.text = text;
    }

    public static MessageType getMessageTypeFromString(String string) throws IllegalArgumentException {
        for (MessageType mt : values()) {
            if (mt.text.equals(string)) return mt;
        }

        throw new IllegalArgumentException(
                String.format("Invalid MessageType %s", string)
        );
    }

    @Override
    public String toString() {
        return this.text;
    }
}
