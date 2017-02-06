package de.fhdw.ml.transactionFramework.typesAndCollections;


public interface ReadWrite {
	static public final ReadWrite READ = Read.read; 
	static public final ReadWrite WRITE = Write.write;
	public abstract <T> T accept(ReadWriteVisitor<T> visitor);
}
class Read implements ReadWrite {
	static Read read = new Read();
	private Read(){}
	@Override
	public <T> T accept(ReadWriteVisitor<T> visitor) {
		return visitor.handleRead();
	}
}	
class Write implements ReadWrite {
	static Write write = new Write();
	private Write(){}
	@Override
	public <T> T accept(ReadWriteVisitor<T> visitor) {
		return visitor.handleWrite();
	}
}
