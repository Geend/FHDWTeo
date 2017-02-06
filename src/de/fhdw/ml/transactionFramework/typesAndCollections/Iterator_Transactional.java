package de.fhdw.ml.transactionFramework.typesAndCollections;

import java.util.Iterator;

public class Iterator_Transactional<E extends Object_Transactional> implements Iterator<E> {

	private static final String REMOVE_BY_ITERATOROperationName = "remove";
	private static final String NEXTOperationName = "next";
	private static final String HAS_NEXTOperationName = "hasNext";
	final protected Collection_Transactional<E> collection;
	protected Iterator<Object_TransactionalInCollectionAdapter<E>> internalIterator;

	
	public Iterator_Transactional(Collection_Transactional<E> collection) {
		this.collection = collection;
	}

	protected Collection_Transactional<E> getCollection() {
		return this.collection;
	}

	protected Iterator<Object_TransactionalInCollectionAdapter<E>> getInternalIterator(){
		if (this.internalIterator == null) this.internalIterator = this.collection.getInternalCollection().iterator();
		return this.internalIterator;
	}
	
	@Override
	public boolean hasNext() {
		return Framework_CollectionObject.collectionMethod(this.getCollection(), HAS_NEXTOperationName, ReadWrite.READ, () -> this.getInternalIterator().hasNext()); 
	}
	
	@Override
	public E next() {
		return Framework_CollectionObject.collectionMethod(this.getCollection(), NEXTOperationName, ReadWrite.READ, () -> this.getInternalIterator().next().getObject());
	}
	
	@Override
	public void remove(){
		Framework_CollectionObject.collectionMethod(this.getCollection(), REMOVE_BY_ITERATOROperationName, ReadWrite.WRITE, () -> {this.getInternalIterator().remove(); return null;});
	}
}
