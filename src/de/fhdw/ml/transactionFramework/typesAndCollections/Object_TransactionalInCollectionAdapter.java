package de.fhdw.ml.transactionFramework.typesAndCollections;

import de.fhdw.ml.transactionFramework.administration.ObjectAdministration;

@SuppressWarnings("serial")
class Object_TransactionalInCollectionAdapter<O extends Object_Transactional> implements Framework_Object {

	private long objectNumber;
	transient O object;
	public Object_TransactionalInCollectionAdapter(O object) {
		this.object = object;
		this.objectNumber = object.getObject$Number();
	}
	@SuppressWarnings("unchecked")
	public O getObject() {
		if (this.object == null) {
			this.object = (O) ObjectAdministration.getCurrentAdministration().provideObject(this.objectNumber);
		}
		return this.object;
	}
	public String toString(){
		return this.getObject().toString();
	}
	public boolean equals(Object argument) {
		if (argument instanceof Framework_Object) {
			if (((Framework_Object) argument).getObject$Number().equals(this.objectNumber)) return true;
			return ((Framework_Object) argument).isTheSame(this.getObject());
		}
		return false;
	}
	@Override
	public Long getObject$Number() {
		return this.objectNumber;
	}
	public boolean isTheSame(Framework_Object argument) {
		return this.getObject().isTheSame(argument);
	}
	public int hashCode(){
		return this.getObject().hashCode();
	}
}
