package bgu.spl.mics;

import java.util.HashMap;
import java.util.Objects;

/**
 * The MicroService is an abstract class that any micro-service in the system
 * must extend. The abstract MicroService class is responsible to get and
 * manipulate the singleton {@link MessageBus} instance.
 * <p>
 * Derived classes of MicroService should never directly touch the message-bus.
 * Instead, they have a set of internal protected wrapping methods (e.g.,
 * {@link #sendBroadcast(bgu.spl.mics.Broadcast)}, {@link #sendBroadcast(bgu.spl.mics.Broadcast)},
 * etc.) they can use. When subscribing to message-types,
 * the derived class also supplies a {@link Callback} that should be called when
 * a message of the subscribed type was taken from the micro-service
 * message-queue (see {@link MessageBus#register(bgu.spl.mics.MicroService)}
 * method). The abstract MicroService stores this callback together with the
 * type of the message is related to.
 * 
 * Only private fields and methods may be added to this class.
 * <p>
 */
public abstract class MicroService implements Runnable {
    // Private fields:
    String name;
    MessageBusImpl messageBus;
    HashMap<Class<? extends Message>, Callback<? extends Message>> subscriptions;
    boolean terminate;

    /**
     * @param name the micro-service name (used mainly for debugging purposes -
     *             does not have to be unique)
     */
    @SuppressWarnings("Convert2Diamond")
    public MicroService(String name) {
    	this.name = name;
    	this.messageBus = MessageBusImpl.getInstance();
    	this.terminate = false;
        subscriptions = new HashMap<Class<? extends Message>, Callback<? extends Message>>();
    }

    /**
    * Adds a new Event or Broadcast subscription to the HashMap with the callback {@code callback}.
    * @param <M>        The type of the Message - Event or Broadcast.
    * @param type       The {@link Class} representing the type of Message.
    * @param callback   The provided callback to associate with this Message type.
     */
    private <M extends Message> void addSubscription(Class<M> type, Callback<M> callback) {
        this.subscriptions.put(type, callback); // Adds or replaces
    }

    /**
     *
     */
    private boolean executeCallback(Message msg) {
        // Get the callback related to msg type:
        Callback callback = this.subscriptions.get(msg.getClass());
        // Execute the callback:
        callback.call(msg);
        return true;
    }

    /**
     * Subscribes to events of type {@code type} with the callback
     * {@code callback}. This means two things:
     * 1. Subscribe to events in the singleton event-bus using the supplied
     * {@code type}
     * 2. Store the {@code callback} so that when events of type {@code type}
     * are received it will be called.
     * <p>
     * For a received message {@code m} of type {@code type = m.getClass()}
     * calling the callback {@code callback} means running the method
     * {@link Callback#call(java.lang.Object)} by calling
     * {@code callback.call(m)}.
     * <p>
     * @param <E>      The type of event to subscribe to.
     * @param <T>      The type of result expected for the subscribed event.
     * @param type     The {@link Class} representing the type of event to
     *                 subscribe to.
     * @param callback The callback that should be called when messages of type
     *                 {@code type} are taken from this micro-service message
     *                 queue.
     */
    protected final <T, E extends Event<T>> void subscribeEvent(Class<E> type, Callback<E> callback) {
    	// Subscribe to event of type 'type' in msg bus:
        this.messageBus.subscribeEvent(type, this);
        // Store call back in subscriptions Hash Map:
        this.addSubscription(type, callback);
    }

    /**
     * Subscribes to broadcast message of type {@code type} with the callback
     * {@code callback}. This means two things:
     * 1. Subscribe to broadcast messages in the singleton event-bus using the
     * supplied {@code type}
     * 2. Store the {@code callback} so that when broadcast messages of type
     * {@code type} received it will be called.
     * <p>
     * For a received message {@code m} of type {@code type = m.getClass()}
     * calling the callback {@code callback} means running the method
     * {@link Callback#call(java.lang.Object)} by calling
     * {@code callback.call(m)}.
     * <p>
     * @param <B>      The type of broadcast message to subscribe to
     * @param type     The {@link Class} representing the type of broadcast
     *                 message to subscribe to.
     * @param callback The callback that should be called when messages of type
     *                 {@code type} are taken from this micro-service message
     *                 queue.
     */
    protected final <B extends Broadcast> void subscribeBroadcast(Class<B> type, Callback<B> callback) {
        // Subscribe to broadcast of type 'type' in msg bus:
        this.messageBus.subscribeBroadcast(type, this);
        // Store call back in subscriptions Hash Map:
        this.addSubscription(type, callback);
    }

    /**
     * Sends the event {@code e} using the message-bus and receive a {@link Future<T>}
     * object that may be resolved to hold a result. This method must be Non-Blocking since
     * there may be events which do not require any response and resolving.
     * <p>
     * @param <T>       The type of the expected result of the request
     *                  {@code e}
     * @param e         The event to send
     * @return  		{@link Future<T>} object that may be resolved later by a different
     *         			micro-service processing this event.
     * 	       			null in case no micro-service has subscribed to {@code e.getClass()}.
     */
    protected final <T> Future<T> sendEvent(Event<T> e) {
        // Send the event throw the msg bus and return the provided Future object:
        return this.messageBus.sendEvent(e);
    }

    /**
     * A Micro-Service calls this method in order to send the broadcast message {@code b} using the message-bus
     * to all the services subscribed to it.
     * <p>
     * @param b The broadcast message to send
     */
    protected final void sendBroadcast(Broadcast b) {
        this.messageBus.sendBroadcast(b);
    }

    /**
     * Completes the received request {@code e} with the result {@code result}
     * using the message-bus.
     * <p>
     * @param <T>    The type of the expected result of the processed event
     *               {@code e}.
     * @param e      The event to complete.
     * @param result The result to resolve the relevant Future object.
     *               {@code e}.
     */
    protected final <T> void complete(Event<T> e, T result) {
    	this.messageBus.complete(e, result);
    }

    /**
     * this method is called once when the event loop starts.
     */
    protected abstract void initialize();

    /**
     * Signals the event loop that it must terminate after handling the current
     * message.
     */
    protected final void terminate() {
        this.terminate = true;
    }

    /**
     * @return the name of the service - the service name is given to it in the
     *         construction time and is used mainly for debugging purposes.
     */
    public final String getName() {
        return this.name;
    }

    /**
     * The entry point of the micro-service.
     * otherwise you will end up in an infinite loop.
     */
    @Override
    public final void run() {
    	// register with msg bus
        this.messageBus.register(this);
        // run the derived class's initialize() method (the derived handles subscribing to msg types)
        this.initialize();
        // message loop:
        boolean callback_called = false;
        while (!this.terminate) {
            Message msg = null;
            try {
                callback_called = false;
                try {
                    msg = this.messageBus.awaitMessage(this);
                }
                catch (IllegalStateException e) { // Handle MicroService isn't registered
                    this.messageBus.register(this);
                    try {
                        msg = this.messageBus.awaitMessage(this);
                    }
                    catch (IllegalStateException ex) { // Really?! You screwed up AGAIN?!
                        System.out.println(ex.getMessage());
                    }
                }
                if (!Objects.isNull(msg)) // Make sure msg is not null
                    callback_called = this.executeCallback(msg);
            }
            catch (InterruptedException e) {
                if (this.terminate) {
                    // When received a message but callback wasn't called:
                    if (!callback_called && !Objects.isNull(msg))
                        this.executeCallback(msg); // Finish handling last received message
                    break; // Exit the run-loop
                }
            }
        }
        // Unregister this MicroService from the msg bus:
        this.messageBus.unregister(this);
    }
}
