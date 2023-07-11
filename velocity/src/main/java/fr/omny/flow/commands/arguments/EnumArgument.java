package fr.omny.flow.commands.arguments;

import java.util.List;
import java.util.Optional;

import com.velocitypowered.api.command.CommandSource;

import fr.omny.flow.commands.Arguments;
import fr.omny.flow.commands.CmdArgument;

public class EnumArgument<E extends Enum<E>> extends CmdArgument<E> {

	private Class<? extends E> enumClass;

	public EnumArgument(Class<? extends E> enumClass, boolean optional) {
		super(enumClass.getSimpleName(), optional);
		this.enumClass = enumClass;
	}

	@Override
	@SuppressWarnings("unchecked")
	public Optional<E> getValue(String textValue, CommandSource sender, Arguments precedentArguments) {
		Enum<E>[] constants = enumClass.getEnumConstants();
		for (Enum<E> constant : constants) {
			if (constant.toString().equalsIgnoreCase(textValue)) {
				return Optional.of((E) constant);
			}
		}
		return Optional.empty();
	}

	@Override
	public List<String> getValues(CommandSource sender, Arguments precedentArguments) {
		return List.of(enumClass.getEnumConstants()).stream().map(Enum::toString).toList();
	}

}
