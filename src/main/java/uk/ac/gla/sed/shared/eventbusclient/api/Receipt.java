package uk.ac.gla.sed.shared.eventbusclient.api;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.eclipsesource.json.ParseException;
import uk.ac.gla.sed.shared.eventbusclient.internal.messages.InvalidMessageException;

public class Receipt {
    public static final String CHECKSUM_FIELD = "checksum";
    public static final String STATUS_FIELD = "status";

    @SuppressWarnings("WeakerAccess")
    protected String checksum;

    @SuppressWarnings("WeakerAccess")
    protected String status;
    
    public Receipt(String checksum, String status){
        this.checksum = checksum;
        this.status = status;
    }
    
    public Receipt(String jsonSerialized) {
        JsonValue json;
        try {
            json = Json.parse(jsonSerialized);
        } catch (ParseException e) {
            throw new InvalidReceiptException("Receipt is not valid JSON!", e);
        }

        // ensure it's a valid json object
        if (!json.isObject()) {
            throw new InvalidReceiptException("Receipt is not valid - the event should be a JSON object and it isn't!");
        }
        JsonObject receiptJsonObject = json.asObject();

        // =================================================
        // extract the checksum and ensure it's a string
        JsonValue checksumValue = receiptJsonObject.get(CHECKSUM_FIELD);
        if (checksumValue == null) {
            throw new InvalidReceiptException(String.format("Receipt is not valid - there is no '%s' property specified and there should be!", CHECKSUM_FIELD));
        }
        if (!checksumValue.isString()) {
            throw new InvalidReceiptException(String.format("Receipt is not valid - the '%s' property should be a string!", CHECKSUM_FIELD));
        }
        this.checksum = receiptJsonObject.get(CHECKSUM_FIELD).asString();

        // =================================================
        // extract the status and ensure it's a string
        JsonValue statusValue = receiptJsonObject.get(STATUS_FIELD);
        if (statusValue == null) {
            throw new InvalidReceiptException(String.format("Receipt is not valid - there is no '%s' property specified and there should be!", STATUS_FIELD));
        }
        if (!statusValue.isString()) {
            throw new InvalidReceiptException(String.format("Receipt is not valid - the '%s' property should be a string!", STATUS_FIELD));
        }
        try {
            this.status = Status.getStatusFromString(receiptJsonObject.get(STATUS_FIELD).asString()).toString();
        } catch (IllegalArgumentException cause) {
            throw new InvalidReceiptException(String.format("Status %s is not valid", receiptJsonObject.get(STATUS_FIELD).asString()), cause);
        }
    }

    @Override
    public String toString() {
        return getFullReceiptObject().toString();
    }

    public String getChecksum() {
        return checksum;
    }

    public String getStatus() {
        return status;
    }

    public JsonObject getFullReceiptObject() {
        JsonObject receiptJson = Json.object().asObject();
        receiptJson.set(CHECKSUM_FIELD, checksum);
        receiptJson.set(STATUS_FIELD, status);
        return receiptJson;
    }
}
