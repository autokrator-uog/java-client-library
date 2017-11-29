package uk.ac.gla.seddev.clientlib.wrapper;


import java.io.IOException;
import java.net.URI;
import java.util.logging.Logger;
import javax.websocket.*;

@ClientEndpoint
public class wsWrapper {

    /**
     * Logger LOG is using level FINE for debug level logs. This is in line with common practice when using
     * java.util.logging.Logger
     */

    Session userSession = null;
    private MessageHandler messageHandler;
    private static final Logger LOG = Logger.getLogger(wsWrapper.class.getName());


    public wsWrapper(URI endpointURI) {
        try {
            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            container.connectToServer(this, endpointURI);
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
        LOG.fine("Opening Websocket");
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
        LOG.fine("Opening Websocket");
        this.userSession = null;
    }

    /**
     * Callback hook for Message Events. This method will be invoked when a client send a message.
     *
     * @param message The text message
     */
    @OnMessage
    public void onMessage(String message) {
        System.out.println("message has been recieved.");
        if (this.messageHandler != null) {
            this.messageHandler.handleMessage(message);
        }
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        if (throwable != null) {
            LOG.severe("SYNC Client error: " + throwable.getMessage());
        } else {
            LOG.severe("SYNC Client error: unknown error");
        }

    }

    /**
     * register message handler
     *
     * @param msgHandler
     */
    public void addMessageHandler(MessageHandler msgHandler) {
        this.messageHandler = msgHandler;
    }

    /**
     * Send a message.
     *
     * @param message
     */

    public void sendMessage(String message) {
        try {
            this.userSession.getBasicRemote().sendText(message);
        } catch (IOException error) {
            LOG.severe(error.getMessage());
        }
    }

    public interface MessageHandler {
        void handleMessage(String message);
    }
}