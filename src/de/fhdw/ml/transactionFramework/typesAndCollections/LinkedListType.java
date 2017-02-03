package de.fhdw.ml.transactionFramework.typesAndCollections;

import java.util.Collection;

public class LinkedListType<E extends Object_Transactional> implements ListType<E> {
	@Override
	public Collection<Object_TransactionalInCollectionAdapter<E>> accept(CollectionTypeVisitor<E> collectionTypeVisitor) {
		return collectionTypeVisitor.handle(this);
	}
 }