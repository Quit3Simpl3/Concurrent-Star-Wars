package bgu.spl.mics.application.passiveObjects;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Passive data-object representing a Diary - in which the flow of the battle is recorded.
 * We are going to compare your recordings with the expected recordings, and make sure that your output makes sense.
 * <p>
 * Do not add to this class nothing but a single constructor, getters and setters.
 */
public class Diary {
    // Private fields:
    private AtomicInteger totalAttacks; // Sum of HanSolo and C3PO attacks.
    // Task finishing timestamps:
    private long HanSoloFinish;
    private long C3POFinish;
    private long R2D2Deactivate;
    // Thread termination timestamps:
    private long LeiaTerminate;
    private long HanSoloTerminate;
    private long C3POTerminate;
    private long R2D2Terminate;
    private long LandoTerminate;

    private final static class SingletonHolder {
        private static final Diary instance = new Diary();
    }

    public static Diary getInstance() {
        return SingletonHolder.instance;
    }

    private Diary() { // Constructor
        this.totalAttacks = new AtomicInteger(0);
        this.HanSoloFinish = 0;
        this.C3POFinish = 0;
        this.R2D2Deactivate = 0;
        this.LeiaTerminate = 0;
        this.HanSoloTerminate = 0;
        this.C3POTerminate = 0;
        this.R2D2Terminate = 0;
        this.LandoTerminate = 0;
    }
  
    public void updateHanSolo(int attacks, long finish, long terminate) {
        if (finish > 0)
            setHanSoloFinish(finish);
        if (terminate > 0)
            setHanSoloTerminate(terminate);
        if (attacks > 0)
            addTotalAttacks(attacks);
    }

    public void updateC3PO(int attacks, long finish, long terminate) {
        if (finish > 0)
            setC3POFinish(finish);
        if (terminate > 0)
            setC3POTerminate(terminate);
        if (attacks > 0)
            addTotalAttacks(attacks);
    }

    /*
    * Setters:
     */
    private void addTotalAttacks(int totalAttacks) {
        this.totalAttacks.getAndAdd(totalAttacks);
    }

    // HanSolo and C3PO setters:
    private void setHanSoloFinish(long HanSoloFinish) {
        this.HanSoloFinish = HanSoloFinish;
    }

    private void setC3POFinish(long C3POFinish) {
        this.C3POFinish = C3POFinish;
    }

    private void setHanSoloTerminate(long HanSoloTerminate) {
        this.HanSoloTerminate = HanSoloTerminate;
    }

    private void setC3POTerminate(long C3POTerminate) {
        this.C3POTerminate = C3POTerminate;
    }
    ////

    public void setR2D2Deactivate(long R2D2Deactivate) {
        this.R2D2Deactivate = R2D2Deactivate;
    }

    public void setLeiaTerminate(long LeiaTerminate) {
        this.LeiaTerminate = LeiaTerminate;
    }

    public void setR2D2Terminate(long R2D2Terminate) {
        this.R2D2Terminate = R2D2Terminate;
    }

    public void setLandoTerminate(long LandoTerminate) {
        this.LandoTerminate = LandoTerminate;
    }

    /*
    * Getters:
     */
    public int getTotalAttacks() {
        return this.totalAttacks.get();
    }

    public long getHanSoloFinish() {
        return this.HanSoloFinish;
    }

    public long getC3POFinish() {
        return this.C3POFinish;
    }

    public long getR2D2Deactivate() {
        return this.R2D2Deactivate;
    }

    public long getLeiaTerminate() {
        return this.LeiaTerminate;
    }

    public long getHanSoloTerminate() {
        return this.HanSoloTerminate;
    }

    public long getC3POTerminate() {
        return this.C3POTerminate;
    }

    public long getR2D2Terminate() {
        return this.R2D2Terminate;
    }

    public long getLandoTerminate() {
        return this.LandoTerminate;
    }
}
