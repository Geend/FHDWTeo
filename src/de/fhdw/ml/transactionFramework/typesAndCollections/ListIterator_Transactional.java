package de.fhdw.ml.transactionFramework.typesAndCollections;

import java.util.ListIterator;

public class ListIterator_Transactional<E extends Object_Transactional> extends Iterator_Transactional<E> implements ListIterator<E>{

	private static final String ADD_BY_ITERATOROperationName = "add";
	private static final String SETOperationName = "set";
	private static final String PREVIOUS_INDEXOperationName = "previousIndex";
	private static final String NEXT_INDEXOperationName = "nextIndex";
	private static final String PREVIOUSOperationName = "previous";
	private static final String HAS_PREVIOUSOperationName = "hasPrevious";

	public ListIterator_Transactional(List_Transactional<E> list) {
		super(list);
	}
	protected ListIterator<Object_TransactionalInCollectionAdapter<E>> getInternalIterator(){
		if (this.internalIterator == null) this.internalIterator = this.getCollection().getInternalCollection().listIterator();
		return (ListIterator<Object_TransactionalInCollectionAdapter<E>>)this.internalIterator;
	}

	protected List_Transactional<E> getCollection() {
		return (List_Transactional<E>)this.collection;
	}
	
	@Override
	public boolean hasPrevious() {
		return Framework_CollectionObject.collectionMethod(this.collection, HAS_PREVIOUSOperationName, ReadWrite.READ, 
				() -> this.getInternalIterator().hasPrevious());
	}

	@Override
	public E previous() {
		return Framework_CollectionObject.collectionMethod(this.collection, PREVIOUSOperationName, ReadWrite.READ, 
				() -> this.getInternalIterator().previous().getObject());
	}

	@Override
	public int nextIndex() {
		return Framework_CollectionObject.collectionMethod(this.collection, NEXT_INDEXOperationName, ReadWrite.READ, 
				() -> this.getInternalIterator().nextIndex());
	}

	@Override
	public int previousIndex() {
		return Framework_CollectionObject.collectionMethod(this.collection, PREVIOUS_INDEXOperationName, ReadWrite.READ, 
				() -> this.getInternalIterator().previousIndex());
	}

	@Override
	public void set(E e) {
		Framework_CollectionObject.collectionMethod(this.collection, SETOperationName, ReadWrite.WRITE, 
				() -> {this.getInternalIterator().set(new Object_TransactionalInCollectionAdapter<E>(e)); return null;});
	}

	@Override
	public void add(E e) {
		Framework_CollectionObject.collectionMethod(this.collection, ADD_BY_ITERATOROperationName, ReadWrite.WRITE, 
				() -> {this.getInternalIterator().add(new Object_TransactionalInCollectionAdapter<E>(e)); return null;});		
	}

}
