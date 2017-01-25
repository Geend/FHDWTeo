package de.fhdw.ml.transactionFramework.transactions;

interface TransactionManager {

	void acknowlegdeExecution(TEOTransactionWithException<?, ?> task);

}
