package fr.omny.flow.utils;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class BooleanValueTest {
	
	@Test
	public void testBooleanValue(){
		assertFalse(Objects.asBoolean(null));
		assertTrue(Objects.asBoolean(true));
		assertFalse(Objects.asBoolean(false));
		assertTrue(Objects.asBoolean(new Object()));
	}

}
