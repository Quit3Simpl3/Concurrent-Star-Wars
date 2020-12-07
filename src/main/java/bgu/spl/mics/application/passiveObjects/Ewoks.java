package bgu.spl.mics.application.passiveObjects;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;

/**
 * Passive object representing the resource manager.
 * <p>
 * This class must be implemented as a thread-safe singleton.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You can add ONLY private methods and fields to this class.
 */
public class Ewoks {
    private static Ewoks instance = null;
    private Ewok[] ewoks;

    public static synchronized Ewoks getInstance() {
        if (Objects.isNull(instance))
            instance = new Ewoks();

        return instance;
    }

    public void createEwoks(int ewoks) {
        this.ewoks = new Ewok[ewoks];
        for (int i = 0; i < ewoks; i++) { // Insert each new Ewok into the array
            this.ewoks[i] = new Ewok(i+1); // Serial numbers run from 1 to n
        }
    }

    private Ewok getEwok(int serialNumber) throws NoSuchElementException {
        return this.ewoks[serialNumber-1];
    }

    /**
     * Acquires the necessary Ewoks' by their serial numbers.
     * If any Ewok is not available currently, then block the thread until they
     * are released.
     * @param serialNumbers - Ewoks' serial numbers.
     */
    public synchronized boolean acquireEwoks(List<Integer> serialNumbers) {
        // TODO: Another implementation idea: insert all unavailable into a queue,
        // then after finishing the for-loop (which acquires all available ewoks),
        // run over the queue and wait() until all the queue's ewoks are available.

        int acquired_ewoks = 0;
        for (Integer serial : serialNumbers) {
            Ewok ewok = this.getEwok(serial);
            // Block thread until Ewok is available, and all other threads waiting
            // to use acquireEwoks() will wait as well:
            while(!ewok.isAvailable()) {
                try {
                    this.wait();
                }
                catch (InterruptedException e) {}
            }

            if (ewok.isAvailable()) {
                ewok.acquire();
                acquired_ewoks++;
            }
        }

        // Return 'true' if all necessary Ewoks were acquired:
        return (acquired_ewoks == serialNumbers.size());
    }

    /**
     * Releases the Ewoks which were acquired for an attack.
     * @param serialNumbers - Ewoks' serial numbers.
     */
    public void releaseEwoks(List<Integer> serialNumbers) {
        for (Integer serial : serialNumbers) {
            this.getEwok(serial).release();
            notifyAll(); // Notify all the microservices waiting for ewoks to be released.
        }
    }
}
