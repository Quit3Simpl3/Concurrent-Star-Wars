package bgu.spl.mics.application.services;


import bgu.spl.mics.Callback;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.AttackEvent;
import bgu.spl.mics.application.messages.TerminateBroadcast;
import bgu.spl.mics.application.passiveObjects.Attack;
import bgu.spl.mics.application.passiveObjects.Diary;
import bgu.spl.mics.application.passiveObjects.Ewoks;

/**
 * HanSoloMicroservices is in charge of the handling {@link AttackEvent}s.
 * This class may not hold references for objects which it is not responsible for:
 * {@link AttackEvent}s.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class HanSoloMicroservice extends MicroService {
    private Diary diary;
    private Ewoks ewoks;


    /*
     * Local field: timestamp, Leia sends DeactivationEvent and RecordDiaryBroadcast, then
     * C3PO and HanSolo both write to Diary.
     * myAttacksCounter
     * */

    public HanSoloMicroservice() {
        super("Han");
        diary = Diary.getInstance();
        ewoks = Ewoks.getInstance();
    }

    /*
     * taskAttack() {...attack}
     * taskDiary() {write last timestamp and attacks}
     * */

    @Override
    protected void initialize() {
        Callback<AttackEvent> myAttack = new Callback<AttackEvent>() {
            @Override
            public void call(AttackEvent c) {
                Attack attack = c.GetAttack();
                ewoks.getEwoks(attack.GetSerials());
                    try {
                        Thread.sleep(attack.GetDuration());
                        diary.setHanSoloFinish(System.currentTimeMillis());
                        diary.addTotalAttacks(1);
                        complete(c, true);
                        ewoks.finsh(attack.GetSerials());
                    } catch (InterruptedException e) {
                        ewoks.finsh(attack.GetSerials());
                        call(c);
                    }
                }
        };
        subscribeEvent(AttackEvent.class,myAttack);

        Callback<TerminateBroadcast> terminated = new Callback<TerminateBroadcast>(){
            @Override
            public void call(TerminateBroadcast c) {
                terminate();
                diary.setHanSoloTerminate(System.currentTimeMillis());

            }
        };
        this.subscribeBroadcast(TerminateBroadcast.class,terminated);
    }
}
