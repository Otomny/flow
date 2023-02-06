package fr.omny.flow.commands.arguments;


import java.util.List;
import java.util.Optional;

import org.bukkit.command.CommandSender;

import fr.omny.flow.commands.CmdArgument;
import fr.omny.flow.commands.wrapper.Arguments;

public class EnumArgument<E extends Enum<E>> extends CmdArgument<E> {

	private Class<? extends E> enumClass;

	public EnumArgument(Class<? extends E> enumClass, boolean optional) {
		super(enumClass.getSimpleName(), optional);
		this.enumClass = enumClass;
	}

	@Override
	@SuppressWarnings("unchecked")
	public Optional<E> getValue(String textValue, CommandSender sender, Arguments precedentArguments) {
		Enum<E>[] constants = enumClass.getEnumConstants();
		for (Enum<E> constant : constants) {
			if (constant.toString().equals(textValue)) {
				return Optional.of((E) constant);
			}
		}
		return Optional.empty();
	}

	@Override
	public List<String> getValues(CommandSender sender, Arguments precedentArguments) {
		return List.of(enumClass.getEnumConstants()).stream().map(Enum::toString).toList();
	}

}
