package fr.omny.flow.aop;



import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import org.bson.Document;
import org.junit.jupiter.api.Test;

import fr.omny.flow.utils.mongodb.MongoSerializer;
import fr.omny.flow.utils.mongodb.ProxyMongoObject;

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
	public void test_Proxy_ToDocument() throws Exception {
		DummyObject originalObject = new DummyObject();
		originalObject.setWorld("Man I Can't");
		assertEquals("Man I Can't", originalObject.getWorld());
		DummyObject proxiedObject = ProxyMongoObject.createProxy(originalObject, (fieldData) -> {
		});
		Document documentObj = MongoSerializer.transform(proxiedObject, DummyObject.class);
		assertEquals("Man I Can't", documentObj.getString("world"));
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
		assertTrue(hasBeenChanged.get());
	}

}
