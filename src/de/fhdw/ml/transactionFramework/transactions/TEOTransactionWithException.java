package de.fhdw.ml.transactionFramework.transactions;

import de.fhdw.ml.transactionFramework.administration.ObjectAdministration;

abstract public class TEOTransactionWithException<ReturnType, E extends Exception> extends TEOTransactionWithTwoExceptions<ReturnType, E, No$Exception>{
	
	abstract protected ReturnType operation() throws E;
	
	public synchronized ReturnType getResult() throws E {
		try {
			return super.getResult();
		} catch (No$Exception e) {
			// no other exception declared
			e.printStackTrace();
			throw new Error("Undeclared exception happened!");
		}
	}
}

