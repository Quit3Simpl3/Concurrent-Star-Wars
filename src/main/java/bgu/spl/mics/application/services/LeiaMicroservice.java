package bgu.spl.mics.application.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import bgu.spl.mics.Callback;
import bgu.spl.mics.Event;
import bgu.spl.mics.Future;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.TerminateBroadcast;
import bgu.spl.mics.application.passiveObjects.Attack;
import bgu.spl.mics.application.messages.AttackEvent;
import bgu.spl.mics.application.passiveObjects.Diary;
import com.sun.org.apache.xpath.internal.operations.Bool;

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

    @Override
    protected void initialize() {
        Callback<TerminateBroadcast> terminated = new Callback<TerminateBroadcast>(){
            @Override
            public void call(TerminateBroadcast c) {
                terminate();
                diary.setLeiaTerminate(System.currentTimeMillis());
            }
        };
        this.subscribeBroadcast(TerminateBroadcast.class,terminated);

        // Send attack events and save their Future objects:
        for (int i = 0; i < attackFutures.length ;i++) {
            Future<Boolean> future = null;
            while (Objects.isNull(future)) { // No one received the message
                future = sendEvent(new AttackEvent(attacks[i])); // Send the message again until someone receives it
            }
            this.attackFutures[i] = future;
        }
    }
}
