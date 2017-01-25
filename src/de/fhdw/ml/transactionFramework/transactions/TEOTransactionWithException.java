package de.fhdw.ml.transactionFramework.transactions;

import de.fhdw.ml.transactionFramework.administration.ObjectAdministration;

abstract public class TEOTransactionWithException< ReturnType, E extends Exception> {
	
	private final long transactionNumber;
	
	ReturnType result;
	TransactionState state = InitialState.theInitialState;
	E exception = null;
	Error error = null;
	RuntimeException runtimeException = null;
	
	public TEOTransactionWithException() {
		this.transactionNumber = ObjectAdministration.getCurrentAdministration().getNewTransactionNumber();
	}
	
	public long getTransactionNumber(){
		return transactionNumber;
	}
	abstract protected ReturnType operation() throws E;
	
	/**
	 * Waits for execution of the operation.
	 * 	
	 * @return result
	 * @throws E     if operation result is exceptional.
	 * @throws Error if interrupted before execution.
	 */
	public synchronized ReturnType getResult() throws E {
		if( this.state == InitialState.theInitialState ) throw new Error("Transaction has not been delivered for execution!");
		while( this.state == ReadyState.theReadyState ){
			try {
				this.wait();
			} catch (InterruptedException e) {
				throw new Error("Execution interrupted!");
			}
		}
		if( this.state == FinalState.theFinalState ) throw new Error("Double result delivery prohibited!");
		this.state = FinalState.theFinalState;
		if( this.error != null ) throw this.error;
		if( this.runtimeException != null ) throw this.runtimeException;
		if( this.exception != null ) throw this.exception;
		return this.result;
	}
}

abstract class TransactionState{}
class InitialState extends TransactionState{
	static InitialState theInitialState = new InitialState();
	private InitialState(){}
}
class ReadyState extends TransactionState {
	static ReadyState theReadyState = new ReadyState();
	private ReadyState(){}	
}
class ExecutedState extends TransactionState{
	static ExecutedState theExecutedState = new ExecutedState();	
	private ExecutedState(){}	
}
class FinalState extends TransactionState{
	static FinalState theFinalState = new FinalState();	
	private FinalState(){}	
}