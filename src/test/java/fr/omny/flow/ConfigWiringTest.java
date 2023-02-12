package fr.omny.flow;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Map;
import java.util.Optional;

import org.junit.Test;

import fr.omny.flow.config.Config;
import fr.omny.flow.config.ConfigApplier;
import fr.omny.flow.data.DummyFileConfiguration;
import fr.omny.odi.Injector;
import lombok.Getter;

public class ConfigWiringTest {

	@Test
	public void wireConfig() {
		var config = new DummyFileConfiguration(Map.of("name", "Hello world", "nested.key.of.interesting.data", 69420));
		Injector.startTest();
		Injector.registerWireListener(new ConfigApplier(config));

		Service service = new Service();
		assertNull(service.getName());
		Injector.wire(service);
		assertNotNull(service.getName());
		assertEquals("Hello world", service.getName());
		assertEquals(69420, service.getData());

		assertTrue(service.getDbName().isEmpty());

		Injector.wipeTest();
	}

	@Getter
	public static class Service {

		@Config("name")
		private String name;

		@Config("nested.key.of.interesting.data")
		private int data;

		@Config("no.one")
		private Optional<String> dbName;


	}

}
