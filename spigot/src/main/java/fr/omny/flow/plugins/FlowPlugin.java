package fr.omny.flow.plugins;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.bson.codecs.pojo.PojoCodecProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.redisson.api.RedissonClient;

import fr.omny.flow.api.aop.ClassRegister;
import fr.omny.flow.api.aop.EnvironmentMethodListener;
import fr.omny.flow.api.aop.RunOnDev;
import fr.omny.flow.api.aop.RunOnProd;
import fr.omny.flow.api.data.ObjectUpdate;
import fr.omny.flow.api.data.implementation.FlowCodec;
import fr.omny.flow.api.process.Env;
import fr.omny.flow.api.process.FlowProcess;
import fr.omny.flow.attributes.ServerInfo;
import fr.omny.flow.config.ConfigApplier;
import fr.omny.flow.events.data.DataUpdateEvent;
import fr.omny.flow.utils.NMS;
import fr.omny.flow.utils.mongodb.codecs.ItemStackCodec;
import fr.omny.flow.utils.mongodb.codecs.LocationCodec;
import fr.omny.flow.utils.mongodb.codecs.StoredPlayerInventoryCodec;
import fr.omny.flow.utils.mongodb.codecs.WorldCodec;
import fr.omny.flow.world.Area;
import fr.omny.flow.world.schematic.SchematicCodec;
import fr.omny.guis.OGui;
import fr.omny.odi.Injector;
import fr.omny.odi.Utils;
import fr.omny.odi.utils.PreClass;
import fr.omny.odi.utils.Predicates;

/**
 * 
 */
public abstract class FlowPlugin extends JavaPlugin implements ServerInfo, FlowProcess {

	public static final List<String> IGNORED_PACKAGES = List.of("fr.omny.guis");

	private static NMS nms;

	public static NMS getNms() {
		return nms;
	}

	private static void setNMS(NMS nms) {
		FlowPlugin.nms = nms;
	}

	public abstract void load();

	public abstract String getPackageName();

	public void loadComponents() {
		var codecs = Injector.getService(FlowCodec.class);
		codecs.addAll(
				List.of(new LocationCodec(), new WorldCodec(), new ItemStackCodec(), new SchematicCodec(),
						new StoredPlayerInventoryCodec()));
		codecs.registerCodecProvider(PojoCodecProvider.builder().register(Area.class).build());

	}

	@Override
	public void onEnable() {
		this.load();
		OGui.register(this);

		// Read config file
		this.saveDefaultConfig();
		var configFile = getConfig();

		setNMS(new NMS());

		// Register pre wire initializer
		var configApplier = new ConfigApplier(configFile);
		Utils.registerCallConstructor(configApplier);
		Injector.registerWireListener(configApplier);
		Injector.registerMethodCallListener(new EnvironmentMethodListener());

		String packageName = getPackageName();
		// Init all component instance
		Injector.startApplication(FlowPlugin.class, getLogger());
		Injector.addFrom("fr.omny.flow");
		// Load external components from others libraries
		loadComponents();
		// Add plugins components
		Injector.addFrom(packageName);

		// Get all classes that implement ClassRegister interface, and call their
		// "register" method
		// This way, we can implement in their own classes, specials components that get
		// auto registered (Cmd, Listener, etc...)
		Predicate<PreClass> classRegisterFilter = preClass -> preClass.isInterfacePresent(ClassRegister.class)
				&& preClass.isNotInner()
				&& preClass.isNotByteBuddy();
		Set<Class<?>> classes = Stream.concat(Utils.getClasses(packageName, classRegisterFilter).stream(),
				Utils.getClasses("fr.omny.flow", classRegisterFilter).stream()).collect(Collectors.toSet());
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

		var redissonClient = Injector.getService(RedissonClient.class);
		if (redissonClient != null) {
			redissonClient.getPatternTopic("repository_*").addListener(ObjectUpdate.class, (pattern, channel, msg) -> {
				var event = new DataUpdateEvent(pattern.toString(), channel.toString(), msg);
				getServer().getPluginManager().callEvent(event);
			});
		}

		// In all registered services, run the method annotated with RunOnDev or
		// RunOnProd
		Injector.findEachWithClasses(Predicates.alwaysTrue()).forEach(entry -> {
			for (Method method : entry.getKey().getDeclaredMethods()) {
				var values = entry.getValue().values();
				if (method.isAnnotationPresent(RunOnDev.class) && Env.getEnvType().equalsIgnoreCase("development")) {
					values.forEach(
							proxyInstance -> Utils.callMethodQuiet(method, proxyInstance.getClass(), proxyInstance, new Object[] {}));
				} else if (method.isAnnotationPresent(RunOnProd.class) && Env.getEnvType().equalsIgnoreCase("production")) {
					values.forEach(
							proxyInstance -> Utils.callMethodQuiet(method, proxyInstance.getClass(), proxyInstance, new Object[] {}));
				}
			}
		});

		generated.forEach((classRegister, generatedObj) -> generatedObj.forEach(obj -> {
			Injector.wire(obj);
			classRegister.postWire(obj);
		}));
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
