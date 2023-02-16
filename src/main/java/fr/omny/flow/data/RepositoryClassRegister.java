package fr.omny.flow.data;


import fr.omny.flow.aop.ClassRegister;
import fr.omny.flow.plugins.FlowPlugin;
import fr.omny.odi.Injector;
import fr.omny.odi.Utils;

public class RepositoryClassRegister implements ClassRegister {

	@Override
	public void register(FlowPlugin plugin) {
		// Get all classes that implements a repository with the annotation
		var repositoryClasses = Utils.getClasses(plugin.getPackageName(), klass -> klass.isAnnotationPresent(Repository.class));
		for (Class<?> implementationClass : repositoryClasses) {
			if (CrudRepository.class.isAssignableFrom(implementationClass)) {
				@SuppressWarnings({
						"unchecked", "rawtypes" })
				Class<? extends CrudRepository> sKlass = (Class<? extends CrudRepository>) implementationClass;
				@SuppressWarnings("unchecked")
				Object repositoryInstance = RepositoryFactory.createRepository(sKlass);
				Injector.addService(implementationClass, repositoryInstance);
			}
		}
	}

}
