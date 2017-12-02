package uk.ac.gla.sed.shared.eventbusclient.api;

import uk.ac.gla.sed.shared.eventbusclient.internal.messages.Message;
import uk.ac.gla.sed.shared.eventbusclient.internal.messages.NewEventMessage;
import uk.ac.gla.sed.shared.eventbusclient.internal.messages.ReceivedEventMessage;
import uk.ac.gla.sed.shared.eventbusclient.internal.websockets.wsWrapper;

import javax.websocket.CloseReason;
import java.net.URI;
import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Logger;

public class EventBusClient implements wsWrapper.MessageHandler, wsWrapper.CloseHandler {
    private static final Logger LOG = Logger.getLogger(EventBusClient.class.getName());
    private String eventBusURI;
    private wsWrapper wsWrapper;
    private BlockingQueue<Event> inQueue = new LinkedBlockingQueue<>();
    private BlockingQueue<Event> outQueue = new LinkedBlockingQueue<>();
    private ProducerThread producerThread;

    public EventBusClient(String eventBusURI) throws RuntimeException {
        this.eventBusURI = eventBusURI;
        this.wsWrapper = new wsWrapper(URI.create(eventBusURI));

        this.producerThread = new ProducerThread();
    }

    public void start() {
        this.producerThread.start();
        this.wsWrapper.addMessageHandler(this);
        this.wsWrapper.addCloseHandler(this);
    }

    public void stop() {
        this.producerThread.interrupt();
        this.wsWrapper.removeMessageHandler();
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
//        stop();
//
//        this.wsWrapper = new wsWrapper(URI.create(eventBusURI));
//        this.producerThread = new ProducerThread();
//
//        start();
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
