package de.fhdw.ml.transactionFramework.typesAndCollections;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Vector;

import de.fhdw.ml.transactionFramework.administration.ObjectAdministration;

@SuppressWarnings("serial")
public class Collection_Transactional<E extends Object_Transactional>  implements Collection<E>, Framework_CollectionObject {

	final protected Collection<Object_TransactionalInCollectionAdapter<E>> internalCollection;

	public Collection_Transactional(CollectionType<E> collectionType){
		this.internalCollection = collectionType.accept(new CollectionTypeVisitor<E>(){
			@Override
			public Collection<Object_TransactionalInCollectionAdapter<E>> handle(VectorType<E> type) {
				return new Vector<Object_TransactionalInCollectionAdapter<E>>();
			}
			@Override
			public Collection<Object_TransactionalInCollectionAdapter<E>> handle(LinkedListType<E> type) {
				return new LinkedList<Object_TransactionalInCollectionAdapter<E>>();
			}});
	}
	protected Collection<Object_TransactionalInCollectionAdapter<E>> getInternalCollection(){
		return this.internalCollection;
	}
	@Override
	public Iterator_Transactional<E> iterator() {
		return new Iterator_Transactional<E>(this);
	}
	@Override
	public boolean add(E e) {
		this.prepareChange("add");
		boolean result = this.getInternalCollection().add(new Object_TransactionalInCollectionAdapter<E>(e));
		this.finishChange("add");		
		return result;
	}
	@Override
	public int size() {
		this.prepareRead("size");
		return this.getInternalCollection().size();
	}
	@Override
	public boolean isEmpty() {
		this.prepareRead("isEmpty");
		return this.getInternalCollection().isEmpty();
	}
	@Override
	public boolean contains(Object o) {		
		this.prepareRead("contains");
		return this.getInternalCollection().contains(o);
	}
	@Override
	public Object[] toArray() {
		this.prepareRead("toArray");
		return this.getInternalCollection().toArray();
	}
	@SuppressWarnings("unchecked")
	@Override
	public <T>  T[] toArray(T[] a) {
		this.prepareRead("toArray<T>");
        if (a.length < this.size()) a = (T[])java.lang.reflect.Array.newInstance(a.getClass().getComponentType(), this.size());
        int i = 0;
		for (Object_TransactionalInCollectionAdapter<?> current : this.getInternalCollection()) {
			a[i++] = (T)current.getObject();
		}
		return a;
	}
	@Override
	public boolean remove(Object o) {
		this.prepareChange("remove");
		boolean result = this.getInternalCollection().remove(o);
		if (result) this.finishChange("remove");
		return result;
	}
	@Override
	public boolean containsAll(Collection<?> c) {
		this.prepareRead("containsAll");
		return this.getInternalCollection().containsAll(c);
	}
	@Override
	public boolean removeAll(Collection<?> c) {
		this.prepareChange("removeAll");
		boolean result = this.getInternalCollection().removeAll(c);
		if (result) this.finishChange("removeAll");
		return result;
	}
	@Override
	public boolean retainAll(Collection<?> c) {
		this.prepareChange("retainAll");
		boolean result = this.getInternalCollection().retainAll(c);
		if (result) this.finishChange("retainAll");
		return result;
	}
	@Override
	public void clear() {
		this.prepareChange("clear");
		this.getInternalCollection().clear();		
		this.finishChange("clear");
	}
	@Override
	public boolean addAll(Collection<? extends E> c) {
		this.prepareChange("addAll");
		boolean result = false;
		for (E current : c) {
			result = result | this.getInternalCollection().add(new Object_TransactionalInCollectionAdapter<E>(current));
		}
		if (result) this.finishChange("addAll");
		return result;
	}
	protected void prepareRead(String methodName){
		ObjectAdministration.getCurrentAdministration().prepareCollectionRead(this, methodName);
	}
	protected void prepareChange(String methodName){
		ObjectAdministration.getCurrentAdministration().prepareCollectionWrite(this, methodName);
	}
	protected void finishChange(String methodName){
		ObjectAdministration.getCurrentAdministration().finishCollectionWrite(this, methodName);
	}
	public void prepareReadByIterator(String methodName) {
		this.prepareRead(methodName);
	}
	public void prepareChangeByIterator(String methodName) {
		this.prepareChange(methodName);
	}
	public void finishChangeByIterator(String methodName) {
		this.finishChange(methodName);
	}
}
