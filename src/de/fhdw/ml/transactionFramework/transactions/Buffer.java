package de.fhdw.ml.transactionFramework.transactions;

import java.util.LinkedList;

class Buffer<T> {
	
	static class StopException extends Exception{
		private static final long serialVersionUID = 1L;

		StopException(){
			super("Stop entry retrieved!");
		}
	}
	
	private LinkedList<BufferEntry<T>> entries = null;

	private abstract static class BufferEntry<T>{
		public abstract T getWrapped() throws StopException;
	}
	
	private static class EntryWrapper<T> extends BufferEntry<T>{
		private T entry;
		EntryWrapper(T entry){
			this.entry = entry;
		}
		@Override
		public T getWrapped() {
			return this.entry;
		}
	}
	private static class Stop<T> extends BufferEntry<T>{
		@Override
		public T getWrapped() throws StopException {
			throw new StopException();
		}		
	}
	public Buffer(){
		this.entries = new LinkedList<BufferEntry<T>>();
	}
	public void put(T entry){
		this.put(new EntryWrapper<T>(entry));
	}
	synchronized private void put(BufferEntry<T> entry) {
		this.entries.addLast( entry );
		this.notify();
	}
	synchronized public T get() throws StopException{
		while( this.entries.isEmpty()){
			try {
				this.wait();
			} catch (InterruptedException e) {
				throw new StopException();
			}
		}
		T result = this.entries.removeFirst().getWrapped();
		return result;
	}
	synchronized public void stop(){
		this.put(new Stop<T>());
	}
	synchronized public int size(){
		return this.entries.size();
	}

}
