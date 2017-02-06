package de.fhdw.ml.transactionFramework.administration;

import java.util.Set;

import de.fhdw.ml.transactionFramework.transactions.TEOTransactionWith2Exceptions;
import de.fhdw.ml.transactionFramework.transactions.TransactionThread;
import de.fhdw.ml.transactionFramework.typesAndCollections.Object_Transactional;
import de.fhdw.ml.transactionFramework.typesAndCollections.ReadWrite;
import de.fhdw.ml.transactionFramework.typesAndCollections.ReadWriteVisitor;

public class OptimisticObjectAdministration extends ObjectAdministration {
	
	private final ConflictGraph<TEOTransactionWith2Exceptions<?,?,?>> conflictGraph;
	private Mutex mutex;
	
	public OptimisticObjectAdministration() {
		super();
		this.conflictGraph = new ConflictGraph<TEOTransactionWith2Exceptions<?,?,?>>();
		this.mutex = new Mutex();
	}
	public void prepareObjectRead(Object_Transactional object, String fieldName) {
		super.prepareObjectRead(object, fieldName);
		object.enterMutex();
		TEOTransactionWith2Exceptions<?, ?, ?> currentTransaction;
		try {
			TransactionThread currentThread = (TransactionThread)Thread.currentThread();
			currentTransaction = currentThread.getOwnerOfThisThread().getCurrentTransaction();
		} catch (ClassCastException cce) {
			return;
		}
		boolean mayNeedConflictGraph = this.mayProduceConflictGraphUpdate(object, fieldName, currentTransaction, ReadWrite.READ);
		if (mayNeedConflictGraph) {
			object.leaveMutex();
			this.mutex.enter();
			object.enterMutex();
			TEOTransactionWith2Exceptions<?, ?, ?> lastWriter = object.getLastWriter(fieldName);
			if (lastWriter != null) {
				try {
					this.conflictGraph.putSuccessor(lastWriter, currentTransaction);
					object.setConflictInCurrentOperation(true);
				} catch (CycleException e) {
					this.abort(currentTransaction);
				}
			}
		}
		object.addLastReader(fieldName, currentTransaction);
	}
	public void finishObjectRead(Object_Transactional object, String fieldName) {
		boolean conflictGraphUpdate = object.hasConflictInCurrentOperation();
		object.setConflictInCurrentOperation(false);
		object.leaveMutex();
		if (conflictGraphUpdate) this.mutex.leave();
		super.finishObjectRead(object, fieldName);
	}

	private boolean mayProduceConflictGraphUpdate(Object_Transactional object, String fieldName, TEOTransactionWith2Exceptions<?, ?, ?> transaction, ReadWrite readWrite){
		return readWrite.accept(new ReadWriteVisitor<Boolean>() {
			@Override
			public Boolean handleWrite() {
				Set<TEOTransactionWith2Exceptions<?, ?, ?>> lastReaders = object.getLastReaders(fieldName);
				TEOTransactionWith2Exceptions<?, ?, ?> lastWriter = object.getLastWriter(fieldName);
				return !lastReaders.isEmpty() || (lastWriter != null && lastWriter != transaction);
			}
			@Override
			public Boolean handleRead() {
				return object.getLastWriter(fieldName) != null;
			}
		});
	}
	private void abort(TEOTransactionWith2Exceptions<?, ?, ?> transaction) {
		// TODO
	}

}

