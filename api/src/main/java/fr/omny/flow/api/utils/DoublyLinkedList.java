package fr.omny.flow.api.utils;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DoublyLinkedList<E> {

	@Getter
	@Setter
	public static class Node<E> {

		private Node<E> next;
		private Node<E> previous;
		private E value;

		/**
		 * 
		 */
		public Node(E value) {
			this.value = value;
		}

		/**
		 * @param next
		 * @param previous
		 */
		public Node(E value, Node<E> next, Node<E> previous) {
			this(value);
			this.next = next;
			this.previous = previous;
		}

		public Node<E> previous() {
			return previous;
		}

		public boolean hasPrevious() {
			return previous != null;
		}

		public Node<E> next() {
			return next;
		}

		public boolean hasNext() {
			return next != null;
		}

		public void detach() {
			this.previous.setNext(this.getNext());
			this.next.setPrevious(this.getPrevious());
		}

	}

	@Getter
	private Node<E> head;
	@Getter
	private Node<E> tail;
	private AtomicInteger size = new AtomicInteger(0);

	public DoublyLinkedList() {
		this.head = null;
		this.tail = null;
	}

	public synchronized void add(E value) {
		insertTail(new Node<E>(value));
	}

	public synchronized void addHead(E value) {
		insertHead(new Node<E>(value));
	}

	public synchronized void insertHead(Node<E> node) {
		if (this.head == null) {
			this.head = node;
			if (this.tail == null) {
				this.tail = node;
			}
		} else {
			this.head.setPrevious(node);
			node.setNext(this.head);
			this.head = node;
		}
		size.incrementAndGet();
	}

	public synchronized void insertTail(Node<E> node) {
		if (this.tail == null) {
			this.tail = node;
			if (this.head == null) {
				this.head = node;
			}
		} else {
			this.tail.setNext(node);
			node.setPrevious(this.tail);
			this.tail = node;
		}
		size.incrementAndGet();
	}

	/**
	 * Pop tail
	 */
	public synchronized void pop() {
		popWithRef();
	}

	/**
	 * Pop tail
	 */
	public synchronized Optional<Node<E>> popWithRef() {
		if (this.tail == null) {
			return Optional.empty();
		}
		var evict = this.tail;
		if (evict.hasPrevious() && this.tail != this.head) {
			evict.getPrevious().setNext(null);
			this.tail = evict.getPrevious();
		} else {
			this.head = null;
			this.tail = null;
		}
		return Optional.of(evict);
	}

	/**
	 * Pop tail and return value
	 * 
	 * @return Value or null if
	 */
	public synchronized Optional<E> popWithValue() {
		return popWithRef().map(e -> e.value);
	}

	public int size() {
		return size.getAcquire();
	}

	public synchronized void detach(Node<E> node) {
		if (node != tail) {
			node.detach();
			if (node == head) {
				head = head.getNext();
			}
			size.decrementAndGet();
		} else {
			pop();
		}
	}

	public synchronized void clear() {
		this.head = null;
		this.tail = null;
	}
}
