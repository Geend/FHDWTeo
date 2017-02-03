package de.fhdw.ml.transactionFramework.transactions;

public abstract class ActiveTransactionObject implements Runnable  {

	protected Buffer<TEOTransactionWith2Exceptions<?, ?, ?>> inputBuffer = null;
	protected Thread thread = null;
	
	protected ActiveTransactionObject(){
		this.inputBuffer  = new Buffer<TEOTransactionWith2Exceptions<?, ?, ?>>();
	}
	public void stop() {
		this.inputBuffer.stop();
	}
	public void handle(TEOTransactionWith2Exceptions<?, ?, ?> task) {
		this.inputBuffer.put( task );
	}
	public abstract void run();
	
}
