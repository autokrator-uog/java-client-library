package uk.ac.gla.sed.shared.eventbusclient.internal.websockets;


import org.glassfish.tyrus.client.ClientManager;
import org.glassfish.tyrus.client.ClientProperties;
import uk.ac.gla.sed.shared.eventbusclient.internal.messages.Message;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.util.logging.Logger;

@ClientEndpoint
public class wsWrapper {
    /**
     * Logger LOG is using level FINE for debug level logs. This is in line with common practice when using
     * java.util.logging.Logger
     */

    private Session userSession = null;
    private MessageHandler messageHandler;
    private CloseHandler closeHandler;
    private static final Logger LOG = Logger.getLogger(wsWrapper.class.getName());

    public wsWrapper(URI endpointURI) {
        ClientManager client = ClientManager.createClient();
        client.getProperties().put(ClientProperties.RECONNECT_HANDLER, new wsReconnectHandler());

        try {
            client.connectToServer(this, endpointURI);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Callback hook for Connection open events.
     *
     * @param userSession the userSession which is opened.
     */
    @OnOpen
    public void onOpen(Session userSession) {
        LOG.info("Opening Websocket...");
        this.userSession = userSession;
    }

    /**
     * Callback hook for Connection close events.
     *
     * @param userSession the userSession which is getting closed.
     * @param reason      the reason for connection close
     */
    @OnClose
    public void onClose(Session userSession, CloseReason reason) {
        LOG.warning("Closing Websocket...");
        this.userSession = null;
        if (this.closeHandler != null) {
            this.closeHandler.handleClose(reason);
        }
    }

    public void closeSession() {
        this.userSession = null;
    }

    /**
     * Callback hook for Message Events. This method will be invoked when a client send a message.
     *
     * @param jsonMessage The message text (serialized json)
     */
    @OnMessage
    public void onMessage(String jsonMessage) {
        if (this.messageHandler != null) {
            // TODO exception handling
            Message messageObj = new Message(jsonMessage);
            this.messageHandler.handleMessage(messageObj);
        }
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        if (throwable != null) {
            throwable.printStackTrace();
            LOG.severe("SYNC Client error: " + throwable.getMessage());
        } else {
            LOG.severe("SYNC Client error: unknown error");
        }

    }

    public void removeMessageHandler() {
        this.messageHandler = null;
    }

    public void addMessageHandler(MessageHandler msgHandler) {
        this.messageHandler = msgHandler;
    }

    public void sendMessage(Message message) {
        try {
            String msgText = message.toString();
            this.userSession.getBasicRemote().sendText(msgText);
        } catch (IOException error) {
            LOG.severe(error.getMessage());
        }
    }

    public void addCloseHandler(CloseHandler closeHandler) {
        this.closeHandler = closeHandler;
    }

    public interface MessageHandler {
        void handleMessage(Message message);
    }

    public interface CloseHandler {
        void handleClose(CloseReason reason);
    }

    class wsReconnectHandler extends ClientManager.ReconnectHandler {
        private static final int RESET_COUNT = 15;
        private int counter = 0;

        @Override
        public boolean onDisconnect(CloseReason closeReason) {
            counter++;
            if (counter <= RESET_COUNT) {
                LOG.severe("### Reconnecting... (reconnect count: " + counter + ")");
                return true;
            } else {
                counter = 0;
                return false;
            }
        }

        @Override
        public boolean onConnectFailure(Exception exception) {
            counter++;
            if (counter <= RESET_COUNT) {
                LOG.severe("### Reconnecting... (reconnect count: " + counter + ") " + exception.getMessage());

                try {
                    Thread.sleep(300);
                } catch (InterruptedException ex) {
                    LOG.severe("onConnectFailure handler was interrupted!");
                    return false;
                }
                return true;
            } else {
                counter = 0;
                return false;
            }
        }

        @Override
        public long getDelay() {
            return 1;
        }
    }

}
