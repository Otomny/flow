package fr.omny.flow.listeners;


import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.bukkit.event.Listener;

import fr.omny.flow.aop.ClassRegister;
import fr.omny.flow.listeners.aop.ListenerProvider;
import fr.omny.flow.plugins.FlowPlugin;
import fr.omny.odi.Injector;
import fr.omny.odi.Utils;
import fr.omny.odi.utils.PreClass;

public class ListenerProviderClassRegister implements ClassRegister {

	@Override
	public void register(FlowPlugin plugin) {
		Predicate<PreClass> listenerProviderFilter = preClass -> preClass.isInterfacePresent(ListenerProvider.class)
				&& !FlowPlugin.IGNORED_PACKAGES.stream().anyMatch(s -> s.startsWith(preClass.getPackageName()))
				&& preClass.isNotInner();

		// Listeners providers
		Set<Class<?>> listenerProviders = Stream
				.concat(Utils.getClasses(plugin.getPackageName(), listenerProviderFilter).stream(),
						Utils.getClasses("fr.omny.flow", listenerProviderFilter).stream())
				.collect(Collectors.toSet());

		listenerProviders.forEach(klass -> {
			try {
				ListenerProvider listenerProviderInstance = (ListenerProvider) Utils.callConstructor(klass);
				Injector.wire(listenerProviderInstance);
				for (Method method : klass.getDeclaredMethods()) {
					if (method.getReturnType() == Listener.class) {
						Listener listenerInstance = (Listener) Utils.callMethod(method, klass, listenerProviderInstance,
								new Object[] {});
						if (listenerInstance != null) {
							Injector.wire(listenerInstance);
							plugin.getServer().getPluginManager().registerEvents(listenerInstance, plugin);
						}
					}
				}
			} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
					| InvocationTargetException e) {
				e.printStackTrace();
			}

		});
	}

}
