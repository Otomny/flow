package fr.omny.flow.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;

import fr.omny.flow.commands.arguments.SentenceArgument;
import fr.omny.flow.permission.PermissionProvider;
import fr.omny.odi.Autowired;
import fr.omny.odi.Injector;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.Component;

public abstract class SubCmd implements CommandComponent {

	@Autowired
	private PermissionProvider permissionProvider;

	@Getter
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
	public abstract void execute(CommandSource sender, Arguments args);

	/**
	 * @param sender
	 * @param args
	 * @return
	 */
	public boolean execute(CommandSource sender, String[] args) {
		if (sender instanceof Player player) {
			if (!permissionProvider.hasPermission(player, this.getPermission())) {
				return false;
			}
		}
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
						// Specific component
						if (cmdArgument instanceof SentenceArgument setence) {
							int from = index;
							int to = args.length;
							var sentence = String.join(" ", IntStream.range(from, to).mapToObj(i -> args[i]).toList());
							arguments.put(index, sentence);
							break CompLoop;
						} else {
							var value = cmdArgument.getValue(textValue, sender, arguments);
							if (value.isPresent()) {
								arguments.put(index, value.get());
								continue CompLoop;
							}
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
			sender.sendMessage(Component.text("§cUsage: " + usage.toString()));
			if (sender instanceof Player player) {
			}
			return false;
		}
		execute(sender, arguments);
		return true;
	}

	public List<String> tabComplete(CommandSource sender, String[] args) {
		List<String> array = new ArrayList<>();
		if (sender instanceof Player player)
			if (!permissionProvider.hasPermission(player, this.getPermission()))
				return array;

		int depth = args.length == 0 ? 0 : args.length - 1;
		String lastWord = args.length == 0 ? "" : args[args.length - 1];
		List<CommandComponent> components = this.comps.get(depth);
		if (args.length >= 1) {
			String firstWord = args[0];
			List<CommandComponent> firstComponents = this.comps.get(0);
			if (firstComponents != null && !firstComponents.isEmpty()) {
				firstComponents.stream().filter(SubCmd.class::isInstance).map(SubCmd.class::cast)
						.filter(subCmd -> subCmd.getName().equalsIgnoreCase(firstWord)).findFirst()
						.ifPresent(subCmd -> array.addAll(subCmd.tabComplete(sender, Arrays.copyOfRange(args, 1, args.length))));
			}
		}
		if (components != null) {
			components.stream().map(s -> {
				if (s instanceof SubCmd subCmd) {
					if (sender instanceof Player player) {
						if (!permissionProvider.hasPermission(player, subCmd.getPermission())) {
							return new ArrayList<String>();
						}
					}
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
