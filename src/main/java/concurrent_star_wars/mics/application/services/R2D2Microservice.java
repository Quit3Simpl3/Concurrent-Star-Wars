package concurrent_star_wars.mics.application.services;

import concurrent_star_wars.mics.application.passiveObjects.Diary;
import concurrent_star_wars.mics.Callback;
import concurrent_star_wars.mics.MicroService;
import concurrent_star_wars.mics.application.messages.*;
import concurrent_star_wars.mics.application.passiveObjects.*;
import concurrent_star_wars.mics.application.messages.DeactivationEvent;
import concurrent_star_wars.mics.application.messages.TerminateBroadcast;

import java.util.concurrent.CountDownLatch;

/**
 * R2D2Microservices is in charge of the handling {@link DeactivationEvent}.
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