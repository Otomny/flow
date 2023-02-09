package fr.omny.flow.utils.mongodb;


import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.function.Consumer;

import fr.omny.flow.aop.GenericProxyFactory;
import fr.omny.flow.utils.Objects;

@SuppressWarnings("unchecked")
public class ProxyMongoObject<T> implements InvocationHandler {

	public static <T> T createProxy(T originalInstance, Consumer<FieldData<T>> fieldUpdate) throws Exception {
		return (T) GenericProxyFactory.newProxyInstance(originalInstance.getClass(),
				new ProxyMongoObject<T>(originalInstance, fieldUpdate));
	}

	public static record FieldData<T>(T instance, Field field, Object oldValue, Object newValue) {}

	private T instance;
	private Class<? extends T> klass;
	private Consumer<FieldData<T>> fieldUpdate;

	private ProxyMongoObject(T originalInstance, Consumer<FieldData<T>> fieldUpdate) {
		this.instance = originalInstance;
		this.fieldUpdate = fieldUpdate;
		this.klass = (Class<? extends T>) this.instance.getClass();
	}

	@Override
	public Object invoke(Object proxyObj, Method method, Object[] arguments) throws Throwable {
		String methodName = method.getName();
		Class<?>[] parametersType = new Class<?>[method.getParameters().length];
		for (int i = 0; i < parametersType.length; i++) {
			parametersType[i] = method.getParameters()[i].getType();
		}

		var remoteMethod = klass.getDeclaredMethod(methodName, parametersType);

		if (Objects.isSetter(remoteMethod)) {
			String associatedField = methodName.replace("set", "");
			associatedField = associatedField.substring(0, 1).toLowerCase() + associatedField.substring(1);

			Field field = instance.getClass().getDeclaredField(associatedField);
			field.setAccessible(true);
			this.fieldUpdate.accept(new FieldData<T>(this.instance, field, field.get(this.instance), arguments[0]));
		}

		return remoteMethod.invoke(this.instance, arguments);
	}

}
