package fr.omny.flow.commands;


import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class Cmd extends Command implements CommandComponent {

	public Cmd(String name) {
		super(name);
	}

	public Cmd(String name, String description, String usageMessage, List<String> aliases) {
		super(name, description, usageMessage, aliases);
	}

	@Override
	public String getName() {
		return super.getName();
	}

	@Override
	public boolean isOptional() {
		return false;
	}

	@Override
	public boolean execute(CommandSender arg0, String arg1, String[] arg2) {
		// TODO Auto-generated method stub
		return false;
	}

}
