package fr.omny.flow;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.velocitypowered.api.proxy.ProxyServer;

import fr.omny.flow.api.aop.ClassRegister;
import fr.omny.flow.api.process.FlowProcess;
import fr.omny.odi.Injector;
import fr.omny.odi.Utils;
import fr.omny.odi.utils.PreClass;

public abstract class FlowProxy implements FlowProcess {

	public FlowProxy(ProxyServer server) {
		init(server);
	}

	@Override
	public List<String> declaredPackages() {
		return List.of("fr.omny");
	}

	public abstract String getPackageName();

	public void init(ProxyServer server) {
		Injector.addService(ProxyServer.class, server);

		Predicate<PreClass> classRegisterFilter = preClass -> preClass.isInterfacePresent(ClassRegister.class)
				&& preClass.isNotInner()
				&& preClass.isNotByteBuddy();

		Set<Class<?>> classes = ClassRegister.getDeclared(this, classRegisterFilter).collect(Collectors.toSet());
		Map<ClassRegister, List<Object>> generated = new HashMap<>();

		for (Class<?> classRegisterImpl : classes) {
			try {
				ClassRegister classRegister = (ClassRegister) Utils.callConstructor(classRegisterImpl);
				Utils.autowire(classRegister);
				generated.put(classRegister, classRegister.register(this));
			} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
					| InvocationTargetException e) {
				e.printStackTrace();
			}
		}
	}

	public void stop() {

	}

}
