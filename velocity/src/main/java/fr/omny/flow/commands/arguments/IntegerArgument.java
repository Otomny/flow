package fr.omny.flow.commands.arguments;

import java.util.List;
import java.util.Optional;

import com.velocitypowered.api.command.CommandSource;

import fr.omny.flow.commands.Arguments;
import fr.omny.flow.commands.CmdArgument;

public class IntegerArgument extends CmdArgument<Integer> {

	public IntegerArgument(String name, boolean optional) {
		super(name, optional);
	}

	@Override
	public Optional<Integer> getValue(String textValue, CommandSource sender, Arguments precedentArguments) {
		try {
			return Optional.of(Integer.parseInt(textValue));
		} catch (NumberFormatException e) {
			return Optional.empty();
		}
	}

	@Override
	public List<String> getValues(CommandSource sender, Arguments precedentArguments) {
		return List.of("1", "0", "-1");
	}

}
