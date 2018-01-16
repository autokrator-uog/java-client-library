package uk.ac.gla.sed.shared.eventbusclient.api;

import uk.ac.gla.sed.shared.eventbusclient.internal.messages.Message;
import uk.ac.gla.sed.shared.eventbusclient.internal.messages.ReceivedEventMessage;
import uk.ac.gla.sed.shared.eventbusclient.internal.producers.ProducerThread;
import uk.ac.gla.sed.shared.eventbusclient.internal.producers.SingleEventProducerThread;
import uk.ac.gla.sed.shared.eventbusclient.internal.websockets.CloseHandler;
import uk.ac.gla.sed.shared.eventbusclient.internal.websockets.MessageHandler;
import uk.ac.gla.sed.shared.eventbusclient.internal.websockets.wsWrapper;

import javax.websocket.CloseReason;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Logger;

/**
 * The EventBusClient class allows you to interface with the event bus using a Java-native interface.
 * <p>
 * RECOMMENDED: If running in dropwizard, create a subclass which implements Managed (it already has start and stop methods)
 * and then use this in your managed lifecycle of the dropwizard app.
 */
public class EventBusClient implements MessageHandler, CloseHandler {
    private static final Logger LOG = Logger.getLogger(EventBusClient.class.getName());

    // instance variables
    private final String eventBusURI;
    private final wsWrapper wsWrapper;

    private final BlockingQueue<Event> inQueue = new LinkedBlockingQueue<>();
    private final BlockingQueue<Event> outQueue = new LinkedBlockingQueue<>();
    private final ProducerThread producerThread;

    // constructors

    /**
     * Create an EventBusClient connecting to a specific WebSockets URI
     *
     * @param eventBusURI the WebSockets URI in the format "ws://{host}:{port}"
     */
    @SuppressWarnings("WeakerAccess")
    public EventBusClient(String eventBusURI) {
        this(new wsWrapper(URI.create(eventBusURI)));
    }

    /**
     * Create an EventBusClient given you already have a wsWrapper object connected to the event bus.
     *
     * @param wrapper the existing wsWrapper object.
     */
    @SuppressWarnings("WeakerAccess")
    public EventBusClient(wsWrapper wrapper) {
        this.wsWrapper = wrapper;
        this.eventBusURI = wrapper.getConnectionURI().toString();

        this.producerThread = new SingleEventProducerThread(wsWrapper, outQueue);
    }

    // getters
    @SuppressWarnings("WeakerAccess")
    public String getEventBusURI() {
        return eventBusURI;
    }

    /**
     * To consume events, simply read from this blocking queue in a concurrent fashion.
     * <p>
     * WARNING: If you use add(), remove(), element(), offer(), poll() or peek(),
     * you will have to ensure thread synchronization yourself!
     * <p>
     * For more info, see https://docs.oracle.com/javase/7/docs/api/java/util/concurrent/BlockingQueue.html
     *
     * @return the BlockingQueue of events to consume.
     */
    public BlockingQueue<Event> getIncomingEventsQueue() {
        return inQueue;
    }

    @SuppressWarnings("WeakerAccess")
    public List<Event> getEventsInOutQueue() {
        return new ArrayList<>(outQueue);
    }

    /**
     * Open the websocket connection and start producing/consuming events.
     */
    public void start() {
        this.wsWrapper.openSession();

        this.producerThread.start();
        this.wsWrapper.setMessageHandler(this);
        this.wsWrapper.setCloseHandler(this);
    }

    /**
     * Close the websocket connection and stop producing/consuming.
     * <p>
     * This method can be called both in the event of an expected shutdown or of an unexpected error causing shutdown
     * (see the handleClose method). So long as before calling closeSession the current object is removed as a
     * CloseHandler, then there are no problems. However, there could be a circular infinite loop here.
     */
    public void stop() {
        this.producerThread.interrupt();
        this.wsWrapper.setMessageHandler(null);

        // IMPORTANT! ensures there is not a circular infinite loop between this close handler and the wsWrapper
        this.wsWrapper.setCloseHandler(null);

        this.wsWrapper.closeSession();
    }

    /**
     * The method for sending an event.
     * <p>
     * The method places the event onto the producer queue, awaiting the producer thread to send it on it's merry way.
     *
     * @param event the event you'd like to send.
     */
    public void sendEvent(Event event, Event correlatedEvent) {
        if (correlatedEvent == null) {
            event.setCorrelationId(ThreadLocalRandom.current().nextLong(999999999));
        } else {
            event.setCorrelationId(correlatedEvent.getCorrelationId());
        }
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
        // retry logic is embedded in wsWrapper
        // so, at this point, just give up.
        stop();
    }

}
