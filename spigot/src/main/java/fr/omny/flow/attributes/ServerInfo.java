package fr.omny.flow.attributes;

import org.bukkit.plugin.Plugin;

public interface ServerInfo {
	
	void serverStart(Plugin plugin);
	
	void serverStop(Plugin plugin);

}
