package fr.omny.flow.aop;

import java.util.List;

import fr.omny.flow.plugins.FlowPlugin;

/**
 * 
 */
public interface ClassRegister {

	/**
	 * 
	 * @param plugin
	 * @return All the instances created
	 */
	List<Object> register(FlowPlugin plugin);

	/**
	 * 
	 * @param object
	 */
	void postWire(Object object);

}
