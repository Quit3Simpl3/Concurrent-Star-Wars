package concurrent_star_wars.mics.application.passiveObjects;

/**
 * Passive data-object representing a forest creature summoned when HanSolo and C3PO receive AttackEvents.
 */
public class Ewok {
    int serialNumber;
	boolean available;

	public Ewok() {}

	public Ewok(int serialNumber) {
	    this.serialNumber = serialNumber;
	    this.available = true;
    }

    /**
     * Acquires an Ewok
     */
    public void acquire() {
		this.available = false;
    }

    /**
     * release an Ewok
     */
    public void release() {
    	this.available = true;
    }

    /*
    * Check if the Ewok is available for acquiring
     */
    public boolean isAvailable() {
        return available;
    }

    /*
    * Return the Ewok's serial number
     */
    public int getSerialNumber() {
        return this.serialNumber;
    }
}
