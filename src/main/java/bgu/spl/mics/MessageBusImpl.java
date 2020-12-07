package bgu.spl.mics;

import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * The {@link MessageBusImpl class is the implementation of the MessageBus interface.
 * Write your implementation here!
 * Only private fields and methods can be added to this class.
 */

public class MessageBusImpl implements MessageBus {
	private static MessageBusImpl instance = null; // Implemented as Singleton

	private Map<Class<? extends Event>, ConcurrentLinkedQueue<MicroService>> eventHash;
	private Map<Class<? extends Broadcast>, ConcurrentLinkedQueue<MicroService>> broadcastHash;
	private Map<MicroService, ConcurrentLinkedQueue<Message>> microServiceHash;
	private Map<Event, Future> futureMap;

	private MessageBusImpl() { // private constructor for singleton
		eventHash = new ConcurrentHashMap<Class<? extends Event>, ConcurrentLinkedQueue<MicroService>>() {};
		broadcastHash = new ConcurrentHashMap<Class<? extends Broadcast>, ConcurrentLinkedQueue<MicroService>>() {};
		microServiceHash = new ConcurrentHashMap<MicroService, ConcurrentLinkedQueue<Message>>() {};
		futureMap = new ConcurrentHashMap<Event, Future>(); // TODO: Maybe Event<?>, Future<?> ?
	}

	private boolean _is_hashMap_valid(Map hashMap, Object obj) {
		return (
				hashMap == null
						|| hashMap.isEmpty()
						|| !hashMap.containsKey(obj.getClass())
		);
	}

	private boolean _is_hashMap_valid(Map hashMap, Object obj, ConcurrentLinkedQueue queue) {
		return (
				hashMap == null
				|| hashMap.isEmpty()
				|| !hashMap.containsKey(obj.getClass())
				|| queue.isEmpty()
		);
	}

	// static synchronized method to get or create instance:
	public static synchronized MessageBusImpl getInstance() {
		if (Objects.isNull(instance))
			instance = new MessageBusImpl();

		return instance;
	}

	@Override
	public <T> void subscribeEvent(Class<? extends Event<T>> type, MicroService m) {
		if (eventHash == null || eventHash.get(type) == null) {
			ConcurrentLinkedQueue<MicroService> eventSubscribList = new ConcurrentLinkedQueue<MicroService>();
			eventHash.put(type, eventSubscribList);
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

	@Override
	public <T> void complete(Event<T> e, T result) {
		if (!_is_hashMap_valid(futureMap, e))
			throw new IllegalArgumentException("No valid argument provided.");

		this.futureMap.get(e).resolve(result);
	}

	@Override
	public synchronized void sendBroadcast(Broadcast b) {
		if (!_is_hashMap_valid(broadcastHash, b, broadcastHash.get(b.getClass())))
			// DO NOTHING (msg goes to trash)
			return;

		for (MicroService microService : broadcastHash.get(b.getClass())) {
			microServiceHash.get(microService).add(b);
		}
		// Notify all waiting threads:
		notifyAll();
	}

	@Override
	public synchronized <T> Future<T> sendEvent(Event<T> e) {
		if (!_is_hashMap_valid(eventHash, e, eventHash.get(e.getClass())))
			// throw new IllegalArgumentException("No valid argument provided.");
			// DO NOTHING (msg goes to trash)
			return null;

		microServiceHash.get(eventHash.get(e.getClass()).peek()).add(e);
		eventHash.get(e.getClass()).add(eventHash.get(e.getClass()).poll());
		// Handle future object:
		Future<T> future = new Future<>();
		this.futureMap.put(e, future);
		// Notify all waiting threads:
		notifyAll();
		// Return the future object to the sending MicroService:
        return future;
	}

	@Override
	public void register(MicroService m) {
		ConcurrentLinkedQueue<Message> iRegister = new ConcurrentLinkedQueue();
		microServiceHash.put(m, iRegister);
	}

	@Override
	public void unregister(MicroService m) {
		microServiceHash.remove(m);  //TODO : need to remove from broadcast and event and what to do with missions i didnt finish
		
	}

	@Override
	public Message awaitMessage(MicroService m) throws InterruptedException {
		if (this.microServiceHash == null || !this.microServiceHash.containsKey(m))
			throw new IllegalStateException("The provided MicroService is not registered.");

		while (this.microServiceHash.get(m).isEmpty()) {
			try {
				this.wait();
			}
			catch (InterruptedException e) {}
		}

		try {
			return this.microServiceHash.get(m).remove();
		}
		catch (NoSuchElementException e) {
			return awaitMessage(m);
		}
	}
}
