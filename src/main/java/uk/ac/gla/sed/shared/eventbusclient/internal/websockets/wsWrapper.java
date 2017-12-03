package uk.ac.gla.sed.shared.eventbusclient.internal.websockets;


import org.glassfish.tyrus.client.ClientManager;
import org.glassfish.tyrus.client.ClientProperties;
import uk.ac.gla.sed.shared.eventbusclient.internal.messages.InvalidMessageException;
import uk.ac.gla.sed.shared.eventbusclient.internal.messages.Message;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.util.logging.Logger;

@ClientEndpoint
public class wsWrapper {
    private static final Logger LOG = Logger.getLogger(wsWrapper.class.getName());
    /**
     * Logger LOG is using level FINE for debug level logs. This is in line with common practice when using
     * java.util.logging.Logger
     */

    private URI connectionURI;
    private ClientManager client;
    private ClientManager.ReconnectHandler reconnectHandler;

    private Session userSession;
    private MessageHandler messageHandler;
    private CloseHandler closeHandler;

    public wsWrapper(URI endpointURI) {
        connectionURI = endpointURI;
        reconnectHandler = new wsReconnectHandler();
    }

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

    /**
     * Callback hook for Message Events. This method will be invoked when a client send a message.
     *
     * @param jsonMessage The message text (serialized json)
     */
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

    @OnError
    public void onError(Session session, Throwable throwable) {
        if (throwable != null) {
            throwable.printStackTrace();
            LOG.severe("Websocket error: " + throwable.getMessage());
        } else {
            LOG.severe("Websocket error: unknown");
        }
    }

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

    public ClientManager getClient() {
        return client;
    }

    public Session getUserSession() {
        return userSession;
    }

    /* THESE DEFINITELY SHOULD NOT BE PUBLIC, BUT IT'S THE ONLY WAY TO TEST THEM */

    public MessageHandler getMessageHandler() {
        return messageHandler;
    }

    public void setMessageHandler(MessageHandler msgHandler) {
        this.messageHandler = msgHandler;
    }

    public CloseHandler getCloseHandler() {
        return closeHandler;
    }

    public void setCloseHandler(CloseHandler closeHandler) {
        this.closeHandler = closeHandler;
    }

    public ClientManager.ReconnectHandler getReconnectHandler() {
        return reconnectHandler;
    }
}
