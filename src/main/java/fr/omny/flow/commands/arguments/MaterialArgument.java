package fr.omny.flow.commands.arguments;


import java.util.List;
import java.util.Optional;

import org.bukkit.Material;
import org.bukkit.command.CommandSender;

import fr.omny.flow.commands.CmdArgument;
import fr.omny.flow.commands.wrapper.Arguments;

public class MaterialArgument extends CmdArgument<Material> {

	public MaterialArgument(boolean optional) {
		super("Type", optional);
	}

	@Override
	public Optional<Material> getValue(String textValue, CommandSender sender, Arguments precedentArguments) {
		return Optional.ofNullable(Material.valueOf(textValue));
	}

	@Override
	public List<String> getValues(CommandSender sender, Arguments precedentArguments) {
		return List.of(Material.values()).stream().map(Material::toString).toList();
	}

}
