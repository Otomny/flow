package fr.omny.flow;


import static org.junit.jupiter.api.Assertions.assertEquals;

import java.lang.reflect.InvocationTargetException;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import fr.omny.flow.placeholders.Placeholder;
import fr.omny.flow.placeholders.Placeholders;
import fr.omny.odi.Injector;
import fr.omny.odi.Utils;

public class PlaceHolderTest {

	@BeforeEach
	public void setup() {
		Injector.startTest();
	}

	@AfterEach
	public void tearDown() {
		Injector.wipeTest();
	}

	@Test
	public void testPlaceholder()
			throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		Placeholders placeholders = Utils.callConstructor(Placeholders.class);
		placeholders.registerPlaceholder(new Placeholder("hello", p -> "Hello"));

		String text = "%hello% World";
		String result = placeholders.inject(text, null);

		assertEquals("Hello World", result);
	}

}
