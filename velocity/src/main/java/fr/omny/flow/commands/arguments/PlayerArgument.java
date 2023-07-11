package fr.omny.flow.commands.arguments;

import java.util.List;
import java.util.Optional;


import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;

import fr.omny.flow.commands.Arguments;
import fr.omny.flow.commands.CmdArgument;
import fr.omny.odi.Autowired;

public class PlayerArgument extends CmdArgument<Player> {

	@Autowired
	private ProxyServer proxyServer;

	public PlayerArgument(boolean optional) {
		super("Player", optional);
	}

	@Override
	public Optional<Player> getValue(String textValue, CommandSource sender, Arguments precedentArguments) {
		return proxyServer.getPlayer(textValue);
	}

	@Override
	public List<String> getValues(CommandSource sender, Arguments precedentArguments) {
		return proxyServer.getAllPlayers()
				.stream()
				.map(Player::getUsername)
				.toList();
	}

}
