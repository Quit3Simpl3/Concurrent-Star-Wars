package bgu.spl.mics.application.messages;
import bgu.spl.mics.Event;
import java.util.List;

public class AttackEvent implements Event<Boolean> {
    private final List<Integer> ewoksForAttack;
    
    public AttackEvent (List<Integer>  serialNumbers) {
        ewoksForAttack =  serialNumbers;
    }
	public List<Integer> getEwokForAttack() {
        return ewoksForAttack;
    }
}
