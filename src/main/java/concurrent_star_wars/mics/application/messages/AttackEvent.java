package concurrent_star_wars.mics.application.messages;
import concurrent_star_wars.mics.application.passiveObjects.Attack;
import concurrent_star_wars.mics.Event;

public class AttackEvent implements Event<Boolean> {
    private final Attack attack;

    public AttackEvent (Attack attack) {
        this.attack = attack;
    }

    public Attack getAttack() {
        return attack;
    }
}
