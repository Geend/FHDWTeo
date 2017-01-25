package de.fhdw.ml.transactionFramework.typesAndCollections;

import java.io.Serializable;

public interface Framework_Object extends Serializable {

	default public boolean isTheSame(Framework_Object argument) {
		return false;
	}
	public Long getObject$Number();

}
