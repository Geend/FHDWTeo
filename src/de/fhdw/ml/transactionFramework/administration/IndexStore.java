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
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import de.fhdw.ml.transactionFramework.typesAndCollections.Object_Transactional;

public class IndexStore {
	
	private static final String IndexRootDirectoryName = "Root_Of_Index_Store";
	
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
	
	class ClassIndex {
		Map<String,ClassIndexEntry> classIndices;
		ClassIndex(){
			this.classIndices = new HashMap<String, ClassIndexEntry>();
		}
		public ClassIndexEntry getEntry(String className) {
			ClassIndexEntry result = this.classIndices.get(className);
			if (result == null) {
				result = new ClassIndexEntry(className);
				this.classIndices.put(className, result);
			}
			return result;
		}
	}
	private ClassIndex classIndex = new ClassIndex();
	class ClassIndexEntry {
		final private String className;
		private Map<String, FieldIndexEntry> fieldIndices;
		
		ClassIndexEntry(String className) {
			this.className = className;
			this.fieldIndices = new HashMap<String, FieldIndexEntry>();
		}
		FieldIndexEntry getEntry(String fieldName) {
			FieldIndexEntry result = this.fieldIndices.get(fieldName);
			if (result == null) {
				result = new FieldIndexEntry(this.className, fieldName);
				this.fieldIndices.put(fieldName, result);
			}
			return result;
		}
	}
	class FieldIndexEntry {
		final private String className;
		final private String fieldName;
		private Map<String,IndexEntry> entries;
		
		FieldIndexEntry(String className, String fieldName) {
			this.className = className;
			this.fieldName = fieldName;
			this.entries = new HashMap<String, IndexEntry>();
		}
		String getFieldName(){
			return this.fieldName;
		}
		public IndexEntry getEntry(String index) throws ObjectStoreRootCreationFailure, IndexStoreRootCreationFailure, IndexStoreCreationFailure, FileNotFoundException, ClassNotFoundException, IOException {
			IndexEntry result = this.entries.get(index);
			if (result == null) {
				result = IndexEntry.tryGetFromStore(getRootDirectoryOfIndexStore(), this.className, this.fieldName, index);
				this.entries.put(index, result);
			}
			return result;
		}
	}
	private IndexEntry getIndexEntry(String className, String fieldName, String index) throws ObjectStoreRootCreationFailure, IndexStoreRootCreationFailure, IndexStoreCreationFailure, FileNotFoundException, ClassNotFoundException, IOException {
		ClassIndexEntry classIndexEntry = this.classIndex.getEntry(className);
		FieldIndexEntry fieldIndexEntry = classIndexEntry.getEntry(fieldName);
		return fieldIndexEntry.getEntry(index);
	}
	public Set<Long> getByIndex(String className, String fieldName, String index) 
			throws ObjectStoreRootCreationFailure, IndexStoreRootCreationFailure, IndexStoreCreationFailure, FileNotFoundException, IOException, ClassNotFoundException {
		System.out.println("Get by index class: \"" + className +"\" field: \"" + fieldName + "\" index: \"" + index + "\"");
		return this.getIndexEntry(className, fieldName, index).objectNumbers;
	}

