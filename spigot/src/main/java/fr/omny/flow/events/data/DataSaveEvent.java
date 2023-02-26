package fr.omny.flow.events.data;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import lombok.Getter;

@Getter
public class DataSaveEvent extends Event{
	
	public static final HandlerList HANDLER_LIST = new HandlerList();

	/**
	 * @return the handlerList
	 */
	public static HandlerList getHandlerList() {
		return HANDLER_LIST;
	}

	@Override
	public HandlerList getHandlers() {
		return HANDLER_LIST;
	}

}
