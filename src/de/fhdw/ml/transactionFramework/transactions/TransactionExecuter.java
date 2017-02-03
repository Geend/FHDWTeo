package de.fhdw.ml.transactionFramework.transactions;

import de.fhdw.ml.transactionFramework.transactions.Buffer.StopException;

class TransactionExecuter extends ActiveTransactionObject {

	private static Integer lastNumber = new Integer(0);
	final private Integer number;

	final private TransactionManager manager;
	private TEOTransactionWithTwoExceptions<?, ?, ?> currentTask;

	public TransactionExecuter( TransactionManager manager){
		this.manager = manager;
		synchronized(lastNumber){
			this.number = ++lastNumber;
		}
		this.thread = new TransactionThread( this );
		this.thread.start();
	}

	@Override
	public void run() {
		while( true ){
			try {
				TEOTransactionWithTwoExceptions<?, ?, ?> task = this.inputBuffer.get();
				this.setAndExecuteCurrentTransaction(task);
				manager.acknowlegdeExecution( task );
			} catch (StopException e) {
				return;
			}
		}
	}
	private void setAndExecuteCurrentTransaction(TEOTransactionWithTwoExceptions<?, ?, ?> task) {
		this.currentTask = task;
		task.execute();
	}

	public String toString() {
		return "Executer " + this.number;
	}

	public TEOTransactionWithTwoExceptions<?, ?, ?> getcurrentTransaction() {
		return this.currentTask;
	}
}
