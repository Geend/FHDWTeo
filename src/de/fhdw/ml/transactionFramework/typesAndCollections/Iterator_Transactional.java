package de.fhdw.ml.transactionFramework.typesAndCollections;

import java.util.Iterator;

public class Iterator_Transactional<E extends Object_Transactional> implements Iterator<E> {

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
		this.getCollection().prepareReadByIterator("hasNext");
		return this.getInternalIterator().hasNext();
	}
	
	@Override
	public E next() {
		this.getCollection().prepareReadByIterator("next");
		return this.getInternalIterator().next().getObject();
	}
	
	@Override
	public void remove(){
		this.getCollection().prepareChangeByIterator("remove");
		this.getInternalIterator().remove();
		this.getCollection().finishChangeByIterator("remove");
	}
}
