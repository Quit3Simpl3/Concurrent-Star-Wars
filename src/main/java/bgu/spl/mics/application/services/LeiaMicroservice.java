package bgu.spl.mics.application.services;

import java.util.*;
import java.util.concurrent.TimeUnit;

import bgu.spl.mics.Callback;
import bgu.spl.mics.Future;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.BombDestroyerEvent;
import bgu.spl.mics.application.messages.DeactivationEvent;
import bgu.spl.mics.application.messages.TerminateBroadcast;
import bgu.spl.mics.application.passiveObjects.Attack;
import bgu.spl.mics.application.messages.AttackEvent;
import bgu.spl.mics.application.passiveObjects.Diary;

/**
 * LeiaMicroservices Initialized with Attack objects, and sends them as  {@link AttackEvent}s.
 * This class may not hold references for objects which it is not responsible for:
 * {@link AttackEvent}s.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class LeiaMicroservice extends MicroService {
	private Attack[] attacks;
	private Diary diary;
	private Future[] attackFutures;

    public LeiaMicroservice(Attack[] attacks) {
        super("Leia");
		this.attacks = attacks;
		attackFutures = new Future[attacks.length];
		diary = Diary.getInstance();
    }

    private boolean awaitAttacks() {
        Boolean result = null;
        Queue<Future<Boolean>> futuresQueue = new LinkedList<>();
        // Enqueue attack future objects:
        for (Future<Boolean> future : this.attackFutures) {
            futuresQueue.add(future);
        }
        while (!futuresQueue.isEmpty()) {
            Future<Boolean> future = futuresQueue.remove();
            result = null;
            if (future.isDone())
                  result = future.get(5, TimeUnit.MILLISECONDS);

            if (Objects.isNull(result)) // If attack isn't finished, put the future back in the queue to be checked again.
                futuresQueue.add(future);
        }
        return true;
    }

    private boolean sendDeactivationEvent() {
        Future<Boolean> future = sendEvent(new DeactivationEvent());
        return future.get(); // Wait until R2D2 finishes.
    }

    private boolean sendBombDestroyerEvent() {
        Future<Boolean> future = sendEvent(new BombDestroyerEvent());
        return future.get(); // Wait until Lando finishes.
    }

    private void terminateLeia() {
        terminate();
        diary.setLeiaTerminate(System.currentTimeMillis());
    }

    @Override
    protected void initialize() {
        Callback<TerminateBroadcast> terminated = c -> terminateLeia();
        this.subscribeBroadcast(TerminateBroadcast.class, terminated);

        // Send attack events and save their Future objects:
        for (int i = 0; i < attackFutures.length ;i++) {
            Future<Boolean> future = null;
            while (Objects.isNull(future)) // No one received the message
                future = sendEvent(new AttackEvent(attacks[i])); // Send the message again until someone receives it
            this.attackFutures[i] = future;
        }

        // Await the attacks' results:
        if (awaitAttacks())
            if (sendDeactivationEvent())
                if(sendBombDestroyerEvent())
                    sendBroadcast(new TerminateBroadcast("Leia"));
    }
}