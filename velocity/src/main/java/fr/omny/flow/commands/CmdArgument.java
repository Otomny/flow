package fr.omny.flow.commands;

import java.util.List;
import java.util.Optional;

import com.velocitypowered.api.command.CommandSource;

/**
 * Command argument Ex:
 * <ul>
 * <li>Int argument</li>
 * <li>Double argument</li>
 * <li>Player argument</li>
 * <li>Reference to other object</li>
 * </ul>
 */
public abstract class CmdArgument<T> implements CommandComponent {

	private String name;
	private boolean optional;

	/**
	 * Create a command argument
	 * 
	 * @param name     The name of the argument
	 * @param optional if it can be skipped
	 */
	public CmdArgument(String name, boolean optional) {
		this.name = name;
		this.optional = optional;
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public boolean isOptional() {
		return this.optional;
	}

	/**
	 * Get the value from the string passed in the command
	 * 
	 * @param textValue          The text value
	 * @param player             The player that executed the command
	 * @param precedentArguments The precedent arguments
	 * @return The object or empty
	 */
	public abstract Optional<T> getValue(String textValue, CommandSource sender, Arguments precedentArguments);

	/**
	 * The list of possible values for an object
	 * 
	 * @param player             The player that executed the command
	 * @param precedentArguments The precedent arguments
	 * @return The list of possible values
	 */
	public abstract List<String> getValues(CommandSource sender, Arguments precedentArguments);

}
