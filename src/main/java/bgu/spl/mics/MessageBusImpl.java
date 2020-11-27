package bgu.spl.mics;

/**
 * The {@link MessageBusImpl class is the implementation of the MessageBus interface.
 * Write your implementation here!
 * Only private fields and methods can be added to this class.
 */
public class MessageBusImpl implements MessageBus {
	// TODO: fields
	// main-queue: stores the messages sent to the MB for processing.
	// dictionary of (key=Event type (Event.class), value=Event) that holds the events the events sent to the MB
	// dictionary of (key=MicroService, value=queue of messages) (NOT in MicroService class! See page 8).
	// add another dictionary for broadcast messages? or: when awaitMessage(), check if the type of the next message is broadcast -
	// 		if it is a broadcast message, don't pop it from the msg queue, but add it to a popping-list that will pop all the
	//		broadcast messages when the round-robin round ends.
	
	@Override
	public <T> void subscribeEvent(Class<? extends Event<T>> type, MicroService m) {
		// TODO: add m to (Event type, MicroService)[type] queue
	}

	@Override
	public void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m) {
		
    }

	@Override @SuppressWarnings("unchecked")
	public <T> void complete(Event<T> e, T result) {
		
	}

	@Override
	public void sendBroadcast(Broadcast b) {
		
	}

	
	@Override
	public <T> Future<T> sendEvent(Event<T> e) {
		
        return null;
	}

	@Override
	public void register(MicroService m) {
		
	}

	@Override
	public void unregister(MicroService m) {
		
	}

	@Override
	public Message awaitMessage(MicroService m) throws InterruptedException {
		
		return null;
	}
}
