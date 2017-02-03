package de.fhdw.ml.transactionFramework.administration;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Map;
import java.util.TreeMap;

import de.fhdw.ml.transactionFramework.typesAndCollections.Framework_Object;
import de.fhdw.ml.transactionFramework.typesAndCollections.Object_Transactional;
import de.fhdw.ml.transactionFramework.typesAndCollections.RealFramework_Object;

public class ObjectStore {
	
	private static final String UserDirectoryKey_in_SystemProperty = "user.dir";
	
	private static final String RootDirectoryName = "Root_Of_Object_Store"; //TODO Configure path to root directory of object store!

	private static final String InitialiseFileName = "Init_Framework";

	private static final String MainLevelDirectoryNamePrefix = "Sub-Store";

	private static final String ObjectFileNamePrefix = "OBJ";

	private static final long ObjectStoreNumberOfMainLevelDirectories = 10;

	public static final long VeryFirstObjectNumber = 0;
	
	public static final long VeryFirstTransactionNumber = 0;
		
	private File rootDirectory = null;
	

	ObjectStore(){
		this.cache = new TreeMap<Long, Framework_Object>();
	}
	
	private FrameworkInitialValueStore initialValueStore = null;

	private Map<Long, Framework_Object> cache = null;
	
	void initialise () throws ObjectStoreRootCreationFailure, FileNotFoundException, IOException, ClassNotFoundException {
		if (initialValueStore == null) {
			File initialiseFile = this.getInitialiseFrameworkFile();
			if (!initialiseFile.exists()) {
				this.initialValueStore = new FrameworkInitialValueStore();
			} else {
				ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(initialiseFile));
				this.initialValueStore = (FrameworkInitialValueStore)inputStream.readObject();		
				inputStream.close();
			}
		}
	}
	void deleteStore() throws ObjectStoreRootCreationFailure {
		this.deleteAll(this.getRootDirectoryOfObjectStore());
	}
	private void deleteAll(File fileOrDirectory) {
		if (!fileOrDirectory.exists()) return;
		if (!fileOrDirectory.isFile()) {
			File[] containees = fileOrDirectory.listFiles();
			for (int i = 0; i < containees.length; i++) this.deleteAll(containees[i]);
		}
		fileOrDirectory.delete();
	}
	public void terminate(){
		try {
			ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(this.getInitialiseFrameworkFile()));
			outputStream.writeObject(this.initialValueStore);
			outputStream.flush();
			outputStream.close();
			System.out.println("Object store finalised!!!");
		} catch (ObjectStoreRootCreationFailure | IOException e) {
			System.out.println(e.getMessage());
		}
	}

	private File getInitialiseFrameworkFile() throws ObjectStoreRootCreationFailure {
		return new File(this.getRootDirectoryOfObjectStore(),InitialiseFileName);
	}

	void store(RealFramework_Object object) 
			throws IOException, ObjectFileCannotBeCreatedException, ObjectStoreRootCreationFailure, ObjectStoreSubRootCreationFailure {
		long objectNumber = object.getObject$Number();
		System.out.println("Begin store object: " + objectNumber);
		if (!this.cache.containsKey(objectNumber)) this.cache.put(objectNumber, object);
		File objectFile = getFileFor(objectNumber);
		ObjectOutputStream serialiser = null;
		FileOutputStream fileStream = null;
		try {
			fileStream = new FileOutputStream(objectFile);
			serialiser = new ObjectOutputStream(fileStream);
			serialiser.writeObject(object);
			serialiser.flush();
		} finally {
			if (serialiser != null) serialiser.close();			
			if (fileStream != null) fileStream.close();			
			System.out.println("End store object: " + objectNumber);
		}
	}
	public Framework_Object provideObject(Long objectNumber) throws ObjectStoreRootCreationFailure, ObjectStoreSubRootCreationFailure, IOException, ClassNotFoundException {
		System.out.println("Provide object: " + objectNumber);
		Framework_Object result = this.cache.get(objectNumber);
		if( result != null ) return result;
		File objectFile = getFileFor(objectNumber);
		ObjectInputStream deserialiser = null;
		FileInputStream fileStream = null;
		try {
			fileStream = new FileInputStream(objectFile);
			deserialiser = new ObjectInputStream(fileStream);
			result =  (Framework_Object) deserialiser.readObject();
			this.cache.put(objectNumber, result);
			return result;
		} finally {
			if (deserialiser != null) deserialiser.close();			
			if (fileStream != null) fileStream.close();			
		}
	}
	File getRootDirectoryOfObjectStore() throws ObjectStoreRootCreationFailure{
		if (rootDirectory == null) {
			rootDirectory = new File(System.getProperty(UserDirectoryKey_in_SystemProperty),RootDirectoryName);
			if (!rootDirectory.exists()) 
				try {
					if (!rootDirectory.mkdir()) throw new ObjectStoreRootCreationFailure();
				} catch (SecurityException se) {
					throw new ObjectStoreRootCreationFailure(se);
				}
		}
		return rootDirectory;
	}
	private String createObjectFileName(long objectNumber) {
		return ObjectFileNamePrefix + objectNumber;
	}
	private String createObjectDirectoryName(long objectNumber) {
		return MainLevelDirectoryNamePrefix + objectNumber % ObjectStoreNumberOfMainLevelDirectories;
	}
	private File getFileFor(long objectNumber) 
			throws ObjectStoreRootCreationFailure, ObjectStoreSubRootCreationFailure {
		File directory = this.getDirectoryForObjectNumber(objectNumber);
		return new File(directory, this.createObjectFileName(objectNumber));
	}

	private File getDirectoryForObjectNumber(long objectNumber) 
			throws ObjectStoreRootCreationFailure, ObjectStoreSubRootCreationFailure {
		File objectDirectory = new File(this.getRootDirectoryOfObjectStore(), this.createObjectDirectoryName(objectNumber));
		if (!objectDirectory.exists()) {
			if (!objectDirectory.mkdir()) throw new ObjectStoreSubRootCreationFailure(this.createObjectDirectoryName(objectNumber));
		}
		return objectDirectory;
	}

	public long getNewObjectNumber() {
		return this.initialValueStore.getNewObjectNumber();
	}

	public long getNewTransactionNumber() {
		return this.initialValueStore.getNewTransactionNumber();
	}

}
class FrameworkInitialValueStore implements Serializable {
	
