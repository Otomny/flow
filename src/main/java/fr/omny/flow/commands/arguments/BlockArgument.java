package fr.omny.flow.commands.arguments;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.command.CommandSender;

import fr.omny.flow.commands.wrapper.Arguments;

public class BlockArgument extends EnumArgument<Material> {

	public BlockArgument(boolean optional) {
		super(Material.class, optional);
	}

	@Override
	public List<String> getValues(CommandSender sender, Arguments precedentArguments) {
		return List.of(Material.values())
				.stream()
				.filter(Material::isBlock)
				.map(Material::toString).toList();
	}

}
