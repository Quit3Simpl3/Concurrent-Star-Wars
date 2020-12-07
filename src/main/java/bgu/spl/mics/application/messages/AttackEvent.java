package bgu.spl.mics.application.messages;
import bgu.spl.mics.Event;
import bgu.spl.mics.application.passiveObjects.Attack;

import java.util.List;

public class AttackEvent implements Event<Boolean> {

    private final Attack attack;
  //  private boolean resulte;
    
    public AttackEvent (Attack attack)
    {
  //      resulte = false;
        this.attack = attack;
    }

	//public List<Integer> getEwokForAttack() {
  //      return ewoksForAttack;
 //   }
    public Attack GetAttack(){ return attack;}
   // public void complete
}
