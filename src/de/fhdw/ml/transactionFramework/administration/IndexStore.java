package de.fhdw.ml.transactionFramework.administration;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Base64;
import java.util.Base64.Encoder;
import java.util.Set;
import java.util.TreeSet;

import de.fhdw.ml.transactionFramework.typesAndCollections.Object_Transactional;

public class IndexStore {
	
	private static final String IndexRootDirectoryName = "Root_Of_Index_Store";

	private static final String StringIndexEntryFileSuffix = "S";

	private File indexRootDirectory;

	IndexStore(){}
	
	private File getRootDirectoryOfIndexStore() throws ObjectStoreRootCreationFailure, IndexStoreRootCreationFailure{
		if (indexRootDirectory == null) {
			indexRootDirectory = new File(ObjectAdministration.getCurrentAdministration().getRootDirectoryOfObjectStore(),IndexRootDirectoryName);
			if (!indexRootDirectory.exists()) 
				try {
					if (!indexRootDirectory.mkdir()) throw new IndexStoreRootCreationFailure();
				} catch (SecurityException se) {
					throw new IndexStoreRootCreationFailure(se);
				}
		}
		return indexRootDirectory;
	}

	public Set<Long> getByIndex(String className, String fieldName, String index) 
			throws ObjectStoreRootCreationFailure, IndexStoreRootCreationFailure, IndexStoreCreationFailure, FileNotFoundException, IOException, ClassNotFoundException {
		System.out.println("Get by index class: \"" + className +"\" field: \"" + fieldName + "\" index: \"" + index + "\"");
		String indexName = this.createIndexName(className, fieldName);
		File indexDirectory = this.getIndexDirectory(indexName);
		String indexAsFileName = this.getAsFileName(index);
		File indexEntryFile = this.createIndexEntryFile(indexDirectory, indexAsFileName);
		IndexEntry entry = null;
		if (indexEntryFile.exists()) {
			ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(indexEntryFile));
			entry = (IndexEntry) inputStream.readObject();			
			inputStream.close();
		} else {
			entry = new IndexEntry(indexAsFileName);
		}
		return entry.objectsWithThatIndex;
	}

	public void updateIndex(String className, String fieldName, Object_Transactional object, String newValue, String oldValue) 
			throws FileNotFoundException, ClassNotFoundException, IOException, 
				   ObjectStoreRootCreationFailure, IndexStoreRootCreationFailure, IndexEntryDoesNotExistException, IndexStoreCreationFailure, IndexFileCannotBedeletedException {
		System.out.println("Update index class: \"" + className +"\" field: \"" + fieldName + "\" object: \"" + object.getObject$Number() + "\" newValue: \"" + newValue + "\"");
		String indexName = this.createIndexName(className, fieldName);
		File indexDirectory = this.getIndexDirectory(indexName);
		if (oldValue != null) {
			String oldValueAsFileName = this.getAsFileName(oldValue);			
			this.deleteIndexEntry(indexDirectory, oldValueAsFileName, object);
		}
		if (newValue != null) {
			String newValueAsFileName = this.getAsFileName(newValue);
			this.addIndexEntry(indexDirectory, newValueAsFileName, object);			
		}
	}

	private void addIndexEntry(File indexDirectory, String valueAsFileName, Object_Transactional object) throws ClassNotFoundException, IOException {
		File indexEntryFile = this.createIndexEntryFile(indexDirectory, valueAsFileName);
		IndexEntry entry = null;
		if (indexEntryFile.exists()) {
			FileInputStream fileStream = null;
			ObjectInputStream inputStream = null;
			try {
				fileStream = new FileInputStream(indexEntryFile);
				inputStream = new ObjectInputStream(fileStream);
				entry = (IndexEntry) inputStream.readObject();	
			} finally {
				if (inputStream != null) inputStream.close();
				if (fileStream != null) fileStream.close();				
			}
		} else {
			entry = new IndexEntry(valueAsFileName);
		}
		entry.add(object.getObject$Number());
		FileOutputStream fileOutStream = null;
		ObjectOutputStream outputStream = null;
		try {
			fileOutStream = new FileOutputStream(indexEntryFile);
			outputStream = new ObjectOutputStream(fileOutStream);
			outputStream.writeObject(entry);
			outputStream.flush();				
		} finally {
			if (outputStream != null) outputStream.close();
			if (fileOutStream != null) fileOutStream.close();
		}
	}
	private void deleteIndexEntry(File indexDirectory, String valueAsFileName, Object_Transactional object) 
			throws FileNotFoundException, IOException, ClassNotFoundException, IndexEntryDoesNotExistException, IndexFileCannotBedeletedException {
		File indexEntryFile = this.createIndexEntryFile(indexDirectory, valueAsFileName);
		if (!indexEntryFile.exists()) throw new IndexEntryDoesNotExistException(indexDirectory.getName(),valueAsFileName);
		FileInputStream fileInStream = null;
		ObjectInputStream inputStream = null;
		IndexEntry entry = null;
		try {
			fileInStream = new FileInputStream(indexEntryFile);
			inputStream = new ObjectInputStream(fileInStream);
			entry = (IndexEntry) inputStream.readObject();
		} finally {
			if (inputStream != null) inputStream.close();
			if (fileInStream != null) fileInStream.close();
		}
		entry.delete(object.getObject$Number());
		if (entry.isEmpty()) {
			if (!indexEntryFile.delete()) throw new IndexFileCannotBedeletedException(indexEntryFile.getName());
		} else {
			FileOutputStream fileOutStream = null;
			ObjectOutputStream outputStream = null;
			try {
				fileOutStream = new FileOutputStream(indexEntryFile);
				outputStream = new ObjectOutputStream(fileOutStream);
				outputStream.writeObject(entry);
				outputStream.flush();				
			} finally {
				if (outputStream != null) outputStream.close();
				if (fileOutStream != null) fileOutStream.close();
			}
		}
	}

	private File createIndexEntryFile(File indexDirectory, String oldValueAsFileName) {
		return new File(indexDirectory,oldValueAsFileName);
	}

	private String base64Encode(String oldValue) {
		return this.getBase64Encoder().encodeToString(oldValue.getBytes());
	}

	Encoder theBase64Encoder = null;
	private Encoder getBase64Encoder() {
		if (this.theBase64Encoder == null) theBase64Encoder = Base64.getUrlEncoder();
		return theBase64Encoder;
	}

	private String createIndexName(String className, String fieldName) {
		return className + "-" + fieldName;
	}

	private File getIndexDirectory(String fieldName) 
			throws ObjectStoreRootCreationFailure, IndexStoreRootCreationFailure, IndexStoreCreationFailure {
		File result = new File(this.getRootDirectoryOfIndexStore(),fieldName);
		if (!result.exists()) 
			if (!result.mkdir()) throw new IndexStoreCreationFailure(fieldName);
		return result;
	}

	private String getAsFileName(String s) {
		return this.base64Encode(s) + StringIndexEntryFileSuffix;
	}
	@SuppressWarnings("unused")
	private String getAsFileName(long i) {
		return new Long(i).toString();
	}
}
class IndexEntry implements Serializable {
	
