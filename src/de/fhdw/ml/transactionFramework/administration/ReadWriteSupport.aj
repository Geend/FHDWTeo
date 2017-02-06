package de.fhdw.ml.transactionFramework.administration;

import java.util.HashMap;
import java.util.Set;
import java.util.TreeSet;

import de.fhdw.ml.transactionFramework.transactions.TEOTransactionWith2Exceptions;
import de.fhdw.ml.transactionFramework.typesAndCollections.Object_Transactional;
import de.fhdw.ml.transactionFramework.typesAndCollections.RealFramework_Object;;

public aspect ReadWriteSupport {
	
	/**
	 * Infrastructure for read and write access on objects by transactions on individual attributes.
	 */
	private transient HashMap<String,TEOTransactionWith2Exceptions<?, ?, ?>> Object_Transactional.lastWriters;
	private transient HashMap<String,Set<TEOTransactionWith2Exceptions<?, ?, ?>>> Object_Transactional.lastReaders;
	private transient Mutex RealFramework_Object.mutex;
	private transient boolean RealFramework_Object.lastOperationHasConflictGraphUpdate;
	
	void RealFramework_Object.setConflictInCurrentOperation(boolean b){
		this.lastOperationHasConflictGraphUpdate = b;
	}
	boolean RealFramework_Object.hasConflictInCurrentOperation(){
		return this.lastOperationHasConflictGraphUpdate;
	}
	
	private Mutex RealFramework_Object.getMutex(){
		if (this.mutex == null) this.mutex = new Mutex();
		return this.mutex;
	}

	void RealFramework_Object.enterMutex(){
		this.getMutex().enter();
	}
	void RealFramework_Object.leaveMutex(){
		this.getMutex().leave();
	}
	private HashMap<String, TEOTransactionWith2Exceptions<?, ?, ?>> Object_Transactional.getLastWriters(){
		if (this.lastWriters == null) this.lastWriters = new HashMap<String,TEOTransactionWith2Exceptions<?, ?, ?>>();
		return this.lastWriters;
	}
	TEOTransactionWith2Exceptions<?, ?, ?> Object_Transactional.getLastWriter(String fieldName){
		return this.getLastWriters().get(fieldName);
	}
	TEOTransactionWith2Exceptions<?, ?, ?> Object_Transactional.putLastWriter(String fieldName, TEOTransactionWith2Exceptions<?, ?, ?> transaction){
		return this.getLastWriters().put(fieldName, transaction);
	}
	void Object_Transactional.clearLastWriter(String fieldName){
		this.getLastWriters().remove(fieldName);
	}

	private HashMap<String, Set<TEOTransactionWith2Exceptions<?, ?, ?>>> Object_Transactional.getLastReaders(){
		if (this.lastReaders == null) this.lastReaders = new HashMap<String,Set<TEOTransactionWith2Exceptions<?, ?, ?>>>();
		return this.lastReaders;
	}
	Set<TEOTransactionWith2Exceptions<?, ?, ?>> Object_Transactional.getLastReaders(String fieldName){
		return this.getLastReaders().get(fieldName);
	}
	void Object_Transactional.addLastReader(String fieldName, TEOTransactionWith2Exceptions<?, ?, ?> transaction){
		Set<TEOTransactionWith2Exceptions<?, ?, ?>> readers = this.getLastReaders().get(fieldName);
		if (readers == null) {
			readers = new TreeSet<TEOTransactionWith2Exceptions<?, ?, ?>>();
			this.getLastReaders().put(fieldName, readers);
		}
		readers.add(transaction);
	}
	void Object_Transactional.clearLastReaders(String fieldName){
		this.getLastReaders().get(fieldName).clear();
	}

}
