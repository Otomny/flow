package fr.omny.flow.placeholders;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import fr.omny.flow.aop.ClassRegister;
import fr.omny.flow.plugins.FlowPlugin;
import fr.omny.odi.Autowired;
import fr.omny.odi.Injector;
import fr.omny.odi.Utils;
import fr.omny.odi.utils.PreClass;

public class PlaceholderClassRegister implements ClassRegister {

	@Autowired
	private Placeholders placeholders;

	@Override
	public List<Object> register(FlowPlugin plugin) {
		Predicate<PreClass> placeholderFilter = preClass -> preClass.isInterfacePresent(PlaceholderProvider.class)
				&& preClass.isNotInner()
				&& preClass.isNotByteBuddy();

		Set<Class<?>> placeholderProviderClasses = Stream
				.concat(Utils.getClasses(plugin.getPackageName(), placeholderFilter).stream(),
						Utils.getClasses("fr.omny.flow", placeholderFilter).stream())
				.collect(Collectors.toSet());

		List<Object> generated = new ArrayList<>();
		for (Class<?> placeholderProviderClass : placeholderProviderClasses) {
			try {
				PlaceholderProvider instance = (PlaceholderProvider) Utils.callConstructor(placeholderProviderClass);

				for (Method method : placeholderProviderClass.getDeclaredMethods()) {
					if (Placeholder.class.isAssignableFrom(method.getReturnType())) {
						method.setAccessible(true);
						Placeholder placeholder = (Placeholder) Utils.callMethod(method, placeholderProviderClass, instance,
								new Object[] {});
						Injector.wire(placeholder);
						generated.add(placeholder);
						this.placeholders.registerPlaceholder(placeholder);
					} else if (Collection.class.isAssignableFrom(method.getReturnType())) {
						method.setAccessible(true);
						Type returnType = method.getGenericReturnType();
						if (returnType instanceof ParameterizedType) {
							ParameterizedType paramType = (ParameterizedType) returnType;
							Type[] argTypes = paramType.getActualTypeArguments();
							if (argTypes.length > 0) {
								Class<?> collectionContentClass = (Class<?>) argTypes[0];
								if (Placeholder.class.isAssignableFrom(collectionContentClass)) {
									@SuppressWarnings("unchecked")
									Collection<Placeholder> placeholder = (Collection<Placeholder>) Utils.callMethod(method,
											placeholderProviderClass, instance, new Object[] {});
									placeholder.stream().peek(Injector::wire).peek(generated::add)
											.forEach(this.placeholders::registerPlaceholder);
								}
							}
						}
					}
				}
			} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
					| InvocationTargetException e) {
				e.printStackTrace();
			}

		}
		return generated;
	}

}
