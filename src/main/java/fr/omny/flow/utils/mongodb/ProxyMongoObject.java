package fr.omny.flow.utils.mongodb;


import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import javax.annotation.Nullable;

import fr.omny.flow.aop.GenericProxyFactory;
import fr.omny.flow.utils.Objects;

@SuppressWarnings("unchecked")
public class ProxyMongoObject<T> implements InvocationHandler {

	public static <T> T createProxySilent(T originalInstance, Consumer<FieldData<T>> fieldUpdate) {
		try {
			return (T) GenericProxyFactory.newProxyInstance(originalInstance.getClass(),
					new ProxyMongoObject<T>(originalInstance, fieldUpdate));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static <T> T createProxy(T originalInstance, Consumer<FieldData<T>> fieldUpdate) throws Exception {
		return (T) GenericProxyFactory.newProxyInstance(originalInstance.getClass(),
				new ProxyMongoObject<T>(originalInstance, fieldUpdate));
	}

	public static record FieldData<T>(T instance, Field field, @Nullable Object oldValue, Object newValue) {}

	private T instance;
	private Class<? extends T> klass;
	private Consumer<FieldData<T>> fieldUpdate;
	private List<Field> fields = new ArrayList<>();

	private ProxyMongoObject(T originalInstance, Consumer<FieldData<T>> fieldUpdate) {
		this.instance = originalInstance;
		this.fieldUpdate = fieldUpdate;
		this.klass = (Class<? extends T>) this.instance.getClass();
		for (Field field : this.klass.getDeclaredFields()) {
			field.setAccessible(true);
			fields.add(field);
		}
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
		if (Objects.isSetter(method) || Objects.isGetter(method)) {
			return remoteMethod.invoke(this.instance, arguments);
		} else {
			int oldHashCodeGlobal = java.util.Objects.hashCode(this.instance);
			Map<Field, Integer> hashCodes = new HashMap<>();
			this.fields.forEach(f -> {
				try {
					hashCodes.put(f, java.util.Objects.hashCode(f.get(this.instance)));
				} catch (IllegalArgumentException | IllegalAccessException e) {
					e.printStackTrace();
				}
			});
			Object result = remoteMethod.invoke(this.instance, arguments);
			int newHashCodeGlobal = java.util.Objects.hashCode(this.instance);
			if(oldHashCodeGlobal != newHashCodeGlobal){
				for (Field field : this.fields) {
					var currentValue = field.get(this.instance);
					int oldHashCode = hashCodes.get(field);
					int currentHashCode = java.util.Objects.hashCode(currentValue);
					if (oldHashCode != currentHashCode) {
						this.fieldUpdate.accept(new FieldData<T>(this.instance, field, null, currentValue));
					}
				}
			}
			
			return result;
		}
	}

}
