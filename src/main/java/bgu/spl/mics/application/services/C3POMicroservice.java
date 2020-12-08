package bgu.spl.mics.application.services;

import bgu.spl.mics.Callback;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.AttackEvent;
import bgu.spl.mics.application.messages.TerminateBroadcast;
import bgu.spl.mics.application.passiveObjects.*;

import java.util.concurrent.CountDownLatch;


/**
 * C3POMicroservices is in charge of the handling {@link AttackEvent}s.
 * This class may not hold references for objects which it is not responsible for:
 * {@link AttackEvent}s.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */


public class C3POMicroservice extends MicroService {

    private Diary diary;
    private Ewoks ewoks;
    private CountDownLatch init;

    // TODO: DELETE BEFORE SUBMITTING!!!
    public C3POMicroservice() {
        super("C3PO");
        diary = Diary.getInstance();
        ewoks = Ewoks.getInstance();
    }
    // TODO: DELETE BEFORE SUBMITTING!!!
	
    public C3POMicroservice(CountDownLatch init) {
        super("C3PO");
        diary =Diary.getInstance();
        ewoks =Ewoks.getInstance();
        this.init = init;
    }

    @Override
    protected void initialize() {
        Callback<AttackEvent> myAttack = new Callback<AttackEvent>() {
            @Override
            public void call(AttackEvent c) {

                // TODO: DELETE BEFORE SUBMITTING!!!
                System.out.println("C3PO: attacking.");
                // TODO: DELETE BEFORE SUBMITTING!!!

                Attack attack = c.getAttack();
                ewoks.acquireEwoks(attack.GetSerials());
                try {
                    Thread.sleep(attack.GetDuration());
                }
                catch (InterruptedException e) { /*TODO: see if we need to handle exe during an attack*/}
                finally {
                    diary.updateC3PO(1,System.currentTimeMillis(),0);
                    System.out.println("C3PO send complete");
                    complete(c, true);

                    ewoks.releaseEwoks(attack.GetSerials());
                    System.out.println("C3PO released ewoks");
                }
            }
        };
        subscribeEvent(AttackEvent.class, myAttack);


        Callback<TerminateBroadcast> terminated = new Callback<TerminateBroadcast>(){
            @Override
            public void call(TerminateBroadcast c) {
                terminate();
                diary.updateC3PO(0,0,System.currentTimeMillis());
            }
        };
        this.subscribeBroadcast(TerminateBroadcast.class,terminated);
        init.countDown();
    }
}
