package concurrent_star_wars.mics.application.services;

import concurrent_star_wars.mics.application.passiveObjects.Diary;
import concurrent_star_wars.mics.Broadcast;
import concurrent_star_wars.mics.Callback;
import concurrent_star_wars.mics.MicroService;
import concurrent_star_wars.mics.application.messages.BombDestroyerEvent;
import concurrent_star_wars.mics.application.messages.TerminateBroadcast;

import java.util.concurrent.CountDownLatch;

/**
 * LandoMicroservice
 * Handles the BombDestroyerEvent.
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
        Callback<TerminateBroadcast> terminated = c -> {
            diary.setLandoTerminate(System.currentTimeMillis());
            terminate();
        };
        this.subscribeBroadcast(TerminateBroadcast.class, terminated);

        Callback<BombDestroyerEvent> bombDestroy = c -> {
            try {
                Thread.sleep(duration);
                complete(c, true);
            }
            catch (InterruptedException e) {}
        };
        subscribeEvent(BombDestroyerEvent.class, bombDestroy);

        init.countDown();
    }
}

