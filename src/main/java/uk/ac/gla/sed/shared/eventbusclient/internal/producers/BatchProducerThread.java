package uk.ac.gla.sed.shared.eventbusclient.internal.producers;

import uk.ac.gla.sed.shared.eventbusclient.api.Event;
import uk.ac.gla.sed.shared.eventbusclient.internal.messages.NewEventMessage;
import uk.ac.gla.sed.shared.eventbusclient.internal.websockets.wsWrapper;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.logging.Logger;

public class BatchProducerThread extends ProducerThread {
    private static final Logger LOG = Logger.getLogger(BatchProducerThread.class.getName());
    private static final int DEFAULT_MAX_NUMBER_OF_EVENTS = 5;

    private final int maxNumberOfEvents;
    private final List<Event> pending = new ArrayList<>();

    @SuppressWarnings("WeakerAccess")
    public BatchProducerThread(wsWrapper wsWrapper, BlockingQueue<Event> outQueue) {
        this(wsWrapper, outQueue, DEFAULT_MAX_NUMBER_OF_EVENTS);
    }

    @SuppressWarnings("WeakerAccess")
    public BatchProducerThread(wsWrapper wsWrapper, BlockingQueue<Event> outQueue, int maxNumberOfEvents) {
        super(wsWrapper, outQueue);
        this.maxNumberOfEvents = maxNumberOfEvents;
    }

    @Override
    public void run() {
        while (true) {
            try {
                synchronized (getOutQueue()) {
                    while (getOutQueue().isEmpty())
                        getOutQueue().wait();

                    // select at most the max batch size of events
                    getOutQueue().drainTo(pending, maxNumberOfEvents);

                    getOutQueue().notify();
                }

                // send the events in the payload of a NewEventMessage
                NewEventMessage message = new NewEventMessage(pending);
                getWsWrapper().sendMessage(message);
                pending.clear();

            } catch (InterruptedException interrupt) {
                LOG.warning("BatchProducerThread interrupted... stopping.");
                return;
            } catch (Exception e) {
                LOG.severe("Error in BatchProducerThread: " + e.toString());
                e.printStackTrace();
            }
        }
    }
}
