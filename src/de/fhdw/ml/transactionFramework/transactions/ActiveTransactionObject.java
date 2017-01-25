package de.fhdw.ml.transactionFramework.transactions;

public abstract class ActiveTransactionObject implements Runnable  {

	protected Buffer<TEOTransactionWithException<?, ?>> inputBuffer = null;
	protected Thread thread = null;
	
	protected ActiveTransactionObject(){
		this.inputBuffer  = new Buffer<TEOTransactionWithException<?, ?>>();
	}
	public void stop() {
		this.inputBuffer.stop();
	}
	public void handle(TEOTransactionWithException<?, ?> task) {
		this.inputBuffer.put( task );
	}
	public abstract void run();
	
}
