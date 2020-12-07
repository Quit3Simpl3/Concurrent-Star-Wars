package bgu.spl.mics.application.services;


import bgu.spl.mics.Callback;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.AttackEvent;
import bgu.spl.mics.application.messages.TerminateBroadcast;
import bgu.spl.mics.application.passiveObjects.Attack;
import bgu.spl.mics.application.passiveObjects.Diary;
import bgu.spl.mics.application.passiveObjects.Ewoks;

/**
 * HanSoloMicroservices is in charge of the handling {@link AttackEvents}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link AttackEvents}.
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
                if (!ewoks.getEwoks(attack.GetSerials()))   //TODO: need to write how to use ewoks and then go inside
                    complete(c, false);
                else {
                    try {
                        Thread.sleep(attack.GetDuration());
                        diary.setHanSoloFinish(System.currentTimeMillis());
                        diary.addTotalAttacks(1);
                        complete(c, true);
                    } catch (InterruptedException e) {
                        complete(c, false);
                        ewoks.finsh(attack.GetSerials()); //TODO :: need to write finish
                    }
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
