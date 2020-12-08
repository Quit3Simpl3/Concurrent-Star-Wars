package bgu.spl.mics.application.services;

import bgu.spl.mics.Broadcast;
import bgu.spl.mics.Callback;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.BombDestroyerEvent;
import bgu.spl.mics.application.messages.TerminateBroadcast;
import bgu.spl.mics.application.passiveObjects.Diary;

import java.util.concurrent.CountDownLatch;

/**
 * LandoMicroservice
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class LandoMicroservice  extends MicroService {
    private long duration;
    private Diary diary;
    Broadcast TerminateBroadcast;
    private CountDownLatch init;


    public LandoMicroservice(long duration, CountDownLatch init) {

        super("Lando");
        this.duration = duration;
        diary = Diary.getInstance();
        this.init = init;
    }

    @Override
    protected void initialize() {
        Callback<TerminateBroadcast> terminated = new Callback<TerminateBroadcast>() {
            @Override
            public void call(TerminateBroadcast c) {
                diary.setLandoTerminate(System.currentTimeMillis());
                terminate();
            }
        };
        this.subscribeBroadcast(TerminateBroadcast.class, terminated);

        Callback<BombDestroyerEvent> bombDestroy = new Callback<BombDestroyerEvent>() {
            @Override
            public void call(BombDestroyerEvent c) {
                try {
                    Thread.sleep(duration);
                    complete(c, true);
                 //   sendBroadcast(TerminateBroadcast);
                } catch (InterruptedException e) {
                }
            }

        };

        subscribeEvent(BombDestroyerEvent.class, bombDestroy);
        init.countDown();

    }
}

