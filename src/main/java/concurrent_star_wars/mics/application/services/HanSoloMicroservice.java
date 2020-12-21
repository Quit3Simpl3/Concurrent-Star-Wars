package concurrent_star_wars.mics.application.services;


import concurrent_star_wars.mics.application.passiveObjects.Attack;
import concurrent_star_wars.mics.application.passiveObjects.Diary;
import concurrent_star_wars.mics.application.passiveObjects.Ewoks;
import concurrent_star_wars.mics.Callback;
import concurrent_star_wars.mics.MicroService;
import concurrent_star_wars.mics.application.messages.AttackEvent;
import concurrent_star_wars.mics.application.messages.TerminateBroadcast;

import java.util.concurrent.CountDownLatch;

/**
 * HanSoloMicroservices is in charge of the handling {@link AttackEvent}s.
 * {@link AttackEvent}s.
 */

public class HanSoloMicroservice extends MicroService {
    private Diary diary;
    private Ewoks ewoks;
    private CountDownLatch init;

    public HanSoloMicroservice(CountDownLatch init) {
        super("Han");
        diary = Diary.getInstance();
        ewoks = Ewoks.getInstance();
        this.init = init;
    }

    @Override
    protected void initialize() {
        Callback<AttackEvent> myAttack = c -> {
            Attack attack = c.getAttack();

            ewoks.acquireEwoks(attack.GetSerials());

            try {
                Thread.sleep(attack.GetDuration());
            }
            catch (InterruptedException e) {}
            finally {
                diary.updateHanSolo(1,System.currentTimeMillis(),0);
                complete(c, true);
                ewoks.releaseEwoks(attack.GetSerials());
            }
        };
        subscribeEvent(AttackEvent.class, myAttack);

        Callback<TerminateBroadcast> terminated = c -> {
            terminate();
            diary.updateHanSolo(0,0,System.currentTimeMillis());
        };
        this.subscribeBroadcast(TerminateBroadcast.class,terminated);

        init.countDown();
    }
}
