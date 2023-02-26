package fr.omny.flow.events.data;


import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import fr.omny.flow.api.data.ObjectUpdate;
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

	private String pattern;
	private String channel;
	private ObjectUpdate update;

	/**
	 * @param repository
	 * @param update
	 */
	public DataUpdateEvent(String pattern, String channel, ObjectUpdate update) {
		super(true);
		this.pattern = pattern;
		this.channel = channel;
		this.update = update;
	}

	@Override
	public HandlerList getHandlers() {
		return HANDLER_LIST;
	}

}