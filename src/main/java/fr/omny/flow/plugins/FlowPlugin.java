package fr.omny.flow.plugins;


import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandMap;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import fr.omny.flow.commands.Cmd;
import fr.omny.guis.OGui;
import fr.omny.guis.utils.ReflectionUtils;
import fr.omny.odi.Injector;
import fr.omny.odi.Utils;
import fr.omny.odi.utils.PreClass;

/**
 * 
 */
public abstract class FlowPlugin extends JavaPlugin {

	public abstract void load();

	public abstract void start();

	public abstract void stop();

	public abstract String getPackageName();

	public void loadComponents() {

	}

	@Override
	public void onEnable() {
		this.load();
		OGui.register(this);

		List<String> ignorePackages = List.of("fr.omny.guis");

		String packageName = getPackageName();
		// Init all component instance
		Injector.startApplication(getClass(), getLogger());
		// Add plugins components
		Injector.addFrom(packageName);
		// Load external components from others libraries
		loadComponents();
		// Init all commands
		Predicate<PreClass> commandsFilter = preClass -> preClass.isSuperClass(Cmd.class);
		List<Class<?>> commands = Stream.concat(Utils.getClasses(packageName, commandsFilter).stream(),
				Utils.getClasses(getClass().getPackageName(), commandsFilter).stream()).toList();

		Optional<CommandMap> map = ReflectionUtils.get(Bukkit.getServer(), "commandMap");
		map.ifPresentOrElse(commandMap -> {
			commands.forEach(klass -> {
				try {
					Cmd cmdInstance = Cmd.class.cast(klass.getConstructor().newInstance());
					Injector.wire(cmdInstance);
					commandMap.register(cmdInstance.getName(), "", cmdInstance);
				} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
						| NoSuchMethodException | SecurityException e) {
					e.printStackTrace();
				}
			});
		}, () -> getLogger().warning("Could not find commandMap"));

		// Init listeners
		Predicate<PreClass> listenerFilter = preClass -> preClass.isInterfacePresent(Listener.class)
				&& !ignorePackages.stream().anyMatch(s -> s.startsWith(preClass.getPackageName()));

		List<Class<?>> listeners = Stream.concat(Utils.getClasses(packageName, listenerFilter).stream(),
				Utils.getClasses(getClass().getPackageName(), listenerFilter).stream()).toList();

		listeners.forEach(klass -> {
			try {
				Listener listenerInstance = Listener.class.cast(klass.getConstructor().newInstance());
				Injector.wire(listenerInstance);
				getServer().getPluginManager().registerEvents(listenerInstance, this);
			} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
					| NoSuchMethodException | SecurityException e) {
				e.printStackTrace();
			}
		});

	}

	@Override
	public void onDisable() {
		this.stop();
	}

}
