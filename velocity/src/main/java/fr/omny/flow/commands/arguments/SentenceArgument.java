package fr.omny.flow.commands.arguments;

import java.util.List;
import java.util.Optional;

import com.velocitypowered.api.command.CommandSource;

import fr.omny.flow.commands.Arguments;
import fr.omny.flow.commands.CmdArgument;

public class SentenceArgument extends CmdArgument<String> {

	public SentenceArgument(String name, boolean optional) {
		super(name, optional);
	}

	@Override
	public Optional<String> getValue(String textValue, CommandSource sender, Arguments precedentArguments) {
		return Optional.of(textValue);
	}

	@Override
	public List<String> getValues(CommandSource sender, Arguments precedentArguments) {
		return List.of();
	}

}
