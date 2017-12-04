package uk.ac.gla.sed.shared.eventbusclient.internal.producers;

import uk.ac.gla.sed.shared.eventbusclient.api.Event;
import uk.ac.gla.sed.shared.eventbusclient.internal.websockets.wsWrapper;

import java.util.concurrent.BlockingQueue;

public abstract class ProducerThread extends Thread {
    private final uk.ac.gla.sed.shared.eventbusclient.internal.websockets.wsWrapper wsWrapper;
    private final BlockingQueue<Event> outQueue;

    ProducerThread(wsWrapper wsWrapper, BlockingQueue<Event> outQueue) {
        this.wsWrapper = wsWrapper;
        this.outQueue = outQueue;
    }

    @Override
    public abstract void run();

    wsWrapper getWsWrapper() {
        return wsWrapper;
    }

    BlockingQueue<Event> getOutQueue() {
        return outQueue;
    }
}
