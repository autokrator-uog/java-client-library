package uk.ac.gla.sed.shared.eventbusclient.internal.websockets;

import uk.ac.gla.sed.shared.eventbusclient.internal.messages.Message;

public interface MessageHandler {
    void handleMessage(Message message);
}
