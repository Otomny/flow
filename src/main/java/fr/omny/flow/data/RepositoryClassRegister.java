package fr.omny.flow.data;

import java.util.ArrayList;
import java.util.List;

import fr.omny.flow.aop.ClassRegister;
import fr.omny.flow.plugins.FlowPlugin;
import fr.omny.odi.Injector;
import fr.omny.odi.Utils;

public class RepositoryClassRegister implements ClassRegister {

	@Override
	public List<Object> register(FlowPlugin plugin) {
		// Get all classes that implements a repository with the annotation
		List<Object> generated = new ArrayList<>();
		var repositoryClasses = Utils.getClasses(plugin.getPackageName(),
				klass -> klass.isAnnotationPresent(Repository.class) && klass.isNotByteBuddy());
		for (Class<?> implementationClass : repositoryClasses) {
			if (CrudRepository.class.isAssignableFrom(implementationClass)) {
				@SuppressWarnings({
						"unchecked", "rawtypes" })
				Class<? extends CrudRepository> sKlass = (Class<? extends CrudRepository>) implementationClass;
				@SuppressWarnings("unchecked")
				Object repositoryInstance = RepositoryFactory.createRepository(sKlass);
				generated.add(repositoryInstance);
				Injector.addService(implementationClass, repositoryInstance);
			}
		}
		return generated;
	}

	@Override
	public void postWire(Object object) {
	}

}
