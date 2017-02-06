package de.fhdw.ml.transactionFramework.typesAndCollections;

public interface ReadWriteVisitor<R> {
	R handleRead();
	R handleWrite();
}