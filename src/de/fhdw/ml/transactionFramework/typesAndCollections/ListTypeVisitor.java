package de.fhdw.ml.transactionFramework.typesAndCollections;

import java.util.Collection;

public interface ListTypeVisitor<E extends Object_Transactional> {
	
	public Collection<Object_TransactionalInCollectionAdapter<E>> handle(VectorType<E> type);
	public Collection<Object_TransactionalInCollectionAdapter<E>> handle(LinkedListType<E> type);

}
