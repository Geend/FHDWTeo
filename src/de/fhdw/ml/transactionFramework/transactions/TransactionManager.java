package de.fhdw.ml.transactionFramework.transactions;

interface TransactionManager {

	void acknowlegdeExecution(TEOTransactionWith2Exceptions<?, ?, ?> task);
	void reportTermination();

}
