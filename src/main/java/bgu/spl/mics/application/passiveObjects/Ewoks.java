package bgu.spl.mics.application.passiveObjects;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Passive object representing the resource manager.
 * <p>
 * This class must be implemented as a thread-safe singleton.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You can add ONLY private methods and fields to this class.
 */
public class Ewoks {
    private Ewok[] ewoks;

    private final static class SingletonHolder {
        private final static Ewoks instance = new Ewoks();
    }
  
    public static Ewoks getInstance() {
        // SingletonHolder
        return SingletonHolder.instance;
    }
  
    public void createEwoks(int ewoks) {
        this.ewoks = new Ewok[ewoks];
        for (int i = 0; i < ewoks; i++) { // Insert each new Ewok into the array
            this.ewoks[i] = new Ewok(i+1); // Serial numbers run from 1 to n
        }
    }

    private Ewok getEwok(int serialNumber) throws NoSuchElementException {
        Ewok ewok = this.ewoks[serialNumber-1];
        if (!(ewok instanceof Ewok)) {
            throw new NoSuchElementException("Ewok " + serialNumber + " does not exist.");
        }
        else if (ewok.getSerialNumber() == serialNumber) {
            return ewok;
        }
        else {
            for (Ewok e : this.ewoks) {
                if (ewok.getSerialNumber() == serialNumber)
                    return ewok;
            }
        }
        throw new NoSuchElementException("Ewok " + serialNumber + " does not exist.");
    }

    /**
     * Acquires the necessary Ewoks' by their serial numbers.
     * If any Ewok is not available currently, then block the thread until they
     * are released.
     * @param serialNumbers - Ewoks' serial numbers.
     */
    public synchronized boolean acquireEwoks(List<Integer> serialNumbers) {
        int acquired_ewoks = 0;
        boolean[] acquired = new boolean[this.ewoks.length+1]; // For asserting uniqueness
        for (int i = 0; i < acquired.length; i++) acquired[i] = false; // We don't trust the default initialization

        for (Integer serial : serialNumbers) {
            Ewok ewok = this.getEwok(serial);
            if (!acquired[serial]) {
                // Block thread until Ewok is available, and all other threads waiting
                // to use acquireEwoks() will wait as well:
                while (!ewok.isAvailable()) {
                    try {
                        this.wait();
                    }
                    catch (InterruptedException e) {}
                }

                if (ewok.isAvailable()) {
                    ewok.acquire();
                    acquired[serial] = true;
                    acquired_ewoks++;
                }
            }
        }

        // Return 'true' if all necessary Ewoks were acquired:
        return (acquired_ewoks == serialNumbers.size());
    }

    /**
     * Releases the Ewoks which were acquired for an attack.
     * @param serialNumbers - Ewoks' serial numbers.
     */
    public synchronized void releaseEwoks(List<Integer> serialNumbers) {
        for (Integer serial : serialNumbers) {
            this.getEwok(serial).release();
            notifyAll(); // Notify all the microservices waiting for ewoks to be released.
        }
    }
}
