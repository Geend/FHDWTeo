package de.fhdw.ml.transactionFramework.administration;

public interface OnCycle<T> {
	
	public void handleCycle(T t);

}
