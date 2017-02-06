package de.fhdw.ml.transactionFramework.typesAndCollections;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class Map2Object_Transactional<K,V extends Object_Transactional> implements Map<K,V>, Framework_CollectionObject {

	private static final long serialVersionUID = 1L;

	private static final String KEY_SETOperationName = "keySet";
	private static final String CLEAROperationName = "clear";
	private static final String REMOVEOperationName = "remove";
	private static final String PUTOperationName = "put";
	private static final String GETOperationName = "get";
	private static final String CONTAINS_VALUEOperationName = "containsValue";
	private static final String CONTAINS_KEYOperationName = "containsKey";
	private static final String IS_EMPTYOperationName = "isEmpty";
	private static final String SIZEOperationName = "size";
	private static final String VALUESOperationName = "values";

	final protected Map<K,Object_TransactionalInCollectionAdapter<V>> internalMap;

	public Map2Object_Transactional() {
		this.internalMap = new HashMap<K, Object_TransactionalInCollectionAdapter<V>>();
	}
	public Map2Object_Transactional(SerializableComparator<? super K> comparator) {
		this.internalMap = new TreeMap<K, Object_TransactionalInCollectionAdapter<V>>(comparator);
	}
	@Override
	public int size() {
		return Framework_CollectionObject.collectionMethod(this, SIZEOperationName, ReadWrite.READ, 
				() -> this.internalMap.size());
	}
	@Override
	public boolean isEmpty() {
		return Framework_CollectionObject.collectionMethod(this, IS_EMPTYOperationName, ReadWrite.READ, 
				() -> this.internalMap.isEmpty());
	}

	@Override
	public boolean containsKey(Object key) {
		return Framework_CollectionObject.collectionMethod(this, CONTAINS_KEYOperationName, ReadWrite.READ, 
				() -> this.internalMap.containsKey(key));
	}

	@Override
	public boolean containsValue(Object value) {
		return Framework_CollectionObject.collectionMethod(this, CONTAINS_VALUEOperationName, ReadWrite.READ, 
				() -> this.internalMap.containsValue(value));
	}

	@Override
	public V get(Object key) {
		return Framework_CollectionObject.collectionMethod(this, GETOperationName, ReadWrite.READ, 
				() -> {
					Object_TransactionalInCollectionAdapter<V> result = this.internalMap.get(key);
					if (result == null) return null;
					return result.getObject();
				});
	}

	@Override
	public V put(K key, V value) {
		return Framework_CollectionObject.collectionMethod(this, PUTOperationName, ReadWrite.WRITE, 
				() -> {
					Object_TransactionalInCollectionAdapter<V> result = this.internalMap.put(key, new Object_TransactionalInCollectionAdapter<V>(value));
					if (result == null) return null;
					return result.getObject();
				});
	}

	@Override
	public V remove(Object key) {
		return Framework_CollectionObject.collectionMethod(this, REMOVEOperationName, ReadWrite.READ, 
				() -> {
					Object_TransactionalInCollectionAdapter<V> result = this.internalMap.remove(key);
					if (result == null) return null;
					return result.getObject();
				});
	}

	@Override
	public void putAll(Map<? extends K, ? extends V> m) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void clear() {
		Framework_CollectionObject.collectionMethod(this, CLEAROperationName, ReadWrite.WRITE, 
				() -> {this.internalMap.clear(); return null;});
	}

	@Override
	public Set<K> keySet() {
		return Framework_CollectionObject.collectionMethod(this, KEY_SETOperationName, ReadWrite.READ, 
				() -> this.internalMap.keySet());
	}

	@Override
	/**
	 * Result collection is not backed by the original map!
	 */
	public Collection<V> values() {
		return Framework_CollectionObject.collectionMethod(this, VALUESOperationName, ReadWrite.READ,
				() -> 	{Collection<V> result = new LinkedList<V>();
						for (K currentKey : this.internalMap.keySet()) {
							result.add(this.internalMap.get(currentKey).getObject());
						}
						return result;});
	}

	@Override
	public Set<java.util.Map.Entry<K, V>> entrySet() {
		throw new UnsupportedOperationException();
	}

}
