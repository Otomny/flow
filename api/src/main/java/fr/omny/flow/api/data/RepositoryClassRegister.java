package fr.omny.flow.api.data;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import fr.omny.flow.api.aop.ClassRegister;
import fr.omny.flow.api.process.FlowProcess;
import fr.omny.odi.Autowired;
import fr.omny.odi.Injector;
import fr.omny.odi.Utils;

public class RepositoryClassRegister implements ClassRegister {

	@Autowired
	private RepositoryFactory repositoryFactory;

	@Override
	public List<Object> register(FlowProcess plugin) {
		// Get all classes that implements a repository with the annotation
		List<Object> generated = new ArrayList<>();

		Stream<Class<?>> allDeclaredPackages = plugin.declaredPackages().stream()
				.flatMap(packageName -> Utils.getClasses(packageName,
						klass -> klass.isAnnotationPresent(Repository.class) && klass.isNotByteBuddy())
						.stream());

		Set<Class<?>> repositoryClasses = Stream
				.concat(allDeclaredPackages,
						Utils.getClasses("fr.omny.flow.api",
								klass -> klass.isAnnotationPresent(Repository.class) && klass.isNotByteBuddy()).stream())
				.collect(Collectors.toSet());
		for (Class<?> implementationClass : repositoryClasses) {
			if (CrudRepository.class.isAssignableFrom(implementationClass)) {
				@SuppressWarnings({
						"unchecked", "rawtypes" })
				Class<? extends CrudRepository> sKlass = (Class<? extends CrudRepository>) implementationClass;
				@SuppressWarnings("unchecked")
				Object repositoryInstance = repositoryFactory.createRepository(sKlass);
				generated.add(repositoryInstance);
				Injector.addService(implementationClass, repositoryInstance, true);
			}
		}
		return generated;
	}

	@Override
	public void postWire(Object object) {
		Injector.wire(object);
	}

}
