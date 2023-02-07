package fr.omny.flow.commands.arguments;

import java.util.List;
import java.util.Optional;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;

import fr.omny.flow.commands.CmdArgument;
import fr.omny.flow.commands.wrapper.Arguments;

public class WorldArgument extends CmdArgument<World>{

	public WorldArgument(boolean optional) {
		super("World", optional);
	}

	@Override
	public Optional<World> getValue(String textValue, CommandSender sender, Arguments precedentArguments) {
		var world = Bukkit.getWorld(textValue);
		return Optional.ofNullable(world);
	}

	@Override
	public List<String> getValues(CommandSender sender, Arguments precedentArguments) {
		return Bukkit.getWorlds()
			.stream()
			.map(World::getName)
			.toList();
	}
	
}
