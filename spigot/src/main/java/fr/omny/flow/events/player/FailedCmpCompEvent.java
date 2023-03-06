package fr.omny.flow.events.player;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import fr.omny.flow.commands.CommandComponent;
import lombok.Getter;

@Getter
public class FailedCmpCompEvent extends CmdCompEvent {

	private static final HandlerList HANDLER_LIST = new HandlerList();

	/**
	 * @return the handlerList
	 */
	public static HandlerList getHandlerList() {
		return HANDLER_LIST;
	}

	private FailedCmpCompEvent.Reason reason;

	public FailedCmpCompEvent(@NotNull Player who, @NotNull CommandComponent cmd, FailedCmpCompEvent.Reason reason) {
		super(who, cmd);
		this.reason = reason;
	}

	@Override
	public @NotNull HandlerList getHandlers() {
		return HANDLER_LIST;
	}

	public enum Reason{

		NO_PERMISSION,
		NOT_ENOUGHT_ARGUMENTS;

	}

}
