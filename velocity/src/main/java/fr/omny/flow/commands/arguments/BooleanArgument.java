package fr.omny.flow.commands.arguments;

import java.util.List;
import java.util.Optional;

import com.velocitypowered.api.command.CommandSource;

import fr.omny.flow.commands.Arguments;
import fr.omny.flow.commands.CmdArgument;

public class BooleanArgument extends CmdArgument<Boolean> {

	public BooleanArgument(String name, boolean optional) {
		super(name, optional);
	}

	@Override
	public Optional<Boolean> getValue(String textValue, CommandSource sender, Arguments precedentArguments) {
		try {
			return Optional.of(Boolean.parseBoolean(textValue));
		} catch (NumberFormatException e) {
			return Optional.empty();
		}
	}

	@Override
	public List<String> getValues(CommandSource sender, Arguments precedentArguments) {
		return List.of("false", "true");
	}

}
