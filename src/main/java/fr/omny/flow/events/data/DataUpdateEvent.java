package fr.omny.flow.events.data;


import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import fr.omny.flow.data.CrudRepository;
import fr.omny.flow.data.ObjectUpdate;
import lombok.Getter;

@Getter
public class DataUpdateEvent extends Event {

	public static final HandlerList HANDLER_LIST = new HandlerList();

	/**
	 * @return the handlerList
	 */
	public static HandlerList getHandlerList() {
		return HANDLER_LIST;
	}

	private CrudRepository<?, ?> repository;
	private Class<?> dataClass;
	private ObjectUpdate update;

	/**
	 * @param isAsync
	 * @param repository
	 * @param update
	 */
	public DataUpdateEvent(CrudRepository<?, ?> repository, Class<?> dataClass, ObjectUpdate update) {
		super(true);
		this.repository = repository;
		this.dataClass = dataClass;
		this.update = update;
	}
	
	@Override
	public HandlerList getHandlers() {
		return HANDLER_LIST;
	}

}
