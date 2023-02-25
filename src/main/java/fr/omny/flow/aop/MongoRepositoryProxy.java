package fr.omny.flow.aop;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

import org.bson.conversions.Bson;

import fr.omny.flow.data.CrudRepository;
import fr.omny.flow.data.implementation.MongoDBRepository;
import fr.omny.flow.data.mongodb.MongoQuery;
import fr.omny.odi.Utils;

public class MongoRepositoryProxy implements InvocationHandler {

  @SuppressWarnings("unchecked")
  public static <T, ID, C extends CrudRepository<T, ID>>
      C createRepositoryProxy(Class<? extends C> proxyClass,
                              Class<? extends T> dataClass,
                              CrudRepository<T, ID> crudRepository) {
    return (C)Proxy.newProxyInstance(
        proxyClass.getClassLoader(), new Class[] {proxyClass},
        new MongoRepositoryProxy(proxyClass, dataClass, crudRepository));
  }

  private CrudRepository<?, ?> repo;
  private Class<?> proxiedClass;
  private Map<Method, Method> mappedMethod;
  private Method executeQuery;

  public MongoRepositoryProxy(Class<?> proxiedClass, Class<?> dataClass,
                              CrudRepository<?, ?> crudRepository) {
    this.proxiedClass = proxiedClass;
    this.repo = crudRepository;
    this.mappedMethod = new HashMap<>();
    this.executeQuery =
        Utils.findByName(MongoDBRepository.class, "executeQuery");
  }

  private Bson createProjection(Method method, MongoQuery query,
                                Object[] arguments) {
    throw new UnsupportedOperationException();
  }

  private Bson createFilter(Method method, MongoQuery query,
                            Object[] arguments) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Object invoke(Object proxy, Method method, Object[] arguments)
      throws Throwable {
    String methodName = method.getName();
    int argumentsCount = arguments == null ? 0 : arguments.length;

    if (method.getName().equals(this.executeQuery.getName())) {
      return this.executeQuery.invoke(this.repo, arguments);
    }
    if (method.isAnnotationPresent(MongoQuery.class)) {
      var queryData = method.getAnnotation(MongoQuery.class);
      var filter = createFilter(method, queryData, arguments);
      var projection = createProjection(method, queryData, arguments);

      return this.executeQuery.invoke(repo, filter, projection);
    }
    if (this.mappedMethod.containsKey(method)) {
      return this.mappedMethod.get(method).invoke(repo, arguments);
    }

    for (Method remoteMethod : repo.getClass().getDeclaredMethods()) {
      if (remoteMethod.getParameterCount() == argumentsCount &&
          remoteMethod.getName().equals(methodName)) {
        this.mappedMethod.put(method, remoteMethod);
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
