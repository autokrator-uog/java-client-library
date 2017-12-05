package uk.ac.gla.sed.shared.eventbusclient.internal.websockets;

import org.glassfish.tyrus.client.ClientManager;

import javax.websocket.CloseReason;
import java.util.logging.Logger;

class wsReconnectHandler extends ClientManager.ReconnectHandler {
    private static final Logger LOG = Logger.getLogger(wsReconnectHandler.class.getName());
    private static final int DEFAULT_RESET_COUNT = 10;

    private final int resetCount;
    private int disconnectCounter = 0;
    private int connectFailureCounter = 0;

    wsReconnectHandler() {
        resetCount = DEFAULT_RESET_COUNT;
    }

    wsReconnectHandler(int resetCount) {
        this.resetCount = resetCount;
    }

    @Override
    public boolean onDisconnect(CloseReason closeReason) {
        disconnectCounter++;
        if (disconnectCounter <= resetCount) {
            LOG.severe("### Reconnecting... (reconnect count: " + disconnectCounter + ")");
            return true;
        } else {
            disconnectCounter = 0;
            return false;
        }
    }

    @Override
    public boolean onConnectFailure(Exception exception) {
        connectFailureCounter++;
        if (connectFailureCounter <= resetCount) {
            LOG.severe("### Reconnecting... (reconnect count: " + connectFailureCounter + ") " + exception.getMessage());

            try {
                Thread.sleep(300);
            } catch (InterruptedException ex) {
                LOG.severe("onConnectFailure handler was interrupted!");
                return false;
            }
            return true;
        } else {
            connectFailureCounter = 0;
            return false;
        }
    }

    @Override
    public long getDelay() {
        return 1;
    }
}