	private static final long serialVersionUID = 1L;

	final String indexAsBase64String;
	Set<Long> objectsWithThatIndex;
	
	public IndexEntry(String indexAsBase64String) {
		this.indexAsBase64String = indexAsBase64String;
		this.objectsWithThatIndex = new TreeSet<Long>();
	}

	public void add(Long object$Number) {
		this.objectsWithThatIndex.add(object$Number);
	}

	public void delete(Long object$Number) {
		this.objectsWithThatIndex.remove(object$Number);
	}
	public boolean isEmpty(){
		return this.objectsWithThatIndex.isEmpty();
	}
}
class IndexEntryDoesNotExistException extends ObjectAdministrationException {
	
	private static final long serialVersionUID = 1L;
	
	private static final String IndexEntryMessagePrefix = "Index entry ";
	private static final String IndexEntryMessageInfix = " cannot be found in " ;

	public IndexEntryDoesNotExistException(String directoryName, String valueAsFileName) {
		super(IndexEntryMessagePrefix + valueAsFileName + IndexEntryMessageInfix + directoryName );
	} 
}
class IndexStoreRootCreationFailure extends ObjectAdministrationException {

	private static final long serialVersionUID = 1L;
	
	private static final String IndexStoreRootCreationFailureMessage = "Cannot create root directory of index store!";

	public IndexStoreRootCreationFailure() {
		super(IndexStoreRootCreationFailureMessage);
	}

	public IndexStoreRootCreationFailure(SecurityException se) {
		super(IndexStoreRootCreationFailureMessage, se);
	}
}
class IndexStoreCreationFailure extends ObjectAdministrationException {

	private static final long serialVersionUID = 1L;
	
	private static final String IndexStoreCreationFailurePrefix = "Cannot create index directory for: ";

	public IndexStoreCreationFailure(String fieldName) {
		super(IndexStoreCreationFailurePrefix + fieldName);
	}
}
class IndexFileCannotBedeletedException extends ObjectAdministrationException {

	private static final long serialVersionUID = 1L;

	private static final String IndexFileCannotBedeletedMessage = "Index file cannot be deleted: ";

	public IndexFileCannotBedeletedException(String fileName) {
		super(IndexFileCannotBedeletedMessage + fileName);
	}
	
}