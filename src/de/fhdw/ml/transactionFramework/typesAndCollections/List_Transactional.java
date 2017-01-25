package de.fhdw.ml.transactionFramework.typesAndCollections;

import java.util.Collection;
import java.util.List;
import java.util.ListIterator;

@SuppressWarnings("serial")
public class List_Transactional<E extends Object_Transactional> extends Collection_Transactional<E> implements List<E>{

	public List_Transactional(ListType<E> listType) {
		super(listType);
	}
	protected List<Object_TransactionalInCollectionAdapter<E>> getInternalCollection(){
		return (List<Object_TransactionalInCollectionAdapter<E>>) this.internalCollection;
	}

	@Override
	public boolean addAll(int index, Collection<? extends E> c) {
		this.prepareChange("addAllAtIndex");
		int i = index;
		for (E current : c) 
			this.getInternalCollection().add(i++,new Object_TransactionalInCollectionAdapter<E>(current));
		if (!c.isEmpty()) this.finishChange("addAllAtIndex");
		return !c.isEmpty();
	}
	@Override
	public E get(int index) {
		this.prepareRead("getAtIndex");
		return this.getInternalCollection().get(index).getObject();
	}
	@Override
	public E set(int index, E element) {
		this.prepareChange("setAtIndex");
		Object_TransactionalInCollectionAdapter<E> result = this.getInternalCollection().set(index, new Object_TransactionalInCollectionAdapter<E>(element));
		this.finishChange("setAtIndex");
		if (result == null) return null;
		return result.getObject();
	}
	@Override
	public void add(int index, E element) {
		this.prepareChange("addAtIndex");
		this.getInternalCollection().add(index, new Object_TransactionalInCollectionAdapter<E>(element));
		this.finishChange("addAtIndex");
	}
	@Override
	public E remove(int index) {
		this.prepareChange("removeAtIndex");
		Object_TransactionalInCollectionAdapter<E> replaced = this.getInternalCollection().remove(index);
		this.finishChange("removeAtIndex");
		if (replaced == null) return null;
		return replaced.getObject();
	}
	@Override
	public int indexOf(Object o) {
		this.prepareRead("indexOf");
		return this.getInternalCollection().indexOf(new Object_TransactionalInCollectionAdapter<Object_Transactional>((Object_Transactional) o));
	}
	@Override
	public int lastIndexOf(Object o) {
		this.prepareRead("lastIndexOf");
		return this.getInternalCollection().lastIndexOf(new Object_TransactionalInCollectionAdapter<Object_Transactional>((Object_Transactional) o));
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
