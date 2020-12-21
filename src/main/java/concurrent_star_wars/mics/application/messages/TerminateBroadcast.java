package concurrent_star_wars.mics.application.messages;

import concurrent_star_wars.mics.Broadcast;

public class TerminateBroadcast implements Broadcast {
    private final String senderId;

    public TerminateBroadcast(String senderId) {
        this.senderId = senderId;
    }

    public String getSenderId() {
        return senderId;
    }
}
