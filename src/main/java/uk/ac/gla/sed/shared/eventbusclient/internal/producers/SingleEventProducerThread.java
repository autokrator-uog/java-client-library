package uk.ac.gla.sed.shared.eventbusclient.internal.producers;

import uk.ac.gla.sed.shared.eventbusclient.api.Event;
import uk.ac.gla.sed.shared.eventbusclient.internal.messages.NewEventMessage;
import uk.ac.gla.sed.shared.eventbusclient.internal.websockets.wsWrapper;

import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;
import java.util.logging.Logger;

public class SingleEventProducerThread extends ProducerThread {
    private static final Logger LOG = Logger.getLogger(SingleEventProducerThread.class.getName());

    public SingleEventProducerThread(wsWrapper wsWrapper, BlockingQueue<Event> outQueue) {
        super(wsWrapper, outQueue);

    }

    @Override
    public void run() {
        while (true) {
            try {
                Event nextEvent = getOutQueue().take();

                ArrayList<Event> pending = new ArrayList<>();
                pending.add(nextEvent);
                NewEventMessage message = new NewEventMessage(pending);

                getWsWrapper().sendMessage(message);
            } catch (InterruptedException interrupt) {
                LOG.warning("SingleEventProducerThread stopping!");
                return;
            }
        }
    }
}
