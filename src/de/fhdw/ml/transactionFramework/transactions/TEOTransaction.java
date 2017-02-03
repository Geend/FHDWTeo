package de.fhdw.ml.transactionFramework.transactions;

public abstract class TEOTransaction< ReturnType> 
			extends TEOTransactionWithException<ReturnType, No$Exception>{
	
	abstract protected ReturnType operation();
	
	public synchronized ReturnType getResult() {
		try {
			return super.getResult();
		} catch (No$Exception e) {
			// no other exception declared
			e.printStackTrace();
			throw new Error("Undeclared exception happened!");
		}
	}
}


class No$Exception extends Exception {
	private static final long serialVersionUID = 1L;
	
	private No$Exception(){}	
}