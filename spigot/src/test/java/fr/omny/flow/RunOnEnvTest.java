package fr.omny.flow;


import static org.junit.jupiter.api.Assertions.assertEquals;

import java.lang.reflect.Method;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junitpioneer.jupiter.ReadsEnvironmentVariable;
import org.junitpioneer.jupiter.SetEnvironmentVariable;

import fr.omny.flow.api.aop.RunOnDev;
import fr.omny.flow.api.aop.RunOnProd;
import fr.omny.flow.api.process.Env;
import fr.omny.odi.Component;
import fr.omny.odi.Injector;
import fr.omny.odi.Utils;
import fr.omny.odi.utils.Predicates;
import lombok.Getter;

public class RunOnEnvTest {

	@BeforeEach
	public void setup() {
		Injector.startTest();
	}

	@AfterEach
	public void tearDown() {
		Injector.wipeTest();
	}

	public void triggerRun(){
		Injector.findEachWithClasses(Predicates.alwaysTrue()).forEach(entry -> {
			for (Method method : entry.getKey().getDeclaredMethods()) {
				var values = entry.getValue().values();
				if (method.isAnnotationPresent(RunOnDev.class) && Env.getEnvType().equalsIgnoreCase("development")) {
					values.forEach(
							proxyInstance -> Utils.callMethodQuiet(method, proxyInstance.getClass(), proxyInstance, new Object[] {}));
				} else if (method.isAnnotationPresent(RunOnProd.class) && Env.getEnvType().equalsIgnoreCase("production")) {
					values.forEach(
							proxyInstance -> Utils.callMethodQuiet(method, proxyInstance.getClass(), proxyInstance, new Object[] {}));
				}
			}
		});
	}

	@Test
	@ReadsEnvironmentVariable
	@SetEnvironmentVariable(key = Env.ENVIRONMENT, value = "production")
	public void testProdCall() throws Exception {
		Injector.addSpecial(Service.class);
		Service service = Injector.getService(Service.class);
		triggerRun();
		String value = service.getName();
		assertEquals("PROD", value);
	}

	@Getter
	@Component
	public static class Service {

		private String name = "";

		@RunOnProd
		public void prod() {
			this.name = "PROD";
		}

		@RunOnDev
		public void dev() {
			this.name = "DEV";
		}

	}

}
