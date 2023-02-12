package fr.omny.flow.plugins;


import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandMap;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.redisson.api.RedissonClient;

import fr.omny.flow.attributes.ServerInfo;
import fr.omny.flow.commands.Cmd;
import fr.omny.flow.config.ConfigApplier;
import fr.omny.flow.data.CrudRepository;
import fr.omny.flow.data.ObjectUpdate;
import fr.omny.flow.data.Repository;
import fr.omny.flow.data.RepositoryFactory;
import fr.omny.flow.events.data.DataUpdateEvent;
import fr.omny.guis.OGui;
import fr.omny.guis.utils.ReflectionUtils;
import fr.omny.odi.Injector;
import fr.omny.odi.Utils;
import fr.omny.odi.utils.PreClass;

/**
 * 
 */
public abstract class FlowPlugin extends JavaPlugin implements ServerInfo {

	public abstract void load();

	public abstract String getPackageName();

	public void loadComponents() {

	}

	@Override
	public void onEnable() {
		this.load();
		OGui.register(this);

		// Read config file
		this.saveDefaultConfig();
		var configFile = getConfig();

		// Register pre wire initializer
		var configApplier = new ConfigApplier(configFile);
		Utils.registerCallConstructor(configApplier);
		Injector.registerWireListener(configApplier);

		List<String> ignorePackages = List.of("fr.omny.guis");

		String packageName = getPackageName();
		// Init all component instance
		Injector.startApplication(FlowPlugin.class, getLogger());
		Injector.addFrom("fr.omny.flow");
		// Load external components from others libraries
		loadComponents();
		// Add plugins components
		Injector.addFrom(packageName);
		// Get all classes that implements a repository with the annotation
		var classes = Utils.getClasses(getPackageName(), klass -> klass.isAnnotationPresent(Repository.class));
		for (Class<?> implementationClass : classes) {
			if (CrudRepository.class.isAssignableFrom(implementationClass)) {
				@SuppressWarnings({
						"unchecked", "rawtypes" })
				Class<? extends CrudRepository> sKlass = (Class<? extends CrudRepository>) implementationClass;
				@SuppressWarnings("unchecked")
				Object repositoryInstance = RepositoryFactory.createRepository(sKlass);
				Injector.addService(implementationClass, repositoryInstance);
			}
		}
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

		// No duplicate
		Set<Class<?>> listeners = Stream.concat(Utils.getClasses(packageName, listenerFilter).stream(),
				Utils.getClasses(getClass().getPackageName(), listenerFilter).stream()).collect(Collectors.toSet());

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
		var redissonClient = Injector.getService(RedissonClient.class);
		if (redissonClient != null) {
			redissonClient.getPatternTopic("repository_*").addListener(ObjectUpdate.class, (pattern, channel, msg) -> {
				var event = new DataUpdateEvent(pattern.toString(), channel.toString(), msg);
				getServer().getPluginManager().callEvent(event);
			});
		}
		serverStart(this);
		Injector.findEach(ServerInfo.class::isInstance).map(ServerInfo.class::cast)
				.forEach(sInfo -> sInfo.serverStart(this));

	}

	@Override
	public void onDisable() {
		Injector.findEach(ServerInfo.class::isAssignableFrom).map(ServerInfo.class::cast)
				.forEach(sInfo -> sInfo.serverStop(this));
		serverStop(this);
	}

}
