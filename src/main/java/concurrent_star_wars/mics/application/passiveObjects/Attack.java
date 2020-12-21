package concurrent_star_wars.mics.application.passiveObjects;

import java.util.List;

/**
 * Passive data-object representing an attack object.
 */
public class Attack {
    final List<Integer> serials;
    final int duration;

    public Attack(List<Integer> serialNumbers, int duration) {
        this.serials = serialNumbers;
        this.duration = duration;
    }

    public List<Integer> GetSerials () {
        return serials;
    }

    public int GetDuration() {
        return duration;
    }
}
