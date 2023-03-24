package fr.omny.flow.api.utils.mongodb;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.function.Consumer;

import fr.omny.flow.api.utils.Objects;
import fr.omny.odi.listener.OnProxyCallListener;
import fr.omny.odi.proxy.ProxyFactory;
import reactor.util.annotation.Nullable;

@SuppressWarnings("unchecked")
public class ProxyMongoObject<T> implements OnProxyCallListener {

	public static <T> T createProxySilent(T originalInstance, Consumer<FieldData<T>> fieldUpdate) {
		try {
			return createProxy(originalInstance, fieldUpdate);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static <T> T createProxy(T originalInstance, Consumer<FieldData<T>> fieldUpdate) throws Exception {
		return (T) ProxyFactory.newProxyInstance(originalInstance.getClass(), originalInstance,
				List.of(new ProxyMongoObject<>(fieldUpdate)));
	}

	public static record FieldData<T>(T instance, Field field, @Nullable Object oldValue, Object newValue) {
	}

	private Consumer<FieldData<T>> fieldUpdate;

	public ProxyMongoObject(Consumer<FieldData<T>> fieldUpdate) {
		this.fieldUpdate = fieldUpdate;
	}

	@Override
	public boolean pass(Method method) {
		return Objects.isSetter(method);
	}

	@Override
	public Object invoke(Object instance, Method remoteMethod, Object[] arguments) throws Exception {
		String methodName = remoteMethod.getName();
		String associatedField = methodName.replace("set", "");
		associatedField = associatedField.substring(0, 1).toLowerCase() + associatedField.substring(1);

		Field field = instance.getClass().getDeclaredField(associatedField);
		field.setAccessible(true);
		this.fieldUpdate.accept(new FieldData<T>((T) instance, field, field.get(instance), arguments[0]));
		return null;
	}

}
