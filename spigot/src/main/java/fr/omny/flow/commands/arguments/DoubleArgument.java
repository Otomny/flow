package fr.omny.flow.commands.arguments;


import java.util.List;
import java.util.Optional;

import org.bukkit.command.CommandSender;

import fr.omny.flow.commands.CmdArgument;
import fr.omny.flow.commands.wrapper.Arguments;

public class DoubleArgument extends CmdArgument<Double> {

	public DoubleArgument(String name, boolean optional) {
		super(name, optional);
	}

	@Override
	public Optional<Double> getValue(String textValue, CommandSender sender, Arguments precedentArguments) {
		try {
			return Optional.of(Double.parseDouble(textValue));
		} catch (NumberFormatException e) {
			return Optional.empty();
		}
	}

	@Override
	public List<String> getValues(CommandSender sender, Arguments precedentArguments) {
		return List.of("1.0", "0.0", "-1.0");
	}

}
