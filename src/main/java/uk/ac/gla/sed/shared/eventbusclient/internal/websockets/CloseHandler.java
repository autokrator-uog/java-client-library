package uk.ac.gla.sed.shared.eventbusclient.internal.websockets;

import javax.websocket.CloseReason;

public interface CloseHandler {
    void handleClose(CloseReason reason);
}
