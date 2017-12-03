package uk.gla.sed.shared.eventbusclient.internal.websockets;

import com.eclipsesource.json.Json;
import org.glassfish.tyrus.client.ClientManager;
import org.glassfish.tyrus.client.ClientProperties;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import uk.ac.gla.sed.shared.eventbusclient.internal.messages.Message;
import uk.ac.gla.sed.shared.eventbusclient.internal.messages.MessageType;
import uk.ac.gla.sed.shared.eventbusclient.internal.websockets.CloseHandler;
import uk.ac.gla.sed.shared.eventbusclient.internal.websockets.MessageHandler;
import uk.ac.gla.sed.shared.eventbusclient.internal.websockets.wsWrapper;

import javax.websocket.CloseReason;
import javax.websocket.RemoteEndpoint;
import javax.websocket.Session;
import java.io.IOException;
import java.net.URI;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class wsWrapperTest {
    private static URI uri = URI.create("ws://127.0.0.1");
    private static CloseReason testCloseReason = new CloseReason(CloseReason.CloseCodes.GOING_AWAY, "We are testing...");
    private static Message testMessage = new Message(MessageType.EVENT, Json.object().asObject().set("Test", "Test"));
    private wsWrapper wrapperUnderTest;
    private StubReconnectHandler reconnectHandler;
    private Session wsSession;

    @BeforeEach
    void setUp() {
        reconnectHandler = new StubReconnectHandler();
        wrapperUnderTest = new wsWrapper(uri, reconnectHandler);

        wsSession = mock(Session.class);
    }

    @AfterEach
    void tearDown() {
        wrapperUnderTest.closeSession();
    }

    @Test
    void testConstructor() {
        assertEquals(uri, wrapperUnderTest.getConnectionURI());
        assertSame(reconnectHandler, wrapperUnderTest.getReconnectHandler());

        assertNull(wrapperUnderTest.getClient());
        assertNull(wrapperUnderTest.getMessageHandler());
        assertNull(wrapperUnderTest.getCloseHandler());
        assertNull(wrapperUnderTest.getUserSession());
    }

    /*  SESSION OPENING TESTS  */

    @Test
    void testSimpleConstructor() {
        // override, using simple constructor
        wrapperUnderTest = new wsWrapper(uri);

        assertEquals(uri, wrapperUnderTest.getConnectionURI());
        assertNotNull(wrapperUnderTest.getReconnectHandler());

        assertNull(wrapperUnderTest.getClient());
        assertNull(wrapperUnderTest.getMessageHandler());
        assertNull(wrapperUnderTest.getCloseHandler());
        assertNull(wrapperUnderTest.getUserSession());
    }

    @Test
    void testOpenSessionMethodCall() {
        assertThrows(RuntimeException.class, () -> {
            wrapperUnderTest.openSession();
        });

        // client should be defined
        assertNotNull(wrapperUnderTest.getClient());

        // client should have our reconnecthandler
        assertSame(reconnectHandler, wrapperUnderTest.getClient().getProperties().get(ClientProperties.RECONNECT_HANDLER));

        // usersession should not be started
        assertNull(wrapperUnderTest.getUserSession());
    }


    /*  SESSION CLOSING TESTS  */

    @Test
    void testOnOpen() {
        wrapperUnderTest.onOpen(wsSession);
        assertSame(wsSession, wrapperUnderTest.getUserSession());
    }

    ;

    @Test
    void testOnCloseWithoutCloseHandler() {
        wrapperUnderTest.onOpen(wsSession);
        wrapperUnderTest.onClose(wsSession, testCloseReason);

        // user session nulled to be garbage collected
        assertNull(wrapperUnderTest.getUserSession());
    }

    @Test
    void testOnCloseWithCloseHandler() {
        // set up close handler
        CloseHandler mockHandler = mock(CloseHandler.class);
        wrapperUnderTest.setCloseHandler(mockHandler);

        // set up class for test
        wrapperUnderTest.onOpen(wsSession);

        // code under test
        wrapperUnderTest.onClose(wsSession, testCloseReason);

        // user session nulled to be garbage collected
        assertNull(wrapperUnderTest.getUserSession());

        // handler called
        verify(mockHandler).handleClose(testCloseReason);
    }

    @Test
    void testCloseSessionNullSession() {
        // implicit check for errors thrown
        wrapperUnderTest.closeSession();
    }

    @Test
    void testCloseSessionNormal() {
        // set up class for test
        wrapperUnderTest.onOpen(wsSession);

        // code under test
        wrapperUnderTest.closeSession();
    }

    @Test
    void testCloseSessionException() {
        // set up class for test
        try {
            Mockito.doThrow(new IOException("TEST EXCEPTION")).when(wsSession).close();
        } catch (IOException e) {
            fail("Mockito failed....");
        }
        wrapperUnderTest.onOpen(wsSession);

        // code under test
        wrapperUnderTest.closeSession();
        // tests should not throw anything
    }


    /*  MESSAGE RECEIPT TESTS  */

    @Test
    void testOnMessageNoHandler() {
        wrapperUnderTest.onMessage(testMessage.toString());
        // implicitly testing no exceptions thrown
    }

    @Test
    void testOnMessage() {
        // mock message handler
        MessageHandler handler = mock(MessageHandler.class);
        wrapperUnderTest.setMessageHandler(handler);

        // code under test
        wrapperUnderTest.onMessage(testMessage.toString());

        // verify message handler called
        ArgumentCaptor<Message> messageCaptor = ArgumentCaptor.forClass(Message.class);
        verify(handler).handleMessage(messageCaptor.capture());

        // extract the produced message and verify it's correctness
        Message producedMessage = messageCaptor.getValue();
        assertEquals(testMessage.getType(), producedMessage.getType());
        assertEquals(testMessage.getMessageJsonObject(), producedMessage.getMessageJsonObject());
    }

    @Test
    void testOnMessageInvalidJSON() {
        // mock message handler
        MessageHandler handler = mock(MessageHandler.class);
        wrapperUnderTest.setMessageHandler(handler);

        String someInvalidJson = "[";

        // code under test
        wrapperUnderTest.onMessage(someInvalidJson);
        // implicitly verifies no exception thrown!

        // verify message handler not called
        verify(handler, never()).handleMessage(any());
    }

    @Test
    void testOnErrorNullThrowable() {
        wrapperUnderTest.onError(wsSession, null);
    }

    @Test
    void testOnErrorWithThrowable() {
        Throwable mock = mock(Throwable.class);

        wrapperUnderTest.onError(wsSession, mock);

        verify(mock, atLeastOnce()).getMessage();
        verify(mock, atLeastOnce()).printStackTrace();
    }

    @Test
    void testSendMessage() {
        // set up class for test
        RemoteEndpoint.Basic basic = mock(RemoteEndpoint.Basic.class);
        when(wsSession.getBasicRemote()).thenReturn(basic);

        wrapperUnderTest.onOpen(wsSession);
        assertNotNull(wrapperUnderTest.getUserSession());

        // code under test
        wrapperUnderTest.sendMessage(testMessage);

        ArgumentCaptor<String> strCaptor = ArgumentCaptor.forClass(String.class);
        try {
            verify(basic).sendText(strCaptor.capture());
        } catch (IOException e) {
            fail("Mockito failed....");
        }
    }

    /* MESSAGE SENDING TESTS */

    @Test
    void testSendMessageError() {
        // set up class for test
        RemoteEndpoint.Basic basic = mock(RemoteEndpoint.Basic.class);
        when(wsSession.getBasicRemote()).thenReturn(basic);
        try {
            Mockito.doThrow(new IOException("TEST EXCEPTION WHILST SENDING")).when(basic).sendText(anyString());
        } catch (IOException e) {
            fail("Mockito failed....");
        }

        wrapperUnderTest.onOpen(wsSession);
        assertNotNull(wrapperUnderTest.getUserSession());

        // code under test
        wrapperUnderTest.sendMessage(testMessage);
        // implicit assert doesn't throw
    }

    private class StubReconnectHandler extends ClientManager.ReconnectHandler {
        @Override
        public boolean onDisconnect(CloseReason closeReason) {
            return false;
        }

        @Override
        public boolean onConnectFailure(Exception exception) {
            return false;
        }

        @Override
        public long getDelay() {
            return 1;
        }
    }
}
