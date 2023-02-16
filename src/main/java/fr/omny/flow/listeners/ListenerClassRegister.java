package fr.omny.flow.listeners;


import java.lang.reflect.InvocationTargetException;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.bukkit.event.Listener;

import fr.omny.flow.aop.ClassRegister;
import fr.omny.flow.plugins.FlowPlugin;
import fr.omny.odi.Injector;
import fr.omny.odi.Utils;
import fr.omny.odi.utils.PreClass;

public class ListenerClassRegister implements ClassRegister {

	@Override
	public void register(FlowPlugin plugin) {
		// Init listeners
		Predicate<PreClass> listenerFilter = preClass -> preClass.isInterfacePresent(Listener.class)
				&& !FlowPlugin.IGNORED_PACKAGES.stream().anyMatch(s -> s.startsWith(preClass.getPackageName()))
				&& preClass.isNotInner();

		// No duplicate
		Set<Class<?>> listeners = Stream.concat(Utils.getClasses(plugin.getPackageName(), listenerFilter).stream(),
				Utils.getClasses("fr.omny.flow", listenerFilter).stream()).collect(Collectors.toSet());

		listeners.forEach(klass -> {
			try {
				Listener listenerInstance = (Listener) Utils.callConstructor(klass);
				if (listenerInstance != null) {
					Injector.wire(listenerInstance);
					plugin.getServer().getPluginManager().registerEvents(listenerInstance, plugin);
				}
			} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
					| SecurityException e) {
				e.printStackTrace();
			}
		});
	}

}
