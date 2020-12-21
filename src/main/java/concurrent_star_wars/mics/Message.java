package concurrent_star_wars.mics;

/**
 * A message is a data-object which is passed between micro-services as a means of communication. The Message interface
 * is a "Marker" interface, it is used only to mark other types of objects as messages.
 * Every class that is sent as a message (using the {@link MessageBus}) implements it.
 */
public interface Message {}
