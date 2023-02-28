package fr.omny.flow.listeners;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

import fr.omny.flow.api.aop.ClassRegister;
import fr.omny.flow.api.process.FlowProcess;
import fr.omny.flow.listeners.aop.ListenerProvider;
import fr.omny.flow.plugins.FlowPlugin;
import fr.omny.odi.Injector;
import fr.omny.odi.Utils;
import fr.omny.odi.utils.PreClass;

public class ListenerProviderClassRegister implements ClassRegister {

	@Override
	public List<Object> register(FlowProcess process) {
		Predicate<PreClass> listenerProviderFilter = preClass -> preClass.isInterfacePresent(ListenerProvider.class)
				&& !FlowPlugin.IGNORED_PACKAGES.stream().anyMatch(s -> s.startsWith(preClass.getPackageName()))
				&& preClass.isNotInner()
				&& preClass.isNotByteBuddy();

		// Listeners providers
		Set<Class<?>> listenerProviders = Stream
				.concat(ClassRegister.getDeclared(process, listenerProviderFilter),
						Utils.getClasses("fr.omny.flow", listenerProviderFilter).stream())
				.collect(Collectors.toSet());

		Plugin plugin = (Plugin) process;
		return listenerProviders.stream().map(klass -> {
			List<Object> generated = new ArrayList<>();
			try {
				ListenerProvider listenerProviderInstance = (ListenerProvider) Utils.callConstructor(klass);
				Injector.wire(listenerProviderInstance);
				generated.add(listenerProviderInstance);
				for (Method method : klass.getDeclaredMethods()) {
					if (method.getReturnType() == Listener.class) {
						Listener listenerInstance = (Listener) Utils.callMethod(method, klass, listenerProviderInstance,
								new Object[] {});
						if (listenerInstance != null) {
							Injector.wire(listenerInstance);
							plugin.getServer().getPluginManager().registerEvents(listenerInstance, plugin);
							generated.add(listenerInstance);
						}
					}
				}
			} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
					| InvocationTargetException e) {
				e.printStackTrace();
			}
			return generated;
		}).flatMap(List::stream).toList();
	}

	@Override
	public void postWire(Object object) {
	}

}
