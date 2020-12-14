package bgu.spl.mics.application.services;

import bgu.spl.mics.Callback;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.application.passiveObjects.*;

import java.util.concurrent.CountDownLatch;

/**
 * R2D2Microservices is in charge of the handling {@link DeactivationEvent}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link DeactivationEvent}.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class R2D2Microservice extends MicroService {
    private long duration;
    private Diary diary;
    private CountDownLatch init;

    public R2D2Microservice(long duration, CountDownLatch init) {
        super("R2D2");
        this.duration = duration;
        diary = Diary.getInstance();
        this.init = init;
    }

    @Override
    protected void initialize() {
        Callback<TerminateBroadcast> terminated = c -> {
            diary.setR2D2Terminate(System.currentTimeMillis());
            terminate();
        };
        this.subscribeBroadcast(TerminateBroadcast.class, terminated);

        Callback<DeactivationEvent> deactivationCallback = c -> {
            try {
                Thread.sleep(duration);
                diary.setR2D2Deactivate(System.currentTimeMillis());
                complete(c, true);
            }
            catch (InterruptedException e) {}
        };
        this.subscribeEvent(DeactivationEvent.class, deactivationCallback);

        init.countDown();
    }
}