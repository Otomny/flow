package fr.omny.flow.listener;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.velocitypowered.api.proxy.ProxyServer;

import fr.omny.flow.api.aop.ClassRegister;
import fr.omny.flow.api.process.FlowProcess;
import fr.omny.odi.Injector;
import fr.omny.odi.Utils;

public class ListenerClassRegister implements ClassRegister {

	@Override
	public List<Object> register(FlowProcess plugin) {
		Set<Class<?>> listenerClasses = ClassRegister.getDeclared(plugin,
				preClass -> preClass.isAnnotationPresent(VelocityListener.class))
				.collect(Collectors.toSet());

		ProxyServer server = Injector.getService(ProxyServer.class);

		List<Object> generated = new ArrayList<>();
		for (Class<?> implementationClass : listenerClasses) {
			try {

				Object listenerInstance = Utils.callConstructor(implementationClass);

				generated.add(listenerInstance);
				Injector.addService(implementationClass, listenerInstance, true);

				server.getEventManager().register(plugin, listenerInstance);

			} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
					| InvocationTargetException e) {
				e.printStackTrace();
			}
		}
		return generated;
	}

	@Override
	public void postWire(Object object) {
		Injector.wire(object);
	}

}
