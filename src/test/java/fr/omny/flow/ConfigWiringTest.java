package fr.omny.flow;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.Optional;

import org.junit.Test;

import fr.omny.flow.config.Config;
import fr.omny.flow.config.ConfigApplier;
import fr.omny.flow.data.DummyFileConfiguration;
import fr.omny.odi.Injector;
import fr.omny.odi.Utils;
import lombok.Getter;

public class ConfigWiringTest {

	@Test
	public void wireConfig()
			throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		var config = new DummyFileConfiguration(
				Map.of("name", "Hello world", "nested.key.of.interesting.data", 69420, "im.here", "John Doe"));
		Injector.startTest();
		var configApplier = new ConfigApplier(config);
		Injector.registerWireListener(configApplier);
		Utils.registerCallConstructor(configApplier);

		Service service = Utils.callConstructor(Service.class);
		assertNotNull(service.getName());
		assertEquals("Hello world", service.getName());
		assertEquals(69420, service.getData());

		assertTrue(service.getDbName().isEmpty());

		assertTrue(service.getWorldName().isPresent());
		assertEquals("John Doe", service.getWorldName().get());

		assertEquals("Hello world", service.getConstructedConfig());

		Injector.wipeTest();
	}

	@Getter
	public static class Service {

		private String constructedConfig;

		@Config("name")
		private String name;

		@Config("nested.key.of.interesting.data")
		private int data;

		@Config("no.one")
		private Optional<String> dbName;

		@Config("im.here")
		private Optional<String> worldName;

		/**
		 * @param constructedConfig
		 */
		public Service(@Config("name") String constructedConfig) {
			this.constructedConfig = constructedConfig;
		}

	}

}
