package fr.omny.flow.commands.arguments;

import java.util.List;
import java.util.Optional;

import org.bukkit.command.CommandSender;

import fr.omny.flow.commands.CmdArgument;
import fr.omny.flow.commands.wrapper.Arguments;

public class SentenceArgument extends CmdArgument<String>{

	public SentenceArgument(String name, boolean optional) {
		super(name, optional);
	}

	@Override
	public Optional<String> getValue(String textValue, CommandSender sender, Arguments precedentArguments) {
		return Optional.of(textValue);
	}

	@Override
	public List<String> getValues(CommandSender sender, Arguments precedentArguments) {
		return List.of();
	}
	
}
