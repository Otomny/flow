package fr.omny.flow.listeners;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

import fr.omny.flow.api.aop.ClassRegister;
import fr.omny.flow.api.process.FlowProcess;
import fr.omny.flow.plugins.FlowPlugin;
import fr.omny.odi.Injector;
import fr.omny.odi.Utils;
import fr.omny.odi.utils.PreClass;

public class ListenerClassRegister implements ClassRegister {

	@Override
	public List<Object> register(FlowProcess process) {
		// Init listeners
		Predicate<PreClass> listenerFilter = preClass -> preClass.isInterfacePresent(Listener.class)
				&& !FlowPlugin.IGNORED_PACKAGES.stream().anyMatch(s -> s.startsWith(preClass.getPackageName()))
				&& preClass.isNotInner()
				&& preClass.isNotByteBuddy();

		// No duplicate
		Set<Class<?>> listeners = Stream.concat(getDeclared(process, listenerFilter),
				Utils.getClasses("fr.omny.flow", listenerFilter).stream()).collect(Collectors.toSet());

		Plugin plugin = (Plugin) process;
		return listeners.stream().map(klass -> {
			try {
				Listener listenerInstance = (Listener) Utils.callConstructor(klass);
				if (listenerInstance != null) {
					Injector.wire(listenerInstance);
					plugin.getServer().getPluginManager().registerEvents(listenerInstance, plugin);
				}
				return listenerInstance;
			} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
					| SecurityException e) {
				throw new RuntimeException(e);
			}
		}).map(Object.class::cast).toList();
	}

	@Override
	public void postWire(Object object) {
	}

}
