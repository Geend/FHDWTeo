package de.fhdw.ml.transactionFramework.typesAndCollections;

import de.fhdw.ml.transactionFramework.administration.ObjectAdministration;

public interface Framework_CollectionObject extends RealFramework_Object {

	static interface CollectionMethod<R> {
		R collectionMethod();
	}

	static public <R> R collectionMethod(Framework_CollectionObject This, String methodName, ReadWrite readOrWrite, CollectionMethod<R> collectionReadMethod) {
		ObjectAdministration administration = ObjectAdministration.getCurrentAdministration();
			prepare(This, methodName, readOrWrite, administration);
			R result = collectionReadMethod.collectionMethod();
			finish(This, methodName, readOrWrite, administration);
			return result;				 
	}

	static void prepare(Framework_CollectionObject This, String methodName, ReadWrite readOrWrite, ObjectAdministration administration) {
		readOrWrite.accept(new ReadWriteVisitor<Void>() {
			@Override
			public Void handleWrite() {
				administration.prepareCollectionWrite(This, methodName); 
				return null;
			}
			@Override
			public Void handleRead() {
				administration.prepareCollectionRead(This, methodName); 
				return null;
			}
		});
	}

	static void finish(Framework_CollectionObject This, String methodName, ReadWrite readOrWrite, ObjectAdministration administration) {
		readOrWrite.accept(new ReadWriteVisitor<Void>() {
			@Override
			public Void handleWrite() {
				administration.finishCollectionWrite(This, methodName); 
				return null;
			}
			@Override
			public Void handleRead() {
				administration.finishCollectionRead(This, methodName); 
				return null;
			}
		});
	}

}
