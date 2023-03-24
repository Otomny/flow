package fr.omny.flow.api.aop;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.jupiter.api.Test;

import fr.omny.flow.api.utils.mongodb.ProxyMongoObject;

public class ProxyMongoObjectTest {

	@Test
	public void test_Proxy_Setter() throws Exception {
		DummyObject originalObject = new DummyObject();
		AtomicReference<String> worldReference = new AtomicReference<>();
		AtomicReference<String> oldWorldReference = new AtomicReference<>();

		worldReference.set(originalObject.getWorld());
		oldWorldReference.set(originalObject.getWorld());

		DummyObject proxiedObject = ProxyMongoObject.createProxy(originalObject, (fieldData) -> {
			worldReference.set((String) fieldData.newValue());
			oldWorldReference.set((String) fieldData.oldValue());
		});
		assertEquals("Hello world!", worldReference.get());
		assertEquals("Hello world!", oldWorldReference.get());

		proxiedObject.setWorld("No hello world :(");

		assertEquals("No hello world :(", worldReference.get());
		assertEquals("Hello world!", oldWorldReference.get());
	}

	@Test
	public void test_selfInvoke_Setter() throws Exception {
		DummyObject originalObject = new DummyObject();

		AtomicBoolean hasBeenChanged = new AtomicBoolean(false);

		originalObject.setWorld("Man I Can't");
		assertEquals("Man I Can't", originalObject.getWorld());
		DummyObject proxiedObject = ProxyMongoObject.createProxy(originalObject, (fieldData) -> {
			hasBeenChanged.set(true);
		});
		proxiedObject.selfInvoke("Hello world");
		assertEquals("Hello world", proxiedObject.getWorld());
		assertFalse(hasBeenChanged.get());
	}

	@Test
	public void test_MultipleInstance() throws Exception {
		DummyObject d1 = new DummyObject();
		d1.setWorld("world 1");
		DummyObject d2 = new DummyObject();
		d2.setWorld("world 2");

		assertEquals("world 1", d1.getWorld());
		assertEquals("world 2", d2.getWorld());

		DummyObject proxied1 = ProxyMongoObject.createProxy(d1, (fieldData) -> {
		});
		DummyObject proxied2 = ProxyMongoObject.createProxy(d2, (fieldData) -> {
		});

		assertEquals("world 1", proxied1.getWorld());
		assertEquals("world 2", proxied2.getWorld());
	}

}
