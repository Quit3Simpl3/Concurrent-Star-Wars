package concurrent_star_wars.mics.application.services;

import concurrent_star_wars.mics.application.passiveObjects.Attack;
import concurrent_star_wars.mics.application.passiveObjects.Diary;
import concurrent_star_wars.mics.application.passiveObjects.Ewoks;
import concurrent_star_wars.mics.Callback;
import concurrent_star_wars.mics.MicroService;
import concurrent_star_wars.mics.application.messages.AttackEvent;
import concurrent_star_wars.mics.application.messages.TerminateBroadcast;
import concurrent_star_wars.mics.application.passiveObjects.*;

import java.util.List;
import java.util.concurrent.CountDownLatch;


/**
 * C3POMicroservices is in charge of the handling {@link AttackEvent}s.
 * {@link AttackEvent}s.
 */

public class C3POMicroservice extends MicroService {
    private Diary diary;
    private Ewoks ewoks;
    private CountDownLatch init;
	
    public C3POMicroservice(CountDownLatch init) {
        super("C3PO");
        diary = Diary.getInstance();
        ewoks = Ewoks.getInstance();
        this.init = init;
    }

    @Override
    protected void initialize() {
        Callback<AttackEvent> myAttack = c -> {
            Attack attack = c.getAttack();

            List<Integer> serials = attack.GetSerials();
            ewoks.acquireEwoks(serials);

            try {
                Thread.sleep(attack.GetDuration());
            }
            catch (InterruptedException e) {}
            finally {
                diary.updateC3PO(1,System.currentTimeMillis(),0);
                complete(c, true);
                ewoks.releaseEwoks(attack.GetSerials());
            }
        };
        subscribeEvent(AttackEvent.class, myAttack);


        Callback<TerminateBroadcast> terminated = c -> {
            terminate();
            diary.updateC3PO(0,0,System.currentTimeMillis());
        };
        this.subscribeBroadcast(TerminateBroadcast.class,terminated);
        init.countDown();
    }
}
