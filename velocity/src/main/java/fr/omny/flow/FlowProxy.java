package fr.omny.flow;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import com.moandjiezana.toml.Toml;
import com.velocitypowered.api.proxy.ProxyServer;

import fr.omny.flow.api.aop.ClassRegister;
import fr.omny.flow.api.aop.EnvironmentMethodListener;
import fr.omny.flow.api.process.FlowProcess;
import fr.omny.flow.config.ConfigApplier;
import fr.omny.odi.Injector;
import fr.omny.odi.Utils;
import fr.omny.odi.utils.PreClass;

public abstract class FlowProxy implements FlowProcess {

	public FlowProxy(ProxyServer server, Logger logger, Path dataDirectory) {
		init(server, logger, dataDirectory);
	}

	@Override
	public List<String> declaredPackages() {
		return List.of("fr.omny");
	}

	public abstract String getPackageName();

	public abstract void loadComponents();

	public void init(ProxyServer server, Logger logger, Path dataDirectory) {
		// Load plugin config
		var dataFolder = dataDirectory.toFile();
		// config path
		var configPath = dataDirectory.resolve("config.toml");
		if (!dataFolder.exists()) {
			dataFolder.mkdir();
		}
		if (!configPath.toFile().exists()) {
			try {
				configPath.toFile().createNewFile();
				// Load config.toml
				try (InputStream in = getClass().getResourceAsStream("/config.toml");
						BufferedReader reader = new BufferedReader(new InputStreamReader(in))) {
					Files.copy(in, configPath, StandardCopyOption.REPLACE_EXISTING);
				} catch (Exception e) {
					e.printStackTrace();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}

		}

		Toml pluginConfig = new Toml().read(configPath.toFile());
		// Register pre wire initializer
		var configApplier = new ConfigApplier(pluginConfig);
		Utils.registerCallConstructor(configApplier);
		Injector.registerWireListener(configApplier);
		Injector.registerMethodCallListener(new EnvironmentMethodListener());

		Injector.startApplication(FlowProxy.class, logger);
		Injector.addFrom("fr.omny.flow");
		declaredPackages().forEach(Injector::addFrom);

		loadComponents();

		Injector.addService(Toml.class, "config", pluginConfig, true);

		Injector.addService(ProxyServer.class, server, true);

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
