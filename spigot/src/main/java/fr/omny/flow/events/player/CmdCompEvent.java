package fr.omny.flow.events.player;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;

import fr.omny.flow.commands.CommandComponent;
import lombok.Getter;

@Getter
public abstract class CmdCompEvent extends PlayerEvent {

	private @NotNull CommandComponent cmdComp;

	public CmdCompEvent(@NotNull Player who, @NotNull CommandComponent cmd) {
		super(who);
		this.cmdComp = cmd;
	}

}