	public void updateIndex(String className, String fieldName, Object_Transactional object, String newValue, String oldValue) 
			throws FileNotFoundException, ClassNotFoundException, IOException, 
				   ObjectStoreRootCreationFailure, IndexStoreRootCreationFailure, IndexEntryDoesNotExistException, IndexStoreCreationFailure, IndexFileCannotBedeletedException {
		System.out.println("Update index class: \"" + className +"\" field: \"" + fieldName + "\" object: \"" + object.getObject$Number() + "\" newValue: \"" + newValue + "\"");
		if (oldValue != null) {
			IndexEntry indexEntry = this.getIndexEntry(className, fieldName, oldValue);
			indexEntry.delete(object.getObject$Number());
			indexEntry.store(getRootDirectoryOfIndexStore());
		}
		if (newValue != null) {
			IndexEntry indexEntry = this.getIndexEntry(className, fieldName, newValue);
			indexEntry.add(object.getObject$Number());
			indexEntry.store(getRootDirectoryOfIndexStore());
		}
	}
	@SuppressWarnings("unused")
	private String getAsFileName(long i) {
		return new Long(i).toString();
	}

}
class IndexEntry implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private static final String StringIndexEntryFileSuffix = "S";

	static IndexEntry tryGetFromStore(File rootOfIndexStore, String className, String fieldName, String index) throws ObjectStoreRootCreationFailure, IndexStoreRootCreationFailure, IndexStoreCreationFailure, FileNotFoundException, IOException, ClassNotFoundException {
		String indexName = createIndexName(className, fieldName);
		File indexDirectory = getIndexDirectory(rootOfIndexStore, indexName);
		String indexAsFileName = getAsFileName(index);
		File indexEntryFile = createIndexEntryFile(indexDirectory, indexAsFileName);
		IndexEntry entry = null;
		if (indexEntryFile.exists()) {
			ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(indexEntryFile));
			entry = (IndexEntry) inputStream.readObject();			
			inputStream.close();
		} else {
			entry = new IndexEntry(indexName, indexAsFileName);
		}
		return entry;
	}
	static String createIndexName(String className, String fieldName) {
		return className + "-" + fieldName;
	}
	static File getIndexDirectory(File rootOfIndexStore, String fieldName) 
			throws ObjectStoreRootCreationFailure, IndexStoreRootCreationFailure, IndexStoreCreationFailure {
		File result = new File(rootOfIndexStore, fieldName);
		if (!result.exists()) 
			if (!result.mkdir()) throw new IndexStoreCreationFailure(fieldName);
		return result;
	}
	static private String getAsFileName(String s) {
		return base64Encode(s) + StringIndexEntryFileSuffix;
	}
	static Encoder theBase64Encoder = Base64.getUrlEncoder();

	static private String base64Encode(String oldValue) {
		return theBase64Encoder.encodeToString(oldValue.getBytes());
	}
	static private File createIndexEntryFile(File indexDirectory, String oldValueAsFileName) {
		return new File(indexDirectory,oldValueAsFileName);
	}

	final private String directoryName;
	final private String indexAsBase64String;
	Set<Long> objectNumbers;
	
	public IndexEntry(String directoryName, String indexAsBase64String) {
		this.indexAsBase64String = indexAsBase64String;
		this.directoryName = directoryName;
		this.objectNumbers = new TreeSet<Long>();
	}

	public void store(File rootOfIndexStore) throws ObjectStoreRootCreationFailure, IndexStoreRootCreationFailure, IndexStoreCreationFailure, IOException, IndexFileCannotBedeletedException {
		File indexDirectory = getIndexDirectory(rootOfIndexStore, this.directoryName);
		File indexEntryFile = createIndexEntryFile(indexDirectory, indexAsBase64String);
		if (this.objectNumbers.isEmpty()) {
			if (!indexEntryFile.delete()) throw new IndexFileCannotBedeletedException(indexEntryFile.getName());
		} else {
			FileOutputStream fileOutStream = null;
			ObjectOutputStream outputStream = null;
			try {
				fileOutStream = new FileOutputStream(indexEntryFile);
				outputStream = new ObjectOutputStream(fileOutStream);
				outputStream.writeObject(this);
				outputStream.flush();
			} finally {
				if (outputStream != null) outputStream.close();
				if (fileOutStream != null) fileOutStream.close();
			}
		}
	}

	public void add(Long object$Number) {
		this.objectNumbers.add(object$Number);
	}

	public void delete(Long object$Number) {
		this.objectNumbers.remove(object$Number);
	}
	public boolean isEmpty(){
		return this.objectNumbers.isEmpty();
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