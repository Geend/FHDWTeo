package de.fhdw.ml.transactionFramework.administration;

import java.util.Comparator;

import de.fhdw.ml.transactionFramework.transactions.TEOTransactionWith2Exceptions;
import de.fhdw.ml.transactionFramework.transactions.TransactionThread;
import de.fhdw.ml.transactionFramework.typesAndCollections.Object_Transactional;

public class OptimisticObjectAdministration extends ObjectAdministration {

	private ConflictGraph<TEOTransactionWith2Exceptions<?, ?, ?>> conflictGraph;

	protected OptimisticObjectAdministration() {
		this.conflictGraph = new ConflictGraph<>(new Comparator<TEOTransactionWith2Exceptions<?, ?, ?>>() {

			@Override
			public int compare(TEOTransactionWith2Exceptions<?, ?, ?> t1, TEOTransactionWith2Exceptions<?, ?, ?> t2) {
				return new Long(t1.getTransactionNumber()).compareTo(t2.getTransactionNumber());
			}

		});
	}

	@Override
	public void prepareObjectRead(Object_Transactional object, String fieldName) {
		try {

			synchronized (object) {

				TransactionThread currentThread = (TransactionThread) Thread.currentThread();
				TEOTransactionWith2Exceptions<?, ?, ?> currentTransaction = currentThread.getOwnerOfThisThread()
						.getCurrentTransaction();

				TEOTransactionWith2Exceptions<?, ?, ?> lastWriter = object.getLastWriters().get(fieldName);

				if (lastWriter != null) {
					this.conflictGraph.putSuccessor(lastWriter, currentTransaction, t -> {
						this.abort(t);
					});
				}
				object.addLastReader(fieldName, currentTransaction);
			}
		} catch (ClassCastException cee) {
			return;
		}
	}

	private void abort(TEOTransactionWith2Exceptions<?, ?, ?> t) {
		// TODO Auto-generated method stub

	}
}
