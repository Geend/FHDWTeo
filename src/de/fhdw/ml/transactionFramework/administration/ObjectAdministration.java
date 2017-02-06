package de.fhdw.ml.transactionFramework.administration;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Set;

import de.fhdw.ml.transactionFramework.transactions.TransactionAdministration;
import de.fhdw.ml.transactionFramework.typesAndCollections.Framework_CollectionObject;
import de.fhdw.ml.transactionFramework.typesAndCollections.Object_Transactional;
import de.fhdw.ml.transactionFramework.typesAndCollections.RealFramework_Object;

public class ObjectAdministration {
	
	
	private static final String ObjectStoreCannotBeInitialisedMessage = "Object store cannot be initialsed!";
	private static final String ObjectAdministrationRuntimeExceptionMessage = "Object store runtime exception: ";
	private static ObjectAdministration theAdministration = null;
	
	public static ObjectAdministration getCurrentAdministration(){
		if (theAdministration == null) theAdministration = new OptimisticObjectAdministration();
		return theAdministration;
	}
	
	private ObjectStore theObjectStore = null;
	private IndexStore theIndexStore = null;
	

	public ObjectAdministration() {}
	
	public ObjectStore getTheObjectStore(){
		if (this.theObjectStore == null) this.theObjectStore = new ObjectStore();
		return this.theObjectStore;
	}
	IndexStore getTheIndexStore(){
		if (theIndexStore == null) theIndexStore = new IndexStore();
		return theIndexStore;
	}

	public void initialiseApplication() {
		System.out.println("Initialise framework"); //TODO Initialise framework 
		this.doInitialiseApplication();
	}
	public void finaliseApplication() {
		System.out.println("finalise framework"); //TODO Initialise framework 
		this.doFinaliseApplication();
	}
	public void handleObjectCreation(RealFramework_Object createdObject) {
		System.out.println("Object creation: " + createdObject.getClass().toString() + " with number  " + createdObject.getObject$Number());	//TODO Handle create
		this.storeObject(createdObject);
	}
	public void prepareObjectRead(Object_Transactional object, String fieldName) {
		System.out.println("Prepare read of field " + fieldName + " in: " + object.getObject$Number()); //TODO Prepare read of field
	}
	public void finishObjectRead(Object_Transactional object, String fieldName) {
		System.out.println("Finish read of field " + fieldName + " in: " + object.getObject$Number()); //TODO Finish read of field
	}
	public void prepareObjectWrite(Object_Transactional object, String fieldName) {
		System.out.println("Prepare write of field " + fieldName + " in: " + object.getObject$Number()); //TODO Prepare write of field 
	}
	public void finishObjectWrite(Object_Transactional object, String fieldName) {
		System.out.println("Finish write of field " + fieldName + " in: " + object.getObject$Number()); //TODO Finish write of field 
		this.storeObject(object);			
	}
	public void prepareCollectionRead(Framework_CollectionObject collection, String methodName) {
		System.out.println("Prepare collection read " + methodName + " in: " + collection.getObject$Number()); //TODO Prepare collection read
	}
	public void finishCollectionRead(Framework_CollectionObject collection, String methodName) {
		System.out.println("Finish collection read " + methodName + " in: " + collection.getObject$Number()); //TODO Finishcollection read
	}
	public void prepareCollectionWrite(Framework_CollectionObject collection, String methodName) {
		System.out.println("Prepare collection write " + methodName + " in: " + collection.getObject$Number()); //TODO Prepare collection write
	}
	public void finishCollectionWrite(Framework_CollectionObject collection, String methodName) {
		System.out.println("Finish collection write by method " + methodName + " in: " +  collection.getObject$Number()); //TODO Finish collection write
		this.storeObject(collection);
	}
	public Object provideObject(Long objectNumber) {
		try {
			return this.getTheObjectStore().provideObject(objectNumber);
		} catch (ObjectStoreRootCreationFailure | ObjectStoreSubRootCreationFailure e) {
			throw new ObjectAdministrationError(ObjectAdministrationRuntimeExceptionMessage, e);
		} catch ( ClassNotFoundException | IOException e) {
			throw new ObjectAdministrationError(ObjectAdministrationRuntimeExceptionMessage + e.getMessage());
		}
	}
	public void prepareSerializingObject(Object_Transactional object) {
		System.out.println("Prepare serialization of object: " + object.getObject$Number()); //TODO Prepare serialization of object
	}
	public void prepareStringIndexUpdate(Object_Transactional manipulatedObject, String newValue, String fieldName) {
		System.out.println("Prepare index update of object " + manipulatedObject.getObject$Number() + " for field " + fieldName); //TODO Prepare index update of object
		this.doStringIndexUpdate(manipulatedObject, newValue, fieldName);
	}

	private void handleObjectAdmistrationException(ObjectAdministrationException exception) {
		System.out.println(exception.getMessage());
	}

//--------------------------------------------------------------------------------------------------------------------------------------------------
	public void deleteObjectStore(){
		try {
			this.getTheObjectStore().deleteStore();
			theAdministration = null;
			System.out.println("Object store deleted!");
		} catch (ObjectStoreRootCreationFailure e) {
			this.handleObjectAdmistrationException(e);
		}
	}
	
