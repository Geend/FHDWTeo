package de.fhdw.ml.transactionFramework.typesAndCollections;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import de.fhdw.ml.transactionFramework.administration.ObjectAdministration;

public class Map2Object_Transactional<K,V extends Object_Transactional> implements Map<K,V>, Framework_CollectionObject {

	private static final long serialVersionUID = 1L;

	final protected Map<K,Object_TransactionalInCollectionAdapter<V>> internalMap;

	public Map2Object_Transactional() {
		this.internalMap = new HashMap<K, Object_TransactionalInCollectionAdapter<V>>();
	}
	public Map2Object_Transactional(SerializableComparator<? super K> comparator) {
		this.internalMap = new TreeMap<K, Object_TransactionalInCollectionAdapter<V>>(comparator);
	}
	@Override
	public int size() {
		this.prepareRead("size");
		return this.internalMap.size();
	}

	@Override
	public boolean isEmpty() {
		this.prepareRead("isEmpty");
		return this.internalMap.isEmpty();
	}

	@Override
	public boolean containsKey(Object key) {
		this.prepareRead("containsKey");
		return this.internalMap.containsKey(key);
	}

	@Override
	public boolean containsValue(Object value) {
		this.prepareRead("containsValue");
		return this.internalMap.containsValue(value);
	}

	@Override
	public V get(Object key) {
		this.prepareRead("get");
		Object_TransactionalInCollectionAdapter<V> result = this.internalMap.get(key);
		if (result == null) return null;
		return result.getObject();
	}

	@Override
	public V put(K key, V value) {
		this.prepareChange("put");
		Object_TransactionalInCollectionAdapter<V> result = this.internalMap.put(key, new Object_TransactionalInCollectionAdapter<V>(value));
		this.finishChange("put");
		if (result == null) return null;
		return result.getObject();
	}

	@Override
	public V remove(Object key) {
		this.prepareChange("remove");
		Object_TransactionalInCollectionAdapter<V> result = this.internalMap.remove(key);
		this.finishChange("remove");
		if (result == null) return null;
		return result.getObject();
	}

	@Override
	public void putAll(Map<? extends K, ? extends V> m) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void clear() {
		this.prepareChange("clear");
		this.internalMap.clear();
		this.finishChange("clear");
	}

	@Override
	public Set<K> keySet() {
		this.prepareRead("keySet");
		return this.internalMap.keySet();
	}

	@Override
	/**
	 * Result collection is not backed by the original map!
	 */
	public Collection<V> values() {
		Collection<V> result = new LinkedList<V>();
		for (K currentKey : this.internalMap.keySet()) {
			result.add(this.internalMap.get(currentKey).getObject());
		}
		return result;
	}

	@Override
	public Set<java.util.Map.Entry<K, V>> entrySet() {
		throw new UnsupportedOperationException();
	}
	protected void prepareRead(String methodName){
		ObjectAdministration.getCurrentAdministration().prepareMapRead(this, methodName);
	}
	protected void prepareChange(String methodName){
		ObjectAdministration.getCurrentAdministration().prepareMapWrite(this, methodName);
	}
	protected void finishChange(String methodName){
		ObjectAdministration.getCurrentAdministration().finishMapWrite(this, methodName);
	}

}
