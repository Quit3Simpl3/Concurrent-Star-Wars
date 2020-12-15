package bgu.spl.mics;

import java.util.Map;
import java.util.NoSuchElementException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * The {@link MessageBusImpl class is the implementation of the MessageBus interface.
 * Write your implementation here!
 * Only private fields and methods can be added to this class.
 */

public class MessageBusImpl implements MessageBus {
	private Map<Class<? extends Event>, ConcurrentLinkedQueue<MicroService>> eventHash;
	private Map<Class<? extends Broadcast>, ConcurrentLinkedQueue<MicroService>> broadcastHash;
	private Map<MicroService, ConcurrentLinkedQueue<Message>> microServiceHash;
	private Map<Event, Future> futureMap;

	private final static class SingletonHolder {
		private final static MessageBusImpl instance = new MessageBusImpl();
	}

	private MessageBusImpl() { // private constructor for singleton
		eventHash = new ConcurrentHashMap<Class<? extends Event>, ConcurrentLinkedQueue<MicroService>>() {};
		broadcastHash = new ConcurrentHashMap<Class<? extends Broadcast>, ConcurrentLinkedQueue<MicroService>>() {};
		microServiceHash = new ConcurrentHashMap<MicroService, ConcurrentLinkedQueue<Message>>() {};
		futureMap = new ConcurrentHashMap<Event, Future>();
	}

	private boolean _is_hashMap_valid(Map hashMap, Object obj) {
		return (
				hashMap == null
						|| hashMap.isEmpty()
						|| !hashMap.containsKey(obj.getClass())
		);
	}

	private boolean _is_hashMap_invalid(Map hashMap, Object obj, ConcurrentLinkedQueue queue) {

		return (
				hashMap == null
				|| hashMap.isEmpty()
				|| !hashMap.containsKey(obj.getClass())
				|| queue.isEmpty()
		);
	}

	// method to get or create instance:
	public static MessageBusImpl getInstance() {
		// SingletonHolder:
		return SingletonHolder.instance;
	}

	@Override
	public synchronized <T> void subscribeEvent(Class<? extends Event<T>> type, MicroService m) {
		if (eventHash == null || eventHash.get(type) == null) {
			ConcurrentLinkedQueue<MicroService> eventQueue = new ConcurrentLinkedQueue<MicroService>();
			eventHash.put(type, eventQueue);
		}

		if(!eventHash.get(type).contains(m))
			eventHash.get(type).add(m); // Add microservice to the queue of event 'type'
	}

	@Override
	public synchronized void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m) {
		if (broadcastHash == null || broadcastHash.get(type) == null) {
			ConcurrentLinkedQueue<MicroService> broadcastQueue = new ConcurrentLinkedQueue<MicroService>();
			broadcastHash.put(type, broadcastQueue);
		}

		if(!broadcastHash.get(type).contains(m))
			broadcastHash.get(type).add(m); // Add microservice to the queue of broadcast 'type'
    }

	@Override
	public <T> void complete(Event<T> e, T result) {
		if (!_is_hashMap_valid(futureMap, e))
			throw new IllegalArgumentException("No valid argument provided.");

		this.futureMap.get(e).resolve(result);
	}

	@Override
	public synchronized void sendBroadcast(Broadcast b) {
		if (_is_hashMap_invalid(broadcastHash, b, broadcastHash.get(b.getClass()))) {
			// DO NOTHING (msg goes to trash)
			return;
		}

		for (MicroService microService : broadcastHash.get(b.getClass())) {
			microServiceHash.get(microService).add(b);
		}
		// Notify all waiting threads:
		notifyAll();
	}

	@Override
	public synchronized <T> Future<T> sendEvent(Event<T> e) {
		if (_is_hashMap_invalid(eventHash, e, eventHash.get(e.getClass()))) {
			// DO NOTHING (msg goes to trash)
			return null;
		}

		// Get next MicroService:
		MicroService microService = eventHash.get(e.getClass()).poll();

		// Add the event to the MicroService's queue:
		microServiceHash.get(microService).add(e);
		// Round-robin message assignment:
		eventHash.get(e.getClass()).add(microService);

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
		if (!microServiceHash.containsKey(m)) {
			ConcurrentLinkedQueue<Message> iRegister = new ConcurrentLinkedQueue();
			microServiceHash.put(m, iRegister);
		}
	}

	private void unsubscribe (Map map, MicroService m) {
		for (Object b : map.keySet()) {
			if (map.get(b) instanceof ConcurrentLinkedQueue) {
				((ConcurrentLinkedQueue)map.get(b)).remove(m);
			}
		}
	}

	@Override
	public void unregister (MicroService m) {
		microServiceHash.remove(m);
		unsubscribe(eventHash,m);
		unsubscribe(broadcastHash,m);
	}

	@Override
	public synchronized Message awaitMessage (MicroService m) throws InterruptedException, IllegalStateException {
		if (this.microServiceHash == null || !this.microServiceHash.containsKey(m)) {
			throw new IllegalStateException("The provided MicroService is not registered.");
		}
		// While the Microservice's queue is empty, wait():
		while (this.microServiceHash.get(m).isEmpty()) {
			try {
				wait();
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



