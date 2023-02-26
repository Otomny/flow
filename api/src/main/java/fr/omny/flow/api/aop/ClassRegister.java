package fr.omny.flow.api.aop;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;

import fr.omny.flow.api.process.FlowProcess;
import fr.omny.odi.Utils;
import fr.omny.odi.utils.PreClass;

/**
 * 
 */
public interface ClassRegister {
	
	default Stream<Class<?>> getDeclared(FlowProcess plugin, Predicate<PreClass> predicate) {
		return plugin.declaredPackages().stream()
				.flatMap(packageName -> Utils.getClasses(packageName,
						klass -> predicate.test(klass) && klass.isNotByteBuddy())
						.stream());
	}

	/**
	 * 
	 * @param plugin
	 * @return All the instances created
	 */
	List<Object> register(FlowProcess plugin);

	/**
	 * 
	 * @param object
	 */
	void postWire(Object object);

}
