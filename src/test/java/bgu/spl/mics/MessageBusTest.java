package bgu.spl.mics;

import bgu.spl.mics.application.messages.AttackEvent;
import bgu.spl.mics.application.messages.BroadcastImpl;
import bgu.spl.mics.application.services.*;
import bgu.spl.mics.example.messages.ExampleBroadcast;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;

import static org.junit.jupiter.api.Assertions.*;

class MessageBusTest {
    MessageBus mb;
    Event<Boolean> attackEvent;
    R2D2Microservice R2D2;
    Broadcast broadcast;

    @BeforeEach
    void setUp() {
        attackEvent = new AttackEvent();
        R2D2 = new R2D2Microservice(1);
        mb = new MessageBusImpl();
        broadcast = new BroadcastImpl();
      MessageBusImpl mytest;
      Event <Boolean> first = new AttackEvent();
      R2D2Microservice R2D2 = new R2D2Microservice(1);
      HashMap <Event, Queue> EventHash;
      LinkedList<Queue> BroadcastList;
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void subscribeEvent() {
        //TODO :: make sure the add the hash table the right Ms + Event type
    }

    @Test
    void subscribeBroadcast() {
        //TODO :: make sure the add the hash table the right Ms + Event type
    }

    @Test
    void complete() {
        //TODO :: verify get the right result and bring back the event is finsh
    }

    @Test
    void sendBroadcast() {
        MessageBusImpl a = new MessageBusImpl();
        Broadcast B = new ExampleBroadcast("mytest");
        Event <Boolean> first = new AttackEvent();
        LinkedList<Queue> BroadcastList = null;
     //   Queue<Event> firstQ = null;
     //   Queue<Event> secondQ = null;
        BroadcastList.add(firstQ);
        BroadcastList.add(secondQ);
        sendBroadcast();

        //TODO :: verify the massage has enter the right Q AND THE Q didnt lose the data he had before
        //TODO :: after sending make sure event is deleted from the primary Q (and that he didnt change the other data)
    }

    @Test
    void sendEvent() {
        int a =5;
        Event <Boolean> first = new AttackEvent();
        R2D2Microservice R2D2 = new R2D2Microservice(1);


        //TODO :: make sure the that round-robin works, and that the massage go to the map table, check the Q
        //TODO :: make sure return null when no micro-service has subscribed
        //TODO :: after sending make sure event is deleted from the primary Q (and that he didnt change the other data)
    }

    @Test
    void register() {

        //TODO :: verify create ms-Q and its empty, and verify that Q go to the right KEY in Hash
    }

    @Test
    void awaitMessage() {
        // R2D2 is not registered. Should throw IllegalStateException:
        assertThrows(IllegalStateException.class, ()->mb.awaitMessage(R2D2));
        //TODO :: make sure the method is waiting for the next massage
        //TODO :: the method is get back available when a massage i push to the Q

        mb.register(R2D2);
        mb.subscribeEvent(AttackEvent.class, R2D2);
        // TODO: try awaitMessage and make sure it waits (how?)
        mb.sendBroadcast(broadcast);
        // TODO: try awaitMessage - expect to get nothing (waits)
        assertThrows(InterruptedException.class, ()->mb.awaitMessage(R2D2)); // This maybe?
        mb.sendEvent(attackEvent);
        assertDoesNotThrow(()->{Message m = mb.awaitMessage(R2D2);});
        // TODO: try awaitMessage - expect to get the event
        // TODO: make sure the event no longer appears in R2D2's Q and in the main Q
    }
}