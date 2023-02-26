package fr.omny.flow.events.data;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import fr.omny.flow.api.data.CrudRepository;
import fr.omny.flow.api.data.ObjectUpdate;
import lombok.Getter;
import lombok.Setter;

@Getter
public class DataEmitEvent extends Event implements Cancellable{

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
	@Setter
	private boolean cancelled;

	/**
	 * @param repository
	 * @param update
	 */
	public DataEmitEvent(CrudRepository<?, ?> repository, Class<?> dataClass, ObjectUpdate update) {
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

