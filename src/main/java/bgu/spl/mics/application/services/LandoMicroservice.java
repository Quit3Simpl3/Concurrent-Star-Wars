package bgu.spl.mics.application.services;

import bgu.spl.mics.Broadcast;
import bgu.spl.mics.Callback;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.BombDestroyerEvent;
import bgu.spl.mics.application.messages.TerminateBroadcast;
import bgu.spl.mics.application.passiveObjects.Diary;

/**
 * LandoMicroservice
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class LandoMicroservice  extends MicroService {
    private long duration;
    private Diary diary;
    Broadcast TerminateBroadcast;

    public LandoMicroservice(long duration) {

        super("Lando");
        this.duration = duration;
        diary = Diary.getInstance();
    }

    @Override
    protected void initialize() {

    Callback<TerminateBroadcast> terminated = new Callback<TerminateBroadcast>(){
        @Override
        public void call(TerminateBroadcast c) {
            diary.setLandoTerminate(System.currentTimeMillis());
            terminate();

        }
    };
        this.subscribeBroadcast(TerminateBroadcast.class,terminated);


    Callback<BombDestroyerEvent> bombDestroy = new Callback<BombDestroyerEvent>() {
        @Override
        public void call(BombDestroyerEvent c) {

            try{

                Thread.sleep(duration);
                complete(c,true);
                sendBroadcast(TerminateBroadcast);

            }catch (InterruptedException e) {
                 complete(c,false);

            }
        }
    };
    subscribeEvent(BombDestroyerEvent.class,bombDestroy);

}


}
