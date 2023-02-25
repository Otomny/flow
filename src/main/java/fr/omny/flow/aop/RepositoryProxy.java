package fr.omny.flow.aop;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import fr.omny.flow.data.CrudRepository;

public class RepositoryProxy implements InvocationHandler {

  @SuppressWarnings("unchecked")
  public static <T, ID, C extends CrudRepository<T, ID>>
      C createRepositoryProxy(Class<? extends C> proxyClass,
                              CrudRepository<T, ID> crudRepository) {
    return (C)Proxy.newProxyInstance(
        proxyClass.getClassLoader(), new Class[] {proxyClass},
        new RepositoryProxy(proxyClass, crudRepository));
  }

  private CrudRepository<?, ?> repo;
  private Class<?> proxiedClass;

  private RepositoryProxy(Class<?> proxiedClass,
                          CrudRepository<?, ?> crudRepository) {
    this.proxiedClass = proxiedClass;
    this.repo = crudRepository;
  }

  @Override
  public Object invoke(Object proxy, Method method, Object[] arguments)
      throws Throwable {
    String methodName = method.getName();
    int argumentsCount = arguments == null ? 0 : arguments.length;

    for (Method remoteMethod : repo.getClass().getDeclaredMethods()) {
      if (remoteMethod.getParameterCount() == argumentsCount &&
          remoteMethod.getName().equals(methodName)) {
        return remoteMethod.invoke(repo, arguments);
      }
    }
    Class<?>[] parametersType = new Class<?>[ method.getParameters().length ];
    for (int i = 0; i < parametersType.length; i++) {
      parametersType[i] = method.getParameters()[i].getType();
    }
    return MethodHandles.lookup()
        .findSpecial(
            proxiedClass, method.getName(),
            MethodType.methodType(method.getReturnType(), parametersType),
            proxiedClass)
        .bindTo(proxy)
        .invokeWithArguments(arguments);
  }
}
