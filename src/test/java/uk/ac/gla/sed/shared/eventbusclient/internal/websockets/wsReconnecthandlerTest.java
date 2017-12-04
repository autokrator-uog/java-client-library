package uk.ac.gla.sed.shared.eventbusclient.internal.websockets;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.websocket.CloseReason;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class wsReconnecthandlerTest {
    private static final int RESET_COUNT = 3;

    private wsReconnectHandler handler;

    @BeforeEach
    void setUp() {
        handler = new wsReconnectHandler(RESET_COUNT);
    }

    @Test
    void testDelayIsAlwaysOne() {
        assertEquals(1, handler.getDelay());
    }


    @Test
    void testOnDisconnect() {
        CloseReason reason = mock(CloseReason.class);

        for (int i = 0; i < RESET_COUNT; i++) {
            assertTrue(handler.onDisconnect(reason));
        }

        assertFalse(handler.onDisconnect(reason));
    }

    @Test
    void testOnConnectFailure() {
        Exception e = new Exception("TEST EXCEPTION");

        for (int i = 0; i < RESET_COUNT; i++) {
            assertTrue(handler.onConnectFailure(e));
        }

        assertFalse(handler.onConnectFailure(e));
    }
}
