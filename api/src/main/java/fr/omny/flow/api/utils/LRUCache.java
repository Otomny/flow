package fr.omny.flow.api.utils;


import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import fr.omny.flow.api.utils.DoublyLinkedList.Node;
import lombok.Getter;
import lombok.Setter;

@Getter
public class LRUCache<K, V> implements Cache<K, V> {

	@Setter
	private int maxSize;
	private Map<K, Node<CacheElement<K, V>>> nodeMap;
	private DoublyLinkedList<CacheElement<K, V>> refMap;

	public LRUCache(int maxSize) {
		this.maxSize = maxSize;
		this.nodeMap = new HashMap<>();
		this.refMap = new DoublyLinkedList<>();
	}

	public static final class CacheElement<K, V> implements Map.Entry<K, V> {

		public static final CacheElement<?, ?> EMPTY = new CacheElement<>(null, null);

		@SuppressWarnings("unchecked")
		public static <K, V> CacheElement<K, V> empty() {
			return (CacheElement<K, V>) EMPTY;
		}

		private K key;
		private V value;

		/**
		 * @param key
		 * @param value
		 */
		public CacheElement(K key, V value) {
			this.key = key;
			this.value = value;
		}

		@Override
		public K getKey() {
			return this.key;
		}

		@Override
		public V getValue() {
			return this.value;
		}

		@Override
		public V setValue(V arg0) {
			return this.value = arg0;
		}

	}

	private void evictTail() {
		if (this.refMap.size() > 0) {
			this.refMap.popWithRef().ifPresent(nodeRef -> nodeMap.remove(nodeRef.getValue().getKey()));
		}
	}

	@Override
	public boolean set(K key, V value) {
		CacheElement<K, V> item = new CacheElement<K, V>(key, value);
		Node<CacheElement<K, V>> newHead;
		if (this.nodeMap.containsKey(key)) {
			// Map contains, we need to refresh order
			// To do that, bring ref to head
			var oldRef = this.nodeMap.get(key);
			this.refMap.detach(oldRef);
			this.refMap.insertHead(oldRef);
			newHead = oldRef;
		} else {
			if (this.size() >= this.maxSize) {
				this.evictTail();
			}
			this.refMap.addHead(item);
			newHead = this.refMap.getHead();
		}
		if (newHead == null) {
			return false;
		}
		this.nodeMap.put(key, newHead);
		return true;
	}

	@Override
	public Optional<V> get(K key) {
		if (!this.nodeMap.containsKey(key)) {
			return Optional.empty();
		}
		var node = this.nodeMap.get(key);
		if (node != null) {
			var oldRef = this.nodeMap.get(key);
			this.refMap.detach(oldRef);
			this.refMap.insertHead(oldRef);
			this.nodeMap.put(key, oldRef);
			return Optional.of(this.nodeMap.get(key).getValue().getValue());
		}
		return Optional.empty();
	}

	@Override
	public int size() {
		return this.nodeMap.size();
	}

	@Override
	public boolean isEmpty() {
		return size() != 0;
	}

	@Override
	public void clear() {
		this.nodeMap.clear();
		this.refMap.clear();
	}

}
