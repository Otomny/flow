package fr.omny.flow.commands.arguments;

import java.util.List;
import java.util.Optional;

import com.velocitypowered.api.command.CommandSource;

import fr.omny.flow.commands.Arguments;
import fr.omny.flow.commands.CmdArgument;

public class LongArgument extends CmdArgument<Long> {

	public LongArgument(String name, boolean optional) {
		super(name, optional);
	}

	@Override
	public Optional<Long> getValue(String textValue, CommandSource sender, Arguments precedentArguments) {
		try {
			return Optional.of(Long.parseLong(textValue));
		} catch (NumberFormatException e) {
			return Optional.empty();
		}
	}

	@Override
	public List<String> getValues(CommandSource sender, Arguments precedentArguments) {
		return List.of("1", "0", "-1");
	}

}
