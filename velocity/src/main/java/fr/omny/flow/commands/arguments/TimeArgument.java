package fr.omny.flow.commands.arguments;

import java.util.List;
import java.util.Optional;

import com.velocitypowered.api.command.CommandSource;

import fr.omny.flow.api.utils.date.TimeParser;
import fr.omny.flow.commands.Arguments;
import fr.omny.flow.commands.CmdArgument;

public class TimeArgument extends CmdArgument<Long> {

	public TimeArgument(boolean optional) {
		super("TimeInMillis", optional);
	}

	@Override
	public Optional<Long> getValue(String textValue, CommandSource sender, Arguments precedentArguments) {
		var dateValue = TimeParser.toMillis(textValue);
		return dateValue == -1 ? Optional.empty() : Optional.of(dateValue);
	}

	@Override
	public List<String> getValues(CommandSource sender, Arguments precedentArguments) {
		return List.of("1m", "2d", "3s", "4y", "5w");
	}

}
