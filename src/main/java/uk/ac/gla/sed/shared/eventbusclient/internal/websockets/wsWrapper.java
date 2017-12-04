package uk.ac.gla.sed.shared.eventbusclient.internal.websockets;


import org.glassfish.tyrus.client.ClientManager;
import org.glassfish.tyrus.client.ClientProperties;
import uk.ac.gla.sed.shared.eventbusclient.internal.messages.InvalidMessageException;
import uk.ac.gla.sed.shared.eventbusclient.internal.messages.Message;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.util.logging.Logger;

@SuppressWarnings("unused")
@ClientEndpoint
public class wsWrapper {
    private static final Logger LOG = Logger.getLogger(wsWrapper.class.getName());
    /**
     * Logger LOG is using level FINE for debug level logs. This is in line with common practice when using
     * java.util.logging.Logger
     */

    private final URI connectionURI;
    private ClientManager client;

    private final ClientManager.ReconnectHandler reconnectHandler;

    private Session userSession;
    private MessageHandler messageHandler;
    private CloseHandler closeHandler;

    public wsWrapper(URI endpointURI) {
        connectionURI = endpointURI;
        reconnectHandler = new wsReconnectHandler();
    }

    @SuppressWarnings("WeakerAccess")
    public wsWrapper(URI endpointURI, ClientManager.ReconnectHandler reconnectHandler) {
        connectionURI = endpointURI;
        this.reconnectHandler = reconnectHandler;
    }

    public URI getConnectionURI() {
        return connectionURI;
    }

    /**
     * Callback hook for Connection open events.
     *
     * @param userSession the userSession which is opened.
     */
    @SuppressWarnings("WeakerAccess")
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
    @SuppressWarnings("WeakerAccess")
    @OnClose
    public void onClose(Session userSession, CloseReason reason) {
        LOG.warning("Closing Websocket...");
        this.userSession = null;
        if (this.closeHandler != null) {
            this.closeHandler.handleClose(reason);
        }
    }

    /**
     * Callback hook for Message Events. This method will be invoked when a client send a message.
     *
     * @param jsonMessage The message text (serialized json)
     */
    @SuppressWarnings("WeakerAccess")
    @OnMessage
    public void onMessage(String jsonMessage) {
        if (this.messageHandler == null) {
            LOG.warning("No MessageHandler configured...ignoring message!");
            return;
        }

        try {
            Message messageObj = new Message(jsonMessage);
            this.messageHandler.handleMessage(messageObj);
        } catch (InvalidMessageException e) {
            LOG.severe(String.format("Invalid message received! Contents: %s", jsonMessage));
        }
    }

    /**
     * Callback for Errors with the Websockets connection.
     *
     * @param session   The websockets session
     * @param throwable The causing exception or null
     */
    @SuppressWarnings("WeakerAccess")
    @OnError
    public void onError(Session session, Throwable throwable) {
        if (throwable != null) {
            throwable.printStackTrace();
            LOG.severe("Websocket error: " + throwable.getMessage());
        } else {
            LOG.severe("Websocket error: unknown");
        }
    }

    /**
     * This is the main method used to send messages via the websockets.
     *
     * @param message Takes a Message abstraction and sends it to the event bus.
     */
    public void sendMessage(Message message) {
        String msgText = message.toString();

        try {
            this.userSession.getBasicRemote().sendText(msgText);
        } catch (IOException error) {
            LOG.severe(error.getMessage());
        }
    }

    public void openSession() {
        client = ClientManager.createClient();
        client.getProperties().put(ClientProperties.RECONNECT_HANDLER, reconnectHandler);

        try {
            client.connectToServer(this, connectionURI);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void closeSession() {
        if (this.userSession != null) {
            try {
                this.userSession.close();
            } catch (IOException e) {
                LOG.severe(e.getMessage());
            }
        }
    }

    ClientManager getClient() {
        return client;
    }

    Session getUserSession() {
        return userSession;
    }

    /* THESE DEFINITELY SHOULD NOT BE PUBLIC, BUT IT'S THE ONLY WAY TO TEST THEM */

    MessageHandler getMessageHandler() {
        return messageHandler;
    }

    /**
     * The message handler is the callback delegate for when a new message is received.
     * <p>
     * This function allows you to set this delegate.
     *
     * @param msgHandler the delegate object, which must implement MessageHandler
     */
    public void setMessageHandler(MessageHandler msgHandler) {
        this.messageHandler = msgHandler;
    }

    CloseHandler getCloseHandler() {
        return closeHandler;
    }

    /**
     * The close handler is a callback delegate for when the connection is closed, either expectedly or unexpectedly.
     * <p>
     * This function allows you to set this delegate.
     *
     * @param closeHandler the delegate object, which must implement CloseHandler
     */
    public void setCloseHandler(CloseHandler closeHandler) {
        this.closeHandler = closeHandler;
    }

    ClientManager.ReconnectHandler getReconnectHandler() {
        return reconnectHandler;
    }
}
