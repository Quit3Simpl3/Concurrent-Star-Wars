package concurrent_star_wars.mics.example.messages;

import concurrent_star_wars.mics.Event;

public class ExampleEvent implements Event<String>{

    private String senderName;

    public ExampleEvent(String senderName) {
        this.senderName = senderName;
    }

    public String getSenderName() {
        return senderName;
    }
}