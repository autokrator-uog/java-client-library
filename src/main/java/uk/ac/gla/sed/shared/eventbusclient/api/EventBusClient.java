package uk.ac.gla.sed.shared.eventbusclient.api;

import uk.ac.gla.sed.shared.eventbusclient.internal.messages.Message;
import uk.ac.gla.sed.shared.eventbusclient.internal.messages.NewEventMessage;
import uk.ac.gla.sed.shared.eventbusclient.internal.messages.ReceivedEventMessage;
import uk.ac.gla.sed.shared.eventbusclient.internal.websockets.CloseHandler;
import uk.ac.gla.sed.shared.eventbusclient.internal.websockets.MessageHandler;
import uk.ac.gla.sed.shared.eventbusclient.internal.websockets.wsWrapper;

import javax.websocket.CloseReason;
import java.net.URI;
import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Logger;

public class EventBusClient implements MessageHandler, CloseHandler {
    private static final Logger LOG = Logger.getLogger(EventBusClient.class.getName());
    private final String eventBusURI;
    private wsWrapper wsWrapper;
    private final BlockingQueue<Event> inQueue = new LinkedBlockingQueue<>();
    private final BlockingQueue<Event> outQueue = new LinkedBlockingQueue<>();
    private ProducerThread producerThread;

    public EventBusClient(String eventBusURI) throws RuntimeException {
        this(new wsWrapper(URI.create(eventBusURI)));
    }

    @SuppressWarnings("WeakerAccess")
    public EventBusClient(wsWrapper wrapper) {
        this.wsWrapper = wrapper;
        this.eventBusURI = wrapper.getConnectionURI().toString();

        this.producerThread = new ProducerThread();
    }

    public void start() {
        this.wsWrapper.openSession();

        this.producerThread.start();
        this.wsWrapper.setMessageHandler(this);
        this.wsWrapper.setCloseHandler(this);
    }

    public void stop() {
        this.producerThread.interrupt();
        this.wsWrapper.setMessageHandler(null);

        this.wsWrapper.closeSession();
    }

    public void sendEvent(Event event) {
        outQueue.add(event);
    }

    @Override
    public void handleMessage(Message message) {
        switch (message.getType()) {
            case EVENT:
                ReceivedEventMessage received = new ReceivedEventMessage(message.toString());
                Event receivedEvent = received.getReceivedEvent();
                try {
                    inQueue.put(receivedEvent);
                } catch (InterruptedException interrupt) {
                    LOG.severe("MessageHandler interrupted!!");
                }
                break;
            default:
                LOG.fine(String.format("Skipping event of type %s", message.getType()));
                break;
        }
    }

    @Override
    public void handleClose(CloseReason reason) {
        stop();

        this.wsWrapper = new wsWrapper(URI.create(eventBusURI));
        this.producerThread = new ProducerThread();

        start();
    }

    public BlockingQueue<Event> getIncomingEventsQueue() {
        return inQueue;
    }

    private class ProducerThread extends Thread {
        @Override
        public void run() {
            while (true) {
                try {
                    Event nextEvent = outQueue.take();

                    ArrayList<Event> pending = new ArrayList<>();
                    pending.add(nextEvent);
                    NewEventMessage message = new NewEventMessage(pending);

                    wsWrapper.sendMessage(message);
                } catch (InterruptedException interrupt) {
                    LOG.warning("EventBusClient ProducerThread stopping!");
                    return;
                }
            }
        }
    }
}
