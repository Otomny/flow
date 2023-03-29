package fr.omny.flow.api.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class DoublyLinkedListTest {
	
	@Test
	public void onTest(){
		DoublyLinkedList<String> list = new DoublyLinkedList<>();
		list.add("Test");
		list.addHead("Test2");
		assertEquals("Test2", list.getHead().getValue());
		assertEquals("Test", list.getHead().getNext().getValue());

		var pr = list.getHead().getNext();
		list.detach(pr);
		assertTrue(!list.getHead().hasNext());

	}

}
