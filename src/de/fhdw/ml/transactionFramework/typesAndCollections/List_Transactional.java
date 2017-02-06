package de.fhdw.ml.transactionFramework.typesAndCollections;

import java.util.Collection;
import java.util.List;
import java.util.ListIterator;

@SuppressWarnings("serial")
public class List_Transactional<E extends Object_Transactional> extends Collection_Transactional<E> implements List<E>{

	private static final String LAST_INDEX_OFOperationName = "lastIndexOf";
	private static final String INDEX_OFOperationName = "indexOf";
	private static final String REMOVE_AT_INDEXOperationName = "removeAtIndex";
	private static final String ADD_AT_INDEXOperationName = "addAtIndex";
	private static final String SET_AT_INDEXoperationName = "setAtIndex";
	private static final String GET_AT_INDEXOperationName = "getAtIndex";
	private static final String ADD_ALL_AT_INDEXOperationName = "addAllAtIndex";
	public List_Transactional(ListType<E> listType) {
		super(listType);
	}
	protected List<Object_TransactionalInCollectionAdapter<E>> getInternalCollection(){
		return (List<Object_TransactionalInCollectionAdapter<E>>) this.internalCollection;
	}

	@Override
	public boolean addAll(int index, Collection<? extends E> c) {
		return Framework_CollectionObject.collectionMethod(this, ADD_ALL_AT_INDEXOperationName, ReadWrite.WRITE, () -> {
			int i = index;
			for (E current : c) 
				this.getInternalCollection().add(i++,new Object_TransactionalInCollectionAdapter<E>(current));
			return !c.isEmpty();
		});
	}
	@Override
	public E get(int index) {
		return Framework_CollectionObject.collectionMethod(this, GET_AT_INDEXOperationName, ReadWrite.READ, 
				() -> this.getInternalCollection().get(index).getObject());
	}
	@Override
	public E set(int index, E element) {
		return Framework_CollectionObject.collectionMethod(this, SET_AT_INDEXoperationName, ReadWrite.WRITE, () -> {
			Object_TransactionalInCollectionAdapter<E> result = this.getInternalCollection().set(index, new Object_TransactionalInCollectionAdapter<E>(element));
			if (result == null) return null;
			return result.getObject();
		});
	}
	@Override
	public void add(int index, E element) {
		Framework_CollectionObject.collectionMethod(this, ADD_AT_INDEXOperationName, ReadWrite.WRITE, 
			() -> {this.getInternalCollection().add(index, new Object_TransactionalInCollectionAdapter<E>(element)); return null;});
	}
	@Override
	public E remove(int index) {
		return Framework_CollectionObject.collectionMethod(this, REMOVE_AT_INDEXOperationName, ReadWrite.WRITE, () -> {
			Object_TransactionalInCollectionAdapter<E> replaced = this.getInternalCollection().remove(index);
			if (replaced == null) return null;
			return replaced.getObject();
		});

	}
	@Override
	public int indexOf(Object o) {
		return Framework_CollectionObject.collectionMethod(this, INDEX_OFOperationName, ReadWrite.READ, 
				() -> this.getInternalCollection().indexOf(new Object_TransactionalInCollectionAdapter<Object_Transactional>((Object_Transactional) o)));
	}
	@Override
	public int lastIndexOf(Object o) {
		return Framework_CollectionObject.collectionMethod(this, LAST_INDEX_OFOperationName, ReadWrite.READ, 
				() -> this.getInternalCollection().lastIndexOf(new Object_TransactionalInCollectionAdapter<Object_Transactional>((Object_Transactional) o)));
	}
	@Override
	public ListIterator<E> listIterator() {
		return new ListIterator_Transactional<E>(this);
	}
	@Override
	public ListIterator<E> listIterator(int index) {
		throw new UnsupportedOperationException();
	}
	@Override
	public List<E> subList(int fromIndex, int toIndex) {	
		throw new UnsupportedOperationException();
	}

}
