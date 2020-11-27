package bgu.spl.mics;

import bgu.spl.mics.application.messages.AttackEvent;
import bgu.spl.mics.application.services.*;
import bgu.spl.mics.example.messages.ExampleBroadcast;
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

    @BeforeEach
    void setUp() {
        testMsg1 = null;
        testMsg2 = null;
        mb = new MessageBusImpl();
        c3po = new C3POMicroservice();
        han = new HanSoloMicroservice();
        lando = new LandoMicroservice(10);
        broadTest = new ExampleBroadcast("test");
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void subscribeEvent() {
    }

    @Test
    void subscribeBroadcast() {
    }

    @Test
    void complete() {
    }

    @Test
    void sendBroadcast() {
        // Register han and c3po to the message bus:
        mb.register(han);
        mb.register(c3po);

        mb.subscribeBroadcast(broadTest.getClass(),han);
        mb.subscribeBroadcast(broadTest.getClass(),c3po);
        // Test awaitMessage without a message waiting:
        try {
            testMsg1 = mb.awaitMessage(han);
        } catch (InterruptedException e){
            assertNull(testMsg1);
        };
        // Test awaitMessage with a message waiting:
        mb.sendBroadcast(broadTest);
        try {
            testMsg1 = mb.awaitMessage(han);
            assertEquals(((ExampleBroadcast)testMsg1).getSenderId(),((ExampleBroadcast)broadTest).getSenderId());
        }
        catch (InterruptedException e) {
            fail(); // Han should receive a message now, otherwise something is wrong.
        }


    }

    @Test
    void sendEvent() {
        //TODO :: COPY FROM BROADCAST
    }

    @Test
    void register() {
    }


    @Test
    void awaitMessage() {
    }
}