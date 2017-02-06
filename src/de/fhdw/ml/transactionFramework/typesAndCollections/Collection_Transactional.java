package de.fhdw.ml.transactionFramework.typesAndCollections;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Vector;

@SuppressWarnings("serial")
public class Collection_Transactional<E extends Object_Transactional> implements Collection<E>, Framework_CollectionObject  {

	private static final String ADD_ALLOperationName = "addAll";
	private static final String CLEAROperationName = "clear";
	private static final String RETAIN_ALLOperationName = "retainAll";
	private static final String REMOVE_ALLOperationName = "removeAll";
	private static final String CONTAINS_ALLOpwerationName = "containsAll";
	private static final String REMOVEOperationName = "remove";
	private static final String TO_ARRAY_T_OperationName = "toArray<T>";
	private static final String TO_ARRAYOperationName = "toArray";
	private static final String CONTAINSOperationName = "contains";
	private static final String IS_EMPTYOperationName = "isEmpty";
	private static final String SIZEOperationName = "size";
	private static final String ADDOperationName = "add";
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
		return Framework_CollectionObject.collectionMethod(this, ADDOperationName, ReadWrite.WRITE, 
				() -> this.getInternalCollection().add(new Object_TransactionalInCollectionAdapter<E>(e))); 
	}
	@Override
	public int size() {
		return Framework_CollectionObject.collectionMethod(this, SIZEOperationName, ReadWrite.READ, () -> this.getInternalCollection().size());
	}
	@Override
	public boolean isEmpty() {
		return Framework_CollectionObject.collectionMethod(this, IS_EMPTYOperationName, ReadWrite.READ, () -> this.getInternalCollection().isEmpty());
	}
	@Override
	public boolean contains(Object o) {		
		return Framework_CollectionObject.collectionMethod(this, CONTAINSOperationName, ReadWrite.READ, () -> this.getInternalCollection().contains(o));
	}
	@Override
	public Object[] toArray() {
		return Framework_CollectionObject.collectionMethod(this, TO_ARRAYOperationName, ReadWrite.READ, () -> this.getInternalCollection().toArray());
	}
	@SuppressWarnings("unchecked")
	@Override
	public <T>  T[] toArray(T[] a) {
		return Framework_CollectionObject.collectionMethod(this, TO_ARRAY_T_OperationName, ReadWrite.READ, () -> {
	        final T[] result = a.length < this.size() ? (T[])java.lang.reflect.Array.newInstance(a.getClass().getComponentType(), this.size()) : a; 
	        int i = 0;
			for (Object_TransactionalInCollectionAdapter<?> current : this.getInternalCollection()) {
				result[i++] = (T)current.getObject();
			}
			return result; });

	}
	@Override
	public boolean remove(Object o) {
		return Framework_CollectionObject.collectionMethod(this, REMOVEOperationName, ReadWrite.WRITE, () -> this.getInternalCollection().remove(o));
	}
	@Override
	public boolean containsAll(Collection<?> c) {
		return Framework_CollectionObject.collectionMethod(this, CONTAINS_ALLOpwerationName, ReadWrite.READ, () -> this.getInternalCollection().containsAll(c) );
	}
	@Override
	public boolean removeAll(Collection<?> c) {
		return Framework_CollectionObject.collectionMethod(this, REMOVE_ALLOperationName, ReadWrite.WRITE, () -> this.getInternalCollection().removeAll(c));
	}
	@Override
	public boolean retainAll(Collection<?> c) {
		return Framework_CollectionObject.collectionMethod(this, RETAIN_ALLOperationName, ReadWrite.WRITE, () -> this.getInternalCollection().retainAll(c));
	}
	@Override
	public void clear() {
		Framework_CollectionObject.collectionMethod(this, CLEAROperationName, ReadWrite.WRITE, () -> {this.getInternalCollection().clear(); return null;});
	}
	@Override
	public boolean addAll(Collection<? extends E> c) {
		return Framework_CollectionObject.collectionMethod(this, ADD_ALLOperationName, ReadWrite.WRITE, () -> {
			boolean result = false;
			for (E current : c) {
				result = result | this.getInternalCollection().add(new Object_TransactionalInCollectionAdapter<E>(current));
			}
			return result;			
		});
	}
}
