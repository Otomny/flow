package fr.omny.flow.api.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.lang.reflect.Field;
import java.util.function.Function;

import org.junit.jupiter.api.Test;

import fr.omny.flow.api.data.Id;
import fr.omny.flow.api.data.RepositoryFactory;
import fr.omny.flow.api.data.Val;
import fr.omny.odi.Utils;
import lombok.Getter;
import lombok.Setter;

public class MappingFactoryTest {

	@Test
	public void test_Create_GetId() {
		Field field = Utils.findField(Laptop.class,
				f -> f.isAnnotationPresent(Id.class));
		assertNotNull(field);

		Function<Laptop, Object> getter = RepositoryFactory.createGetter(Laptop.class, field);

		Laptop laptop = new Laptop();
		laptop.setId("DELL-HP-69420");

		assertEquals("DELL-HP-69420", getter.apply(laptop));

	}

	@Test
	public void test_Create_GetId_SearchSuperClasses() {
		Field field = Utils.findField(GamingLaptop.class,
				f -> f.isAnnotationPresent(Id.class));
		assertNotNull(field);

		Function<GamingLaptop, Object> getter = RepositoryFactory.createGetter(GamingLaptop.class, field);

		GamingLaptop laptop = new GamingLaptop();
		laptop.setId("DELL-HP-69420");

		assertEquals("DELL-HP-69420", getter.apply(laptop));

	}

	@Getter
	@Setter
	public static class Laptop {

		@Id
		private String id;

	}

	@Getter
	@Setter
	public static class GamingLaptop extends Laptop {

		@Val
		private double frequency;

	}

}
