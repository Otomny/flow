package fr.omny.flow.aop;

import static org.junit.Assert.assertEquals;

import java.util.concurrent.atomic.AtomicReference;

import org.junit.Test;

import fr.omny.flow.utils.mongodb.ProxyMongoObject;

public class ProxyMongoObjectTest {
	
	@Test
	public void test_Proxy_Setter() throws Exception{
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

	

}
