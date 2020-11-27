package bgu.spl.mics;

import bgu.spl.mics.application.messages.AttackEvent;
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

    @BeforeEach
    void setUp() {
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
    void unregister() {
        //TODO :: make sure all the massage in m's Q  go back to main Q
        //TODO :: make sure the Q is deleted
        //TODO :: make sure all the other Q are still there
    }

    @Test
    void awaitMessage() {
        //TODO :: verify m's is register
        //TODO :: make sure the method is waiting for the next massage
        //TODO :: the method is get back available when a massage i push to the Q
        //TODO :: get the massage and bring it to my Q and get out of the m's Q in the MB
        //TODO :: throw Exception if m's isn'T registered

    }
}