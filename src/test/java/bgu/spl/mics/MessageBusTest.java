package bgu.spl.mics;

import bgu.spl.mics.application.services.*;
import bgu.spl.mics.example.messages.ExampleBroadcast;
import bgu.spl.mics.example.messages.ExampleEvent;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;

import static org.junit.jupiter.api.Assertions.*;

class MessageBusTest {
    // fields:
    Message testMsg1;
    Message testMsg2;
    MessageBus mb;
    MicroService c3po;
    MicroService han;
    MicroService lando;
    Broadcast broadTest,broadTest1;
    Event exampleEvent1, exampleEvent2, exampleEvent3;
    CountDownLatch init;


    @BeforeEach
    void setUp() {
        testMsg1 = null;
        testMsg2 = null;
        exampleEvent1 = new ExampleEvent("test_event1");
        exampleEvent2 = new ExampleEvent("test_event2");
        exampleEvent3 = new ExampleEvent("test_event3");
        mb = MessageBusImpl.getInstance();
        c3po = new C3POMicroservice(init);
        han = new HanSoloMicroservice(init);
        lando = new LandoMicroservice(10,init);
        broadTest = new ExampleBroadcast("test_broadcast");
        broadTest1 = new ExampleBroadcast("test_broadcast1");
    }

    @AfterEach
    void tearDown() {
        mb.unregister(han);
        mb.unregister(lando);
        mb.unregister(c3po);
    }

    @Test
    void testSubscribeEvent() {
        mb.register(han);
        mb.register(c3po);

        mb.sendEvent(exampleEvent1);

        mb.subscribeEvent(ExampleEvent.class, han);

        mb.sendEvent(exampleEvent1);

        try {
            testMsg1 = mb.awaitMessage(han);
        }
        catch (InterruptedException e) {
          fail();
        }

        assertNotNull(testMsg1);
        assertNull(testMsg2);

        assertEquals(((ExampleEvent)testMsg1).getSenderName(),((ExampleEvent)exampleEvent1).getSenderName());
    }

    @Test
    void testSubscribeBroadcast() {
        mb.register(han);
        mb.register(c3po);

        mb.sendBroadcast(broadTest);

        mb.subscribeBroadcast(broadTest.getClass(), han);
        mb.subscribeBroadcast(broadTest.getClass(), c3po);
        mb.sendBroadcast(broadTest1);

        try {
            testMsg1 = mb.awaitMessage(han);
            testMsg2 = mb.awaitMessage(c3po);
        } catch (InterruptedException e) {
            assertNotNull(testMsg1);
            assertNotNull(testMsg2);
        }
        try {
            assertEquals(
                    ((ExampleBroadcast) testMsg1).getSenderId(),
                    ((ExampleBroadcast) broadTest1).getSenderId(),
                    ((ExampleBroadcast) testMsg2).getSenderId()
            );
        } catch (NullPointerException e) {
            fail();
        }
    }

    @Test
    void testComplete() {
        // 1. register han
        // 2. han subscribe event
        // 3. han does mb.complete(event, some_result)
        // 4. make sure future.get() gives some_result

        mb.register(han); // Register han to MessageBus
        mb.subscribeEvent(ExampleEvent.class, han); // Subscribe han to ExampleEvent type
        Future<String> future = mb.sendEvent(exampleEvent1); // han should receive this event
        assertDoesNotThrow(()->{ testMsg1 = mb.awaitMessage(han); });  // awaitMessage should work properly
        String han_result = "My name is Han and I am done.";
        mb.complete(exampleEvent1, han_result); // mb resolves future with han_result
        try {
            String result_from_mb = future.get(); // should contain han_result
            assertEquals(result_from_mb, han_result);
        }
        catch (NullPointerException e) {
            fail();
        }

    }

    @Test
    void testSendBroadcast() {
        // Register Han and C3PO to the message bus:
        mb.register(han);
        mb.register(c3po);
        // Subscribe Han and C3PO to broadTest in the message bus:
        mb.subscribeBroadcast(broadTest.getClass(), han);
        mb.subscribeBroadcast(broadTest.getClass(), c3po);
        // Test awaitMessage without a message waiting:
        /*try {
            testMsg1 = mb.awaitMessage(han);
            testMsg2 = mb.awaitMessage(c3po);
        } catch (InterruptedException e) {
            assertNull(testMsg1);
            assertNull(testMsg2);
        }*/
        // Test awaitMessage with a message waiting:
        mb.sendBroadcast(broadTest);
        try {
            testMsg1 = mb.awaitMessage(han); // Receive the broadcast for han
            testMsg2 = mb.awaitMessage(c3po); // Receive the broadcast for c3po
        } catch (InterruptedException e) {
            fail(); // Han should receive a message now, otherwise something is wrong.
        }
        // Assert that both han AND c3po received the broadcast message:
        try {
            assertEquals(
                    ((ExampleBroadcast) testMsg1).getSenderId(),
                    ((ExampleBroadcast) broadTest).getSenderId()
            );
            assertEquals(
                    ((ExampleBroadcast) testMsg2).getSenderId(),
                    ((ExampleBroadcast) broadTest).getSenderId()
            );
        } catch (NullPointerException e) {
            fail();
        }
    }

    @Test
    void testSendEvent() {
        // Register Han and C3PO to the message bus:
        mb.register(han);
        mb.register(c3po);
        // Subscribe Han and C3PO to AttackEvent type in the message bus:
        mb.subscribeEvent(ExampleEvent.class, han);
        mb.subscribeEvent(ExampleEvent.class, c3po);
        // Test awaitMessage without a message waiting:
        /*try {
            testMsg1 = mb.awaitMessage(han);
        }
        catch (InterruptedException e) {
            assertNull(testMsg1);
        }*/
        // Test awaitMessage with ONE message waiting:
        /*mb.sendEvent(exampleEvent1);
        try {
            testMsg1 = mb.awaitMessage(han);
            testMsg2 = mb.awaitMessage(c3po);
        }
        catch (InterruptedException e) {
            // Make sure ONLY ONE received a message:
            String msg_han = ((ExampleEvent)testMsg1).getSenderName();
            String msg_c3po = ((ExampleEvent)testMsg2).getSenderName();
            String example_event1 = ((ExampleEvent)exampleEvent1).getSenderName();
            // Condition:
            boolean cond = (
                (testMsg2 == null && ((ExampleEvent)testMsg1).getSenderName().equals(example_event1))
                ||
                (testMsg1 == null && ((ExampleEvent)testMsg2).getSenderName().equals(example_event1))
            );
            assertTrue(cond);
        }*/
        // Test awaitMessage with TWO messages waiting:
        mb.sendEvent(exampleEvent2);
        mb.sendEvent(exampleEvent3);
        try {
            testMsg1 = mb.awaitMessage(han);
            testMsg2 = mb.awaitMessage(c3po);
        }
        catch (InterruptedException e) {
            fail(); // If the awaitMessage didn't return a message twice it therefore failed to work properly.
        }
        // Make sure han AND c3po received each a single, different message:


         try{

        boolean cond = (
            (
                ((ExampleEvent)testMsg1).getSenderName().equals(((ExampleEvent)exampleEvent2).getSenderName())
                &&
                ((ExampleEvent)testMsg2).getSenderName().equals(((ExampleEvent)exampleEvent3).getSenderName())
            )
            ||
            (
                ((ExampleEvent)testMsg1).getSenderName().equals(((ExampleEvent)exampleEvent3).getSenderName())
                &&
                ((ExampleEvent)testMsg2).getSenderName().equals(((ExampleEvent)exampleEvent2).getSenderName())
            )
        );
        assertTrue(cond);
    }
        catch (NullPointerException e) {
        fail();
        }

    }


    @Test
    void testAwaitMessage() {
        // c3po is not registered. Should throw IllegalStateException:
        assertThrows(IllegalStateException.class, ()->mb.awaitMessage(c3po));
        // register c3po:
        mb.register(c3po);
        // c3po is registered. should receive the broadcast msg:
        mb.subscribeBroadcast(ExampleBroadcast.class, c3po);
        mb.sendBroadcast(broadTest);
        try {
            testMsg1 = mb.awaitMessage(c3po);
        }
        catch (InterruptedException e) {
            fail(); // shouldn't throw an exception
        }
        assertEquals(
                ((ExampleBroadcast)testMsg1).getSenderId(),
                ((ExampleBroadcast)broadTest).getSenderId()
        );
        // c3po is registered. should receive the event msg:
        mb.subscribeEvent(ExampleEvent.class, c3po);
        mb.sendEvent(exampleEvent1);
        try {
            testMsg2 = mb.awaitMessage(c3po);
        }
        catch (InterruptedException e) {
            fail(); // shouldn't throw an exception
        }
        assertEquals(
                ((ExampleEvent)testMsg2).getSenderName(),
                ((ExampleEvent)exampleEvent1).getSenderName()
        );
    }
}