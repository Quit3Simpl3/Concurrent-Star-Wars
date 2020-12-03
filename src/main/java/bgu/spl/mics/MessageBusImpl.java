package bgu.spl.mics;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

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
	private static MessageBusImpl instance = null; // Implemented as Singleton


	private Map<Class<? extends Event>, ConcurrentLinkedQueue> eventHash;
	private Map<Class<? extends Broadcast>, ConcurrentLinkedQueue> broadcastHash;
	private Map<MicroService, ConcurrentLinkedQueue> microServiceHash;
	private Map<Event, Future> completeFutre;

	private MessageBusImpl() { // private constructor for singleton
		// TODO: initialize fields
		eventHash = new ConcurrentHashMap<Class<? extends Event>, ConcurrentLinkedQueue>() {};
		broadcastHash = new ConcurrentHashMap<Class<? extends Broadcast>, ConcurrentLinkedQueue>() {};
		microServiceHash = new ConcurrentHashMap<MicroService, ConcurrentLinkedQueue>() {};
		completeFutre = new ConcurrentHashMap<Event, Future>();
	}

	// static method to create instance
	public static MessageBusImpl getInstance() {
		if (instance == null)
			instance = new MessageBusImpl();;
		return instance;
	}

	@Override
	public <T> void subscribeEvent(Class<? extends Event<T>> type, MicroService m) {
		if (eventHash == null || eventHash.get(type) == null) {
			ConcurrentLinkedQueue<MicroService> eventSubscribList = new ConcurrentLinkedQueue<MicroService>();
			eventHash.put(type,eventSubscribList);
		}
		if(!eventHash.get(type).contains(m)) { //TODO : we need to know if i can add twice
			eventHash.get(type).add(m);
		}
	}

	@Override
	public void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m) {
		if (broadcastHash == null || broadcastHash.get(type) == null) {
			ConcurrentLinkedQueue<MicroService> broadcastSubscribList = new ConcurrentLinkedQueue<MicroService>();
			broadcastHash.put(type,broadcastSubscribList);
		}
		if(!broadcastHash.get(type).contains(m)) { //TODO : we need to know if i can add twice
			broadcastHash.get(type).add(m);
		}
    }

	@Override @SuppressWarnings("unchecked")
	public <T> void complete(Event<T> e, T result) {
		if (completeFutre.get(e) != null) { //not sure its can be..
			completeFutre.get(e).resolve(result);

			// not sure what to do with the result
		}

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
		ConcurrentLinkedQueue<Message> iRegister = new ConcurrentLinkedQueue();
		microServiceHash.put(m,iRegister);
	}

	@Override
	public void unregister(MicroService m) {
		microServiceHash.remove(m);  //TODO : need to remove from broadcast and event and what to do with missions i didnt finish
		
	}

	@Override
	public Message awaitMessage(MicroService m) throws InterruptedException {
		
		return null;
	}
}
