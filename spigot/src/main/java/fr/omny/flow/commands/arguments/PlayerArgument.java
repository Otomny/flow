package fr.omny.flow.commands.arguments;

import java.util.List;
import java.util.Optional;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import fr.omny.flow.commands.CmdArgument;
import fr.omny.flow.commands.wrapper.Arguments;

public class PlayerArgument extends CmdArgument<Player>{

	public PlayerArgument(boolean optional) {
		super("Player", optional);
	}

	@Override
	public Optional<Player> getValue(String textValue, CommandSender sender, Arguments precedentArguments) {
		var player = Bukkit.getPlayer(textValue);
		return Optional.ofNullable(player);
	}

	@Override
	public List<String> getValues(CommandSender sender, Arguments precedentArguments) {
		return Bukkit.getOnlinePlayers()
			.stream()
			.map(Player::getName)
			.toList();
	}
	
}
