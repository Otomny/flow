package fr.omny.flow.tasks;


import java.lang.reflect.InvocationTargetException;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.bukkit.Bukkit;

import fr.omny.flow.aop.ClassRegister;
import fr.omny.flow.plugins.FlowPlugin;
import fr.omny.flow.utils.Converter;
import fr.omny.odi.Injector;
import fr.omny.odi.Utils;
import fr.omny.odi.utils.PreClass;

public class RunnableClassRegister implements ClassRegister {

	@Override
	public void register(FlowPlugin plugin) {
		Predicate<PreClass> runnablePredicate = preClass -> preClass.isNotInner()
				&& preClass.isInterfacePresent(Runnable.class) && preClass.isAnnotationPresent(RunnableConfig.class);

		Set<Class<?>> runnableClasses = Stream.concat(Utils.getClasses(plugin.getPackageName(), runnablePredicate).stream(),
				Utils.getClasses("fr.omny.flow", runnablePredicate).stream()).collect(Collectors.toSet());

		runnableClasses.forEach(klass -> {
			try {
				var bukkitConfig = klass.getAnnotation(RunnableConfig.class);
				var name = bukkitConfig.value();
				long period = bukkitConfig.period();
				long delay = bukkitConfig.delay();
				var isAsync = bukkitConfig.async();
				var type = bukkitConfig.type();
				var isPeriodic = period != 0L;
				var scheduler = Bukkit.getServer().getScheduler();
				var bukkitRunnable = (Runnable) Utils.callConstructor(klass);

				Injector.wire(bukkitRunnable);
				Injector.addService(klass, name, bukkitRunnable);

				if (type == SchedulerType.BUKKIT) {
					if (isAsync) {
						if (isPeriodic) {
							scheduler.runTaskTimerAsynchronously(plugin, bukkitRunnable, delay, period);
						} else {
							scheduler.runTaskLaterAsynchronously(plugin, bukkitRunnable, delay);
						}
					} else {
						if (isPeriodic) {
							scheduler.runTaskTimer(plugin, bukkitRunnable, delay, period);
						} else {
							scheduler.runTaskLater(plugin, bukkitRunnable, delay);
						}
					}
				} else if (type == SchedulerType.FLOW) {
					Dispatcher dispatcher = Injector.getService(Dispatcher.class);
					long periodAsMillis = Converter.tickToMillis(period);
					long delayAsMillis = Converter.tickToMillis(delay);
					if (isPeriodic) {
						dispatcher.submitFixedRate(bukkitRunnable, delayAsMillis, periodAsMillis, TimeUnit.MILLISECONDS);
					} else {
						dispatcher.submit(bukkitRunnable, delayAsMillis, TimeUnit.MILLISECONDS);
					}
				}

			} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
					| SecurityException e) {
				e.printStackTrace();
			}
		});
	}

}
