package de.fhdw.ml.transactionFramework.transactions;

import java.util.HashMap;
import java.util.Map;

import de.fhdw.ml.transactionFramework.transactions.Buffer.StopException;

public class TransactionAdministration extends ActiveTransactionObject implements TransactionManager {

	private static final int NumberOfExecuters = 5;

	private static ActiveTransactionObject theTransactionAdministration = null;

	public static ActiveTransactionObject getTheTransactionAdministration() {
		if( theTransactionAdministration  == null ){
			theTransactionAdministration = new TransactionAdministration();
		}
		return theTransactionAdministration;
	}
	
	private Buffer<TransactionExecuter> executerPool = null;
	private Map< TEOTransactionWithTwoExceptions<?, ?, ?>, TransactionExecuter > taskMap = new HashMap<TEOTransactionWithTwoExceptions<?, ?, ?>, TransactionExecuter>();
	
	private TransactionAdministration(){
		this.initExecuterPool();		
		this.thread = new Thread( this, "Transaction Administration" );
		this.thread.setPriority(Thread.MAX_PRIORITY);
		this.thread.start();
	}

	private void initExecuterPool() {
		this.executerPool = new Buffer<TransactionExecuter>();
		for( int i = 0; i < NumberOfExecuters; i++ ){
			this.executerPool.put(new TransactionExecuter( this ));
		}
	}
	@Override
	public void handle(TEOTransactionWithTwoExceptions<?, ?, ?> task) {
		if (! (task.state == InitialState.theInitialState)) throw new Error("Transactions shall not be handled twice!");
		task.state = ReadyState.theReadyState;
		super.handle(task);
	}

	@Override
	public void run() {
		while(true){
			TEOTransactionWithTwoExceptions<?, ?, ?> task = null;
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

	private void startExecution(TEOTransactionWithTwoExceptions<?, ?, ?> task) throws StopException {
		// Manager waits for idle executor. Executor gets at most one task into its input queue.
		TransactionExecuter executer = this.executerPool.get();
		synchronized( this ){			
			this.taskMap.put(task, executer);
		}
		executer.handle(task);
	}

	@Override
	public void acknowlegdeExecution(TEOTransactionWithTwoExceptions<?, ?, ?> task) {
		TransactionExecuter executer = null;
		synchronized( this ){			
			executer = this.taskMap.remove(task);
		}
		this.executerPool.put(executer);
	}
			
}
