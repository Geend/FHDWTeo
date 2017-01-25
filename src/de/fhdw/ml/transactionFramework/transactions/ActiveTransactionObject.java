package de.fhdw.ml.transactionFramework.transactions;

public abstract class ActiveTransactionObject implements Runnable  {

	protected Buffer<TEOTransactionWithTwoExceptions<?, ?, ?>> inputBuffer = null;
	protected Thread thread = null;
	
	protected ActiveTransactionObject(){
		this.inputBuffer  = new Buffer<TEOTransactionWithTwoExceptions<?, ?, ?>>();
	}
	public void stop() {
		this.inputBuffer.stop();
	}
	public void handle(TEOTransactionWithTwoExceptions<?, ?, ?> task) {
		this.inputBuffer.put( task );
	}
	public abstract void run();
	
}
