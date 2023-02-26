package fr.omny.flow.commands.arguments;


import java.util.List;
import java.util.Optional;

import org.bukkit.command.CommandSender;

import fr.omny.flow.api.utils.date.TimeParser;
import fr.omny.flow.commands.CmdArgument;
import fr.omny.flow.commands.wrapper.Arguments;

public class TimeArgument extends CmdArgument<Long> {

	public TimeArgument(boolean optional) {
		super("TimeInMillis", optional);
	}

	@Override
	public Optional<Long> getValue(String textValue, CommandSender sender, Arguments precedentArguments) {
		var dateValue = TimeParser.toMillis(textValue);
		return dateValue == -1 ? Optional.empty() : Optional.of(dateValue);
	}

	@Override
	public List<String> getValues(CommandSender sender, Arguments precedentArguments) {
		return List.of("1m", "2d", "3s", "4y", "5w");
	}

}
