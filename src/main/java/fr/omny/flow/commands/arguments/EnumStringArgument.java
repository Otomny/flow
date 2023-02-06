package fr.omny.flow.commands.arguments;


import java.util.List;
import java.util.Optional;

import org.bukkit.command.CommandSender;

import fr.omny.flow.commands.CmdArgument;
import fr.omny.flow.commands.wrapper.Arguments;

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
	public Optional<String> getValue(String textValue, CommandSender sender, Arguments precedentArguments) {
		return accepted.contains(textValue) ? Optional.of(textValue) : Optional.empty();
	}

	@Override
	public List<String> getValues(CommandSender sender, Arguments precedentArguments) {
		return this.accepted;
	}

}
