package de.fhdw.ml.transactionFramework.transactions;

interface TransactionManager {

	void acknowlegdeExecution(TEOTransactionWithTwoExceptions<?, ?, ?> task);
	void reportTermination();

}
