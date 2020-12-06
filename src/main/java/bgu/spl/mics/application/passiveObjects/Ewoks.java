package bgu.spl.mics.application.passiveObjects;

import java.util.List;
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
        for (int i = 1; i <= ewoks; i++) {
            this.ewoks[i] = new Ewok(i);
        }
    }

    public void acquireEwoks(List<Integer> serialNumbers) {

    }
}
