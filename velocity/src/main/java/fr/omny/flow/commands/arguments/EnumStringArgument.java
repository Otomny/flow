package fr.omny.flow.commands.arguments;

import java.util.List;
import java.util.Optional;

import com.velocitypowered.api.command.CommandSource;

import fr.omny.flow.commands.Arguments;
import fr.omny.flow.commands.CmdArgument;

public class EnumStringArgument extends CmdArgument<String> {

	private List<String> accepted;

	public EnumStringArgument(String name, boolean optional, String... strings) {
		this(name, optional, List.of(strings));
	}

	public EnumStringArgument(String name, boolean optional, List<String> strings) {
		super(name, optional);
		this.accepted = strings;
	}

	@Override
	public Optional<String> getValue(String textValue, CommandSource sender, Arguments precedentArguments) {
		return accepted.contains(textValue) ? Optional.of(textValue) : Optional.empty();
	}

	@Override
	public List<String> getValues(CommandSource sender, Arguments precedentArguments) {
		return this.accepted;
	}

}