	private void doInitialiseApplication() {
		try {
			this.getTheObjectStore().initialise();
		} catch (ObjectStoreRootCreationFailure e) {
			this.handleObjectAdmistrationException(e);
		} catch (ClassNotFoundException | IOException e) {
			this.handleObjectAdmistrationException(new ObjectAdministrationException(ObjectStoreCannotBeInitialisedMessage, e));
		} 
	}
	private void doFinaliseApplication() {
		this.getTheObjectStore().terminate(); 
		TransactionAdministration.getTheTransactionAdministration().terminate();
		theAdministration = null;
	}
	private void storeObject(RealFramework_Object createdObject) {
		try {
			this.getTheObjectStore().store(createdObject);
		} catch (IOException e) {
			this.handleObjectAdmistrationException(new ObjectCannotBeStoredException(e, createdObject.getObject$Number()));
		} catch (ObjectAdministrationException e) {
			this.handleObjectAdmistrationException(e);
		}
	}
	private void doStringIndexUpdate(Object_Transactional manipulatedObject, String newValue, String fieldName) {
		Field indexField = ReflectionSupport.getFieldAccess(manipulatedObject,fieldName);
		indexField.setAccessible(true);
		String oldValue;
		try {
			oldValue = (String)indexField.get(manipulatedObject);
		} catch (IllegalArgumentException | IllegalAccessException e) {
			throw new Error(e);
		}
		indexField.setAccessible(false);
		try {
			this.getTheIndexStore().updateIndex(indexField.getDeclaringClass().getName(), fieldName, manipulatedObject, newValue, oldValue);
		} catch (ClassNotFoundException | IOException e) {
			this.handleObjectAdmistrationException(new IndexCannotBeUpdatedException(e, fieldName));
		} catch (ObjectAdministrationException e) {
			this.handleObjectAdmistrationException(e);
		}
	}

	public long getNewObjectNumber() {
		return this.getTheObjectStore().getNewObjectNumber();
	}

	public long getNewTransactionNumber() {
		return this.getTheObjectStore().getNewTransactionNumber();
	}

	public File getRootDirectoryOfObjectStore() throws ObjectStoreRootCreationFailure{
		return this.getTheObjectStore().getRootDirectoryOfObjectStore();
	}

	synchronized public Collection<Object_Transactional> getByIndex(String className, String fieldName, String index) {
		try {
			Set<Long> numbersForObjectsInIndex = this.getTheIndexStore().getByIndex(className, fieldName, index);
			Collection<Object_Transactional> result = new LinkedList<Object_Transactional>();
			for (Long currentObjectNumber : numbersForObjectsInIndex) {
				result.add((Object_Transactional) this.getTheObjectStore().provideObject(currentObjectNumber));
			}
			return result;
		} catch (ObjectStoreRootCreationFailure | ObjectStoreSubRootCreationFailure | IndexStoreRootCreationFailure | IndexStoreCreationFailure e) {
			throw new ObjectAdministrationError(ObjectAdministrationRuntimeExceptionMessage, e);
		}catch (FileNotFoundException fnfe) {
			return new LinkedList<Object_Transactional>();
		}catch (ClassNotFoundException | IOException e) {
			throw new ObjectAdministrationError(ObjectAdministrationRuntimeExceptionMessage + e.getMessage());
		} 
	}
}

class ObjectAdministrationException extends Exception {

	private static final long serialVersionUID = 1L;

	private static final String CauseInfix = " CAUSE: ";

	public ObjectAdministrationException(String message, Exception cause) {
		super(message, cause);
	}

	public ObjectAdministrationException(String message) {
		super(message);
	}
	public String getMessage(){
		return super.getMessage() + (this.getCause() == null ? "" : CauseInfix + this.getCause().getMessage());
	}
}
class ObjectCannotBeStoredException extends ObjectAdministrationException {
	
	private static final long serialVersionUID = 1L;
	
	private static final String ObjectCannotBeStoredMessagePrefix = "Object ";
	private static final String ObjectCannotBeStoredMessageSuffix = " cannot be stored!";

	public ObjectCannotBeStoredException(Exception e, long objectNumber) {
		super(ObjectCannotBeStoredMessagePrefix + objectNumber + ObjectCannotBeStoredMessageSuffix, e);
	}
}
class IndexCannotBeUpdatedException extends ObjectAdministrationException {
	
	private static final long serialVersionUID = 1L;

	private static final String IndexCannotBeUpdatedMessagePrefix = "Index for field ";
	private static final String IndexCannotBeUpdatedMessageSuffix = " cannot be updated!";

	public IndexCannotBeUpdatedException(Exception e, String fieldName) {
		super(IndexCannotBeUpdatedMessagePrefix + fieldName + IndexCannotBeUpdatedMessageSuffix, e);
	}
}
class ObjectAdministrationError extends Error {

	private static final long serialVersionUID = 1L;

	public ObjectAdministrationError(String message, ObjectAdministrationException e) {
		super(message, e);
	}

	public ObjectAdministrationError(String message) {
		super(message);
	}
	
}