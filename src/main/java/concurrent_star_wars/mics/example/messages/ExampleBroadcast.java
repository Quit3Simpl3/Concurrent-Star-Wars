package concurrent_star_wars.mics.example.messages;

import concurrent_star_wars.mics.Broadcast;

public class ExampleBroadcast implements Broadcast {

    private String senderId;

    public ExampleBroadcast(String senderId) {
        this.senderId = senderId;
    }

    public String getSenderId() {
        return senderId;
    }

}
