package bgu.spl.mics;

import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Set;
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

	private boolean _is_hashMap_invalid(Map hashMap, Object obj, ConcurrentLinkedQueue queue) {

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
			// TODO: DELETE BEFORE SUBMITTING!
			System.out.println("Added " + m + " to " + type + " queue.");
			// TODO: DELETE BEFORE SUBMITTING!
		}
		else { // TODO: DELETE BEFORE SUBMITTING!
			System.out.println(m + " already in " + type + " queue!");
		} // TODO: DELETE BEFORE SUBMITTING!
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
			// throw new IllegalArgumentException("No valid argument provided.");
			// DO NOTHING (msg goes to trash)
			return null;
		}

		// microServiceHash.get(eventHash.get(e.getClass()).peek()).add(e); // TODO: remove comment

		// TODO: DELETE BEFORE SUBMITTING!!!
		ConcurrentLinkedQueue event_queue = eventHash.get(e.getClass());
		System.out.println("event_queue: " + event_queue);
		MicroService m = (MicroService)event_queue.poll();
		System.out.println("m: " + m);
		System.out.println("microServiceHash: " + microServiceHash);
		ConcurrentLinkedQueue msq = microServiceHash.get(m);
		System.out.println("msq: " + msq);
		msq.add(e);

		System.out.println(m.getName() + " assigned event: " + e);
		eventHash.get(e.getClass()).add(m);
		// TODO: DELETE BEFORE SUBMITTING!!!

		// Round-robin message assignment:
		// eventHash.get(e.getClass()).add(eventHash.get(e.getClass()).poll()); // TODO: remove comment

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

				// TODO: DELETE BEFORE SUBMITTING!
				System.out.println("Unsubscribing " + m + " from " + b);
				// TODO: DELETE BEFORE SUBMITTING!

				((ConcurrentLinkedQueue)map.get(b)).remove(m);
			}
		}
	}

	@Override
	public void unregister (MicroService m){
		microServiceHash.remove(m);
		unsubscribe(eventHash,m);
		unsubscribe(broadcastHash,m);
				// TODO: remove m from eventHash and broadcastHasg

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
			Message msg = this.microServiceHash.get(m).remove();
			// TODO: DELETE BEFORE SUBMITTING!!!
			System.out.println(Thread.currentThread().getName() + " got msg: " + msg);
			// TODO: DELETE BEFORE SUBMITTING!!!
			return msg;
		}
		catch (NoSuchElementException e) {
			return awaitMessage(m);
		}
	}
}



