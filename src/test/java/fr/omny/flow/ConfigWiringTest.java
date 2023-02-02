package fr.omny.flow;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.Map;

import org.junit.Test;

import fr.omny.flow.config.Config;
import fr.omny.flow.config.ConfigApplier;
import fr.omny.flow.data.DummyFileConfiguration;
import fr.omny.odi.Injector;
import lombok.Getter;

public class ConfigWiringTest {
	
	@Test
	public void wireConfig(){
		var config = new DummyFileConfiguration(Map.of("name", "Hello world"));
		Injector.startTest();
		Injector.registerWireListener(new ConfigApplier(config));

		Service service = new Service();
		assertNull(service.getName());
		Injector.wire(service);
		assertNotNull(service.getName());
		assertEquals("Hello world", service.getName());

		Injector.wipeTest();
	}

	public static class Service{

		@Getter
		@Config("name")
		private String name;
		
	}
	

}