	private static final long serialVersionUID = 1L;

	Long lastObjectNumber;
	Long lastTransactionNumber;

	FrameworkInitialValueStore() {
		lastObjectNumber = ObjectStore.VeryFirstObjectNumber;
		lastTransactionNumber = ObjectStore.VeryFirstTransactionNumber;
	}

	public long getNewObjectNumber() {
		synchronized (lastObjectNumber) {			
			return this.lastObjectNumber++;
		}
	}
	public long getNewTransactionNumber() {
		synchronized (lastTransactionNumber) {			
			return this.lastTransactionNumber++;
		}
	}
}

class InitialiseFileCannotBeWritten extends ObjectAdministrationException {

	private static final long serialVersionUID = 1L;
	
	private static final String InitialiseFileCannotBeWrittenMessage = "Initialise file cannot be written!";

	public InitialiseFileCannotBeWritten() {
		super(InitialiseFileCannotBeWrittenMessage);
	}
	
}

class ObjectFileCannotBeCreatedException extends ObjectAdministrationException {

	private static final String ObjectFileCannotBeCreatedMessagePrefix = "Object file cannot be created for object: ";

	public ObjectFileCannotBeCreatedException(Object_Transactional object) {
		super(ObjectFileCannotBeCreatedMessagePrefix + object.getObject$Number());
	}

	private static final long serialVersionUID = 1L;
	
}

class ObjectStoreRootCreationFailure extends ObjectAdministrationException {

	private static final long serialVersionUID = 1L;

	private static final String ObjectStoreRootCreationFailureMessage = "Cannot create root directory of object store!";

	public ObjectStoreRootCreationFailure() {
		super(ObjectStoreRootCreationFailureMessage);
	}

	public ObjectStoreRootCreationFailure(SecurityException se) {
		super(ObjectStoreRootCreationFailureMessage, se);
	}
	
}
class ObjectStoreSubRootCreationFailure extends ObjectAdministrationException {

	private static final long serialVersionUID = 1L;

	private static final String ObjectStoreSubRootCreationFailurePrefix = "Cannot create root sub-directory of object store: ";

	public ObjectStoreSubRootCreationFailure(String directoryName) {
		super(ObjectStoreSubRootCreationFailurePrefix + directoryName);
	}
	
}