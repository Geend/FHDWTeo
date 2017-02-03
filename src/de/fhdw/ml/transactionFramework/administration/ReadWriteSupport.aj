package de.fhdw.ml.transactionFramework.administration;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import de.fhdw.ml.transactionFramework.transactions.TEOTransactionWith2Exceptions;
import de.fhdw.ml.transactionFramework.typesAndCollections.Object_Transactional;

public aspect ReadWriteSupport {

	
	private HashMap<String, Set<TEOTransactionWith2Exceptions<?, ?, ?>>> Object_Transactional.lastReaders;
	

	private HashMap<String, Set<TEOTransactionWith2Exceptions<?, ?, ?>>> Object_Transactional.getLastReaders(){
		if(this.lastReaders == null){
			this.lastReaders = new HashMap<String, Set<TEOTransactionWith2Exceptions<?, ?, ?>>>();
		}
		
		return this.lastReaders;
	};
	
	void Object_Transactional.addLastReader(String fieldName, TEOTransactionWith2Exceptions<?, ?, ?> t){
		Set<TEOTransactionWith2Exceptions<?, ?, ?>> currentReaders = this.getLastReaders().get(fieldName);
		
		if(currentReaders == null){
			currentReaders = new HashSet<TEOTransactionWith2Exceptions<?, ?, ?>>();
			this.getLastReaders().put(fieldName, currentReaders);
		}		
		
		currentReaders.add(t);
		
	}
	void Object_Transactional.clearLastReaders(String fieldName){
		this.getLastReaders().remove(fieldName);
	}
	
	
	private HashMap<String, TEOTransactionWith2Exceptions<?, ?, ?>> Object_Transactional.lastWriters;
	
	public HashMap<String, TEOTransactionWith2Exceptions<?, ?, ?>> Object_Transactional.getLastWriters(){
		if(this.lastWriters == null){
			this.lastWriters = new HashMap<String, TEOTransactionWith2Exceptions<?, ?, ?>>();
		}
		
		return this.lastWriters;
	};	
	
	void Object_Transactional.clearLastWriters(String fieldName){
		this.getLastWriters().remove(fieldName);
	}
	
	TEOTransactionWith2Exceptions<?, ?, ?> Object_Transactional.setWriter(String fieldName, TEOTransactionWith2Exceptions<?, ?, ?> w){
		return this.getLastWriters().put(fieldName, w);
	}
	
	
	
	
	
}
 