package uk.ac.gla.sed.shared.eventbusclient.internal.producers;

import com.eclipsesource.json.Json;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import uk.ac.gla.sed.shared.eventbusclient.api.Event;
import uk.ac.gla.sed.shared.eventbusclient.internal.messages.Message;
import uk.ac.gla.sed.shared.eventbusclient.internal.messages.MessageType;
import uk.ac.gla.sed.shared.eventbusclient.internal.messages.NewEventMessage;
import uk.ac.gla.sed.shared.eventbusclient.internal.websockets.wsWrapper;

import java.lang.reflect.Constructor;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.*;

class ProducerThreadsTest {
    private static final int SLEEP_TIME_MILLISECONDS = 500;

    private wsWrapper wrapper;
    private BlockingQueue<Event> outQueue;
    private ProducerThread thread;

    @BeforeEach
    void setUp() {
        outQueue = new LinkedBlockingQueue<>();
        wrapper = mock(wsWrapper.class);
    }

    // Use Reflection to get an instance of the desired class...
    private ProducerThread getThreadInstance(String className) throws Exception {
        Class<?> threadClass = Class.forName(className);
        Constructor<?> ctor = threadClass.getConstructor(wsWrapper.class, BlockingQueue.class);
        return (ProducerThread) ctor.newInstance(new Object[]{wrapper, outQueue});
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "uk.ac.gla.sed.shared.eventbusclient.internal.producers.SingleEventProducerThread",
            "uk.ac.gla.sed.shared.eventbusclient.internal.producers.BatchProducerThread"
    })
    void testNoEvents(String threadClassName) {
        try {
            thread = getThreadInstance(threadClassName);
        } catch (Exception e) {
            fail(e);
        }

        thread.start();

        try {
            Thread.sleep(SLEEP_TIME_MILLISECONDS);
        } catch (InterruptedException interrupt) {
            fail(interrupt);
        }

        thread.interrupt();

        verify(wrapper, never()).sendMessage(any());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "uk.ac.gla.sed.shared.eventbusclient.internal.producers.SingleEventProducerThread",
            "uk.ac.gla.sed.shared.eventbusclient.internal.producers.BatchProducerThread"
    })
    void testWithEvent(String threadClassName) {
        try {
            thread = getThreadInstance(threadClassName);
        } catch (Exception e) {
            fail(e);
        }

        Event event = new Event("TestEventType", Json.object().asObject().set("Test", "Test"));
        outQueue.add(event);

        thread.start();
        try {
            Thread.sleep(SLEEP_TIME_MILLISECONDS);
        } catch (InterruptedException interrupt) {
            fail(interrupt);
        }
        thread.interrupt();

        ArgumentCaptor<Message> messageCaptor = ArgumentCaptor.forClass(Message.class);
        verify(wrapper, atLeastOnce()).sendMessage(messageCaptor.capture());
        Message message = messageCaptor.getValue();

        assertEquals(MessageType.NEW, message.getType());
        assertEquals(event.getFullEventObject(), message.getMessageJsonObject().get(NewEventMessage.EVENTS_FIELD_NAME).asArray().get(0));
    }
}
