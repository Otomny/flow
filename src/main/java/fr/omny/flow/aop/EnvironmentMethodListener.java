package fr.omny.flow.aop;


import java.lang.reflect.Method;

import fr.omny.flow.plugins.Env;
import fr.omny.odi.listener.OnMethodComponentCallListener;

public class EnvironmentMethodListener implements OnMethodComponentCallListener {

	@Override
	public boolean isFiltered(Class<?> containgClass, Method method) {
		return method.isAnnotationPresent(RunOnDev.class) || method.isAnnotationPresent(RunOnProd.class);
	}

	@Override
	public boolean canCall(Class<?> containgClass, Method method) {
		return (method.isAnnotationPresent(RunOnDev.class) && Env.getEnvType().equalsIgnoreCase("development"))
				|| (method.isAnnotationPresent(RunOnProd.class) && Env.getEnvType().equalsIgnoreCase("production"));
	}

}
