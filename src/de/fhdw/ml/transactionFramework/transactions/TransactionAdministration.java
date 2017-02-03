package de.fhdw.ml.transactionFramework.transactions;

import java.util.HashMap;
import java.util.Map;

import de.fhdw.ml.transactionFramework.transactions.Buffer.StopException;

public class TransactionAdministration extends ActiveTransactionObject implements TransactionManager {

	private static final int NumberOfExecuters = 5;

	private static TransactionAdministration theTransactionAdministration = null;

	synchronized public static TransactionAdministration getTheTransactionAdministration() {
		if( theTransactionAdministration  == null ){
			theTransactionAdministration = new TransactionAdministration();
		}
		return theTransactionAdministration;
	}
	
	private Buffer<TransactionExecuter> executerPool = null;
	private Map< TEOTransactionWith2Exceptions<?, ?, ?>, TransactionExecuter > taskMap = new HashMap<TEOTransactionWith2Exceptions<?, ?, ?>, TransactionExecuter>();

	private int runningExecuters;
	
	private TransactionAdministration(){
		this.initExecuterPool();		
		this.thread = new Thread( this, "Transaction Administration" );
		this.thread.setPriority(Thread.MAX_PRIORITY);
		this.thread.start();
	}

	private void initExecuterPool() {
		this.executerPool = new Buffer<TransactionExecuter>();
		for( this.runningExecuters = 0; this.runningExecuters < NumberOfExecuters; this.runningExecuters++ ){
			this.executerPool.put(new TransactionExecuter( this ));
		}
	}
	@Override
	public void handle(TEOTransactionWith2Exceptions<?, ?, ?> task) {
		if (! (task.state == InitialState.theInitialState)) throw new Error("Transactions shall not be handled twice!");
		task.state = ReadyState.theReadyState;
		super.handle(task);
	}

	@Override
	public void run() {
		while(true){
			TEOTransactionWith2Exceptions<?, ?, ?> task = null;
			try {
				task = this.inputBuffer.get();
			} catch (StopException e) {
				stopThreadExecuters();
				return;
			}
			try{
				startExecution(task);
			} catch (StopException e) {
				throw new Error(e);
			}
		}
	}
	
	private void stopThreadExecuters() {
		for( int i = 0; i < NumberOfExecuters; i++ ){
			TransactionExecuter executer;
			try {
				executer = this.executerPool.get();
			} catch (StopException e) {
				throw new Error(e);
			}
			executer.stop();
		}
	}

	private void startExecution(TEOTransactionWith2Exceptions<?, ?, ?> task) throws StopException {
		// Manager waits for idle executor. Executor gets at most one task into its input queue.
		TransactionExecuter executer = this.executerPool.get();
		synchronized( this ){			
			this.taskMap.put(task, executer);
		}
		executer.handle(task);
	}

	@Override
	public void acknowlegdeExecution(TEOTransactionWith2Exceptions<?, ?, ?> task) {
		TransactionExecuter executer = null;
		synchronized( this ){			
			executer = this.taskMap.remove(task);
		}
		this.executerPool.put(executer);
	}
	synchronized public void terminate() {
		this.stop();
		while (this.runningExecuters != 0) {
			try {
				this.wait();
			} catch (InterruptedException e) {
				return;
			}
		}
		theTransactionAdministration = null;
	}

	@Override
	synchronized public void reportTermination() {
		if (--this.runningExecuters == 0) this.notify();
	}
		
}
