package de.fhdw.ml.transactionFramework.transactions;

class TransactionThread extends Thread {

	final private TransactionExecuter ownerOfThisThread;
	
	public TransactionExecuter getOwnerOfThisThread() {
		return this.ownerOfThisThread;
	}

	TransactionThread(TransactionExecuter ownerOfThisThread) {
		super(ownerOfThisThread);
		this.ownerOfThisThread = ownerOfThisThread;
		this.setName(this.ownerOfThisThread.toString());
	}

}
