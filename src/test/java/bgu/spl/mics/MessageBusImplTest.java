package bgu.spl.mics;

import bgu.spl.mics.application.messages.AttackEvent;
import bgu.spl.mics.application.services.*;
import bgu.spl.mics.example.messages.ExampleBroadcast;
import bgu.spl.mics.example.messages.ExampleEvent;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MessageBusImplTest {

    Message testMsg1;
    Message testMsg2;
    MessageBus mb;
    MicroService c3po;
    MicroService han;
    MicroService lando;
    Broadcast broadTest;
    Event exampleEvent1, exampleEvent2, exampleEvent3;

    @BeforeEach
    void setUp() {
        testMsg1 = null;
        testMsg2 = null;
        exampleEvent1 = new ExampleEvent("test_event1");
        exampleEvent2 = new ExampleEvent("test_event2");
        exampleEvent3 = new ExampleEvent("test_event3");
        mb = new MessageBusImpl();
        c3po = new C3POMicroservice();
        han = new HanSoloMicroservice();
        lando = new LandoMicroservice(10);
        broadTest = new ExampleBroadcast("test_broadcast");
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void testSubscribeEvent() {
    }

    @Test
    void testSubscribeBroadcast() {
    }

    @Test
    void testComplete() {
    }

    @Test
    void testSendBroadcast() {
        // Register Han and C3PO to the message bus:
        mb.register(han);
        mb.register(c3po);
        // Subscribe Han and C3PO to broadTest in the message bus:
        mb.subscribeBroadcast(broadTest.getClass(),han);
        mb.subscribeBroadcast(broadTest.getClass(),c3po);
        // Test awaitMessage without a message waiting:
        try {
            testMsg1 = mb.awaitMessage(han);
            testMsg2 = mb.awaitMessage(c3po);
        }
        catch (InterruptedException e) {
            assertNull(testMsg1);
            assertNull(testMsg2);
        }
        // Test awaitMessage with a message waiting:
        mb.sendBroadcast(broadTest);
        try {
            testMsg1 = mb.awaitMessage(han); // Receive the broadcast for han
            testMsg2 = mb.awaitMessage(c3po); // Receive the broadcast for c3po
        }
        catch (InterruptedException e) {
            fail(); // Han should receive a message now, otherwise something is wrong.
        }
        // Assert that both han AND c3po received the broadcast message:
        assertEquals(
                ((ExampleBroadcast)testMsg1).getSenderId(),
                ((ExampleBroadcast)broadTest).getSenderId()
        );
        assertEquals(
                ((ExampleBroadcast)testMsg2).getSenderId(),
                ((ExampleBroadcast)broadTest).getSenderId()
        );
    }

    @Test
    void testSendEvent() {
        // Register Han and C3PO to the message bus:
        mb.register(han);
        mb.register(c3po);
        // Subscribe Han and C3PO to AttackEvent type in the message bus:
        mb.subscribeEvent(AttackEvent.class, han);
        mb.subscribeEvent(AttackEvent.class, c3po);
        // Test awaitMessage without a message waiting:
        try {
            testMsg1 = mb.awaitMessage(han);
        }
        catch (InterruptedException e) {
            assertNull(testMsg1);
        }
        // Test awaitMessage with ONE message waiting:
        mb.sendEvent(exampleEvent1);
        try {
            testMsg1 = mb.awaitMessage(han);
            testMsg2 = mb.awaitMessage(c3po);
        }
        catch (InterruptedException e) {
            // Make sure han received a message:
            assertEquals(
                    ((ExampleEvent)testMsg1).getSenderName(),
                    ((ExampleEvent)exampleEvent1).getSenderName()
                );
            // Make sure c3po didn't receive a message (Event is received only once):
            assertNull(testMsg2);
        }
        // Test awaitMessage with TWO messages waiting:
        mb.sendEvent(exampleEvent2);
        mb.sendEvent(exampleEvent3);
        try {
            testMsg1 = mb.awaitMessage(han);
            testMsg2 = mb.awaitMessage(c3po);
        }
        catch (InterruptedException e) {
            fail(); // If the awaitMessage didn't return a message twice it therefore failed to work propertly.
        }
        // Make sure han AND c3po received each a single, different message:
        assertEquals(
                ((ExampleEvent)testMsg1).getSenderName(),
                ((ExampleEvent)exampleEvent2).getSenderName()
        );
        assertEquals(
                ((ExampleEvent)testMsg2).getSenderName(),
                ((ExampleEvent)exampleEvent3).getSenderName()
        );
    }

    @Test
    void testRegister() { // TODO: Check whether we need to test this.
    }


    @Test
    void testAwaitMessage() {
    }
}