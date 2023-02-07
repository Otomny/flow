package fr.omny.flow.commands;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import fr.omny.flow.commands.wrapper.Arguments;
import fr.omny.flow.translation.I18N;
import fr.omny.odi.Autowired;
import fr.omny.odi.Injector;

public abstract class Cmd extends Command implements CommandComponent {

	private Map<Integer, List<CommandComponent>> comps;

	@Autowired
	private Optional<I18N> translator;

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
		if (commandComponent instanceof SubCmd subCmd) {
			Injector.wire(subCmd);
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
							return subCmd.execute(sender, Arrays.copyOfRange(args, index + 1, args.length));
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

	@Override
	public List<String> tabComplete(CommandSender sender, String alias, String[] args, Location location)
			throws IllegalArgumentException {
		return this.tabComplete(sender, alias, args);
	}

	@Override
	public List<String> tabComplete(CommandSender sender, String alias, String[] args) throws IllegalArgumentException {
		List<String> array = new ArrayList<>();

		int depth = args.length == 0 ? 0 : args.length - 1;
		String lastWord = args.length == 0 ? "" : args[args.length - 1];
		List<CommandComponent> components = this.comps.get(depth);
		if (args.length >= 1) {
			String firstWord = args[0];
			List<CommandComponent> firstComponents = this.comps.get(0);
			if (firstComponents != null && !firstComponents.isEmpty()) {
				var possibleSubCmd = firstComponents.stream().filter(SubCmd.class::isInstance).map(SubCmd.class::cast)
						.filter(subCmd -> subCmd.getName().equalsIgnoreCase(firstWord)).findFirst();
				if (possibleSubCmd.isPresent()) {
					array.addAll(possibleSubCmd.get().tabComplete(sender, Arrays.copyOfRange(args, 1, args.length)));
					return array;
				}
			}
		}
		if (components != null) {
			components.stream().map(s -> {
				if (s instanceof SubCmd subCmd) {
					return List.of(subCmd.getName());
				} else if (s instanceof CmdArgument<?> cmdArgument) {
					return cmdArgument.getValues(sender, new Arguments());
				} else {
					return new ArrayList<String>();
				}
			}).flatMap(List::stream).filter(s -> s.toLowerCase().startsWith(lastWord.toLowerCase())).map(String::toLowerCase)
					.forEach(array::add);
		}
		Collections.sort(array);
		return array;
	}

}
