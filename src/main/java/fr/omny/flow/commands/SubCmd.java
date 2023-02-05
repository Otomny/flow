package fr.omny.flow.commands;


import org.bukkit.command.CommandSender;

public class SubCmd implements CommandComponent {

	private String name;
	private boolean optional;

	public SubCmd(String name) {
		this.name = name;
	}

	public SubCmd(String name, boolean optional) {
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

	public boolean execute(CommandSender sender, String[] args) {
		return false;
	}

}
