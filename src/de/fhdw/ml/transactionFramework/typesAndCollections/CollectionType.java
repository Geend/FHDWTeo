package de.fhdw.ml.transactionFramework.typesAndCollections;

import java.util.Collection;

public interface CollectionType<E extends Object_Transactional> {

	Collection<Object_TransactionalInCollectionAdapter<E>> accept(CollectionTypeVisitor<E> collectionTypeVisitor);

}
