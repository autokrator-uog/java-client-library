package uk.ac.gla.sed.shared.eventbusclient.internal.websockets;

import javax.websocket.CloseReason;

public interface CloseHandler {
    @SuppressWarnings("unused")
    void handleClose(CloseReason reason);
}
