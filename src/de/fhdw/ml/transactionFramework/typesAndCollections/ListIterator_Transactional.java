package de.fhdw.ml.transactionFramework.typesAndCollections;

import java.util.ListIterator;

public class ListIterator_Transactional<E extends Object_Transactional> extends Iterator_Transactional<E> implements ListIterator<E>{

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
		this.getCollection().prepareReadByIterator("hasPrevious");
		return this.getInternalIterator().hasPrevious();
	}

	@Override
	public E previous() {
		this.getCollection().prepareReadByIterator("previous");
		return this.getInternalIterator().previous().getObject();
	}

	@Override
	public int nextIndex() {
		this.getCollection().prepareReadByIterator("nextIndex");
		return this.getInternalIterator().nextIndex();
	}

	@Override
	public int previousIndex() {
		this.getCollection().prepareReadByIterator("previousIndex");
		return this.getInternalIterator().previousIndex();
	}

	@Override
	public void set(E e) {
		this.getCollection().prepareChangeByIterator("set");
		this.getInternalIterator().set(new Object_TransactionalInCollectionAdapter<E>(e));
		this.getCollection().finishChangeByIterator("set");
	}

	@Override
	public void add(E e) {
		this.getCollection().prepareChangeByIterator("add");
		this.getInternalIterator().add(new Object_TransactionalInCollectionAdapter<E>(e));
		this.getCollection().finishChangeByIterator("add");
	}

}
