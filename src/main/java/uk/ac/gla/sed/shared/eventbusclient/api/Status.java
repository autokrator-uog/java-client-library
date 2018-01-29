package uk.ac.gla.sed.shared.eventbusclient.api;

public enum Status {
    SUCCESS("success"),
    INCONSISTENT("inconsistent");
    
    public static final String MESSAGE_FIELD_NAME = "status";
    private final String text;
    
    Status(String text){
        this.text = text;
    }

    public static Status getStatusFromString(String string) throws IllegalArgumentException {
        for (Status st : values()) {
            if (st.text.equals(string)) return st;
        }

        throw new IllegalArgumentException(
                String.format("Invalid Status %s", string)
        );
    }

    @Override
    public String toString() {
        return this.text;
    }
    
}
