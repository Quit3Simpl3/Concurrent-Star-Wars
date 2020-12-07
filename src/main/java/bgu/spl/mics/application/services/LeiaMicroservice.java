package bgu.spl.mics.application.services;

import java.util.ArrayList;
import java.util.List;

import bgu.spl.mics.Callback;
import bgu.spl.mics.Event;
import bgu.spl.mics.MicroService;
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
	private Object [][] results;
	private int counter;

	
    public LeiaMicroservice(Attack[] attacks) {
        super("Leia");
		this.attacks = attacks;
		diary = Diary.getInstance();
        results = new Object[attacks.length][3];   // 0 - Attack, 1 - AttackEvent, 2- future
        for (int i = 0; i<attacks.length;i++) {
            results[i][0] = attacks[i];
            results[i][1] = new AttackEvent(attacks[i]);
        }
        counter = attacks.length;
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

        for (int j=0 ;j<=counter ;j++) {
            results[j][2] = sendEvent((Event)results[j][1]);

        }


    }
}
