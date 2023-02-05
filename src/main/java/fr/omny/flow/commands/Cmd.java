package fr.omny.flow.commands;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import fr.omny.flow.commands.wrapper.Arguments;

public abstract class Cmd extends Command implements CommandComponent {

	private Map<Integer, List<CommandComponent>> comps;

	/**
	 * @param name
	 */
	public Cmd(String name) {
		super(name);
		localInit();
	}

	/**
	 * @param name
	 * @param description
	 * @param usageMessage
	 * @param aliases
	 */
	public Cmd(String name, String description, String usageMessage, List<String> aliases) {
		super(name, description, usageMessage, aliases);
		localInit();
	}

	protected void localInit() {
		this.comps = new HashMap<>();
	}

	/**
	 * Shortcut for {@link Cmd#registerComponent(int, CommandComponent)} method
	 * 
	 * @param index
	 * @param commandComponent
	 * @throws IllegalArgumentException if {@link Cmd} instance is passed
	 */
	public void rc(int index, CommandComponent commandComponent) {
		registerComponent(index, commandComponent);
	}

	/**
	 * Register a component for this method
	 * 
	 * @param index
	 * @param commandComponent
	 * @throws IllegalArgumentException if {@link Cmd} instance is passed
	 */
	public void registerComponent(int index, CommandComponent commandComponent) {
		if (commandComponent instanceof Cmd)
			throw new IllegalArgumentException("You can't register a Cmd instance");
		if (comps.containsKey(index)) {
			comps.get(index).add(commandComponent);
		} else {
			List<CommandComponent> commandComponents = new ArrayList<>();
			commandComponents.add(commandComponent);
			comps.put(index, commandComponents);
		}
	}

	@Override
	public String getName() {
		return super.getName();
	}

	@Override
	public boolean isOptional() {
		return false;
	}

	/**
	 * Execute the command
	 * 
	 * @param sender
	 * @param args
	 */
	public abstract void execute(CommandSender sender, Arguments args);

	@Override
	public boolean execute(CommandSender sender, String commandLabel, String[] args) {
		// "/test a b c"
		// commandLabel = test
		// args= [a, b, c]
		// arg 0

		Arguments arguments = new Arguments();

		// Main loop
		CompLoop: for (int index : comps.keySet()) {
			List<CommandComponent> comps = this.comps.get(index);
			boolean allOptional = comps.stream().allMatch(CommandComponent::isOptional);

			if (!allOptional && args.length <= index)
				return false;
			String textValue = args[index];
			for (CommandComponent comp : comps) {
				if (comp instanceof CmdArgument<?> cmdArgument) {
					var value = cmdArgument.getValue(textValue, sender, arguments);
					if (value.isPresent()) {
						arguments.put(index, value.get());
						continue CompLoop;
					}
				} else if (comp instanceof SubCmd subCmd) {
					if (textValue.equalsIgnoreCase(subCmd.getName())) {
						return subCmd.execute(sender, Arrays.copyOfRange(args, index, args.length));
					}
				}
			}
			// TODO impossible (no command comps found for specific string)
			return false;
		}
		execute(sender, arguments);
		return true;
	}

	@Override
	public List<String> tabComplete(CommandSender sender, String alias, String[] args, Location location)
			throws IllegalArgumentException {
		return super.tabComplete(sender, alias, args, location);
	}

}
