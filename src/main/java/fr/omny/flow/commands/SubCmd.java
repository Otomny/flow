package fr.omny.flow.commands;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.command.CommandSender;

import fr.omny.flow.commands.wrapper.Arguments;
import fr.omny.odi.Injector;
import lombok.Getter;
import lombok.Setter;

public abstract class SubCmd implements CommandComponent {

	private Map<Integer, List<CommandComponent>> comps;

	private String name;
	@Getter
	@Setter
	private String permission;
	private boolean optional;

	public SubCmd(String name, boolean optional) {
		this.comps = new HashMap<>();
		this.name = name;
		this.optional = optional;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public boolean isOptional() {
		return this.optional;
	}

	/**
	 * Shortcut for {@link SubCmd#registerComponent(int, CommandComponent)} method
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
		if (commandComponent instanceof SubCmd subCmd) {
			Injector.wire(subCmd);
		}
	}

	/**
	 * Execute the command
	 * 
	 * @param sender
	 * @param args
	 */
	public abstract void execute(CommandSender sender, Arguments args);

	/**
	 * @param sender
	 * @param args
	 * @return
	 */
	public boolean execute(CommandSender sender, String[] args) {
		Arguments arguments = new Arguments();

		// Main loop
		CompLoop: for (int index : comps.keySet()) {
			List<CommandComponent> components = this.comps.get(index);
			boolean allOptional = components.stream().allMatch(CommandComponent::isOptional);
			// args.length == 0
			// comps.size() == 1
			// index = 0
			if (allOptional && index >= args.length)
				break CompLoop;
			if (args.length > index) {
				String textValue = args[index];
				for (CommandComponent comp : components) {
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
			}

			StringBuilder usage = new StringBuilder("/" + this.getName() + " ");
			for (int i = 0; i < comps.size(); i++) {
				List<CommandComponent> localsComps = this.comps.get(i);
				if (localsComps.isEmpty())
					continue;
				if (localsComps.size() == 1) {
					usage.append("<" + localsComps.get(0).getName() + ">");
				} else {
					usage.append("<(");
					for (CommandComponent comp : localsComps) {
						usage.append(comp.getName() + "|");
					}
					usage.replace(usage.length() - 1, usage.length(), "");
					usage.append(")>");
				}
				usage.append(" ");
			}
			usage.replace(usage.length() - 1, usage.length(), "");
			sender.sendMessage("§cUsage: " + usage.toString());
			return false;
		}
		execute(sender, arguments);
		return true;
	}

}
