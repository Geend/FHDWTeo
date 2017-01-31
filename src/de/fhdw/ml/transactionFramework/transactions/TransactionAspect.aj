package de.fhdw.ml.transactionFramework.transactions;

import de.fhdw.ml.transactionFramework.transactions.TEOTransactionWithTwoExceptions;

public aspect TransactionAspect {
	
	synchronized void TEOTransactionWithTwoExceptions.execute() {
		if( ! ( this.state == ReadyState.theReadyState ) ) throw new Error("Transaction is not ready for execution or has been executed already!");
		try {
			this.result = operation();	
			this.commit();			
		} catch (RuntimeException rte){
			this.runtimeException  = rte;
			this.rollBack();			
		} catch (Exception e) {
			this.exception = e;
			this.rollBack();			
		} catch (Error fatalError){
			this.error  = fatalError;
			this.rollBack();			
		}
		this.state = ExecutedState.theExecutedState;
		this.notify();
	}
	private void TEOTransactionWithTwoExceptions.commit() {
		System.out.println("Handle successful completion (commit) of transaction: " + this.getTransactionNumber() + "!"); //TODO Handle commit
	}
	private void TEOTransactionWithTwoExceptions.rollBack() {
		System.out.println("Handle failure (rollBack) in transaction execution: " + this.getTransactionNumber() + "!");	//TODO Handle rollBack	
	}
}
