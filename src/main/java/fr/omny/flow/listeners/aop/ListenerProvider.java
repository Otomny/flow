package fr.omny.flow.listeners.aop;

/**
 * Implement that class if you want to register listeners on the fly. It scan the implementing class to find each PUBLIC
 * method that returns {@link org.bukkit.event.Listener} and call them
 */
public interface ListenerProvider {

}
