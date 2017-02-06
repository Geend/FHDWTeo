package de.fhdw.ml.transactionFramework.aspects;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import de.fhdw.ml.transactionFramework.administration.ObjectAdministration;
import de.fhdw.ml.transactionFramework.administration.ReflectionSupport;
import de.fhdw.ml.transactionFramework.typesAndCollections.Object_Transactional;
import de.fhdw.ml.transactionFramework.typesAndCollections.RealFramework_Object;
import de.fhdw.ml.transactionFramework.typesAndCollections.Framework_Object;
import de.fhdw.ml.transactionFramework.annotations.Index_Field;

public aspect Object_TransactionalAspect {

	private Long RealFramework_Object.object$Number;

	public Long RealFramework_Object.getObject$Number() {
		return this.object$Number;
	}

	public boolean Object_Transactional.equals(Object argument) {
		if (argument instanceof Framework_Object) {
			Framework_Object argumentAsTransactionalObject = (Framework_Object) argument;
			if (this.getObject$Number().equals(argumentAsTransactionalObject.getObject$Number())) {
				return true;
			} else {
				return this.isTheSame(argumentAsTransactionalObject);
			}
		} else {
			return false;
		}
	}
	public int RealFramework_Object.hashCode(){
		return new Long(this.getObject$Number()).hashCode();
	}
	pointcut objectCreation() : 
		call (RealFramework_Object+.new(..));

	after() returning (RealFramework_Object createdObject): objectCreation() {
		this.setObjectNumber(createdObject);
	}

	private void setObjectNumber(RealFramework_Object createdObject) {
		if (createdObject.getObject$Number() == null){
			createdObject.object$Number = ObjectAdministration.getCurrentAdministration().getNewObjectNumber();
			ObjectAdministration.getCurrentAdministration().handleObjectCreation(createdObject);
		}
	}

	pointcut getter(Object_Transactional readObject) : 
		(get(boolean Object_Transactional+.*) ||
		 get(int Object_Transactional+.*) ||
		 get(long Object_Transactional+.*) ||
		 get(Object+ Object_Transactional+.*))
		&& target(readObject);

	pointcut getterInConstructor(RealFramework_Object readObject) : 
		 get(* Object_Transactional+.*)
			&& target(readObject)
			&& withincode(Object_Transactional+.new(..));

	pointcut getterForTransactionObject(Object_Transactional readObject) :
		get(Framework_Object+ Object_Transactional+.*)
		&& target(readObject);

	before(RealFramework_Object readObject) : getterInConstructor(readObject) && 
												!within(de.fhdw.ml.transactionFramework.aspects.*)  &&
												!within(de.fhdw.ml.transactionFramework.administration.*) {
		this.setObjectNumber(readObject);
	}

	Object around (Object_Transactional readObject) : getter(readObject) && 
											  !within(de.fhdw.ml.transactionFramework.aspects.*) &&
											  !within(de.fhdw.ml.transactionFramework.administration.*){
		String fieldName = thisJoinPoint.getSignature().getName();
		Field field = ReflectionSupport.getFieldAccess(readObject, fieldName);
		ObjectAdministration administration = ObjectAdministration.getCurrentAdministration();
		if (!Modifier.isTransient(field.getModifiers())) administration.prepareObjectRead(readObject, fieldName); 
		Object result = proceed(readObject);
		if (!Modifier.isTransient(field.getModifiers())) administration.finishObjectRead(readObject, fieldName); 
		return result;
	}
	//TODO Think about synchronization!!!
	before(Object_Transactional readObject) : getterForTransactionObject(readObject)  && 
												!within(de.fhdw.ml.transactionFramework.aspects.*)  &&
												!within(de.fhdw.ml.transactionFramework.administration.*) {
		Field accessedField = ReflectionSupport.getFieldAccess(readObject, thisJoinPoint.getSignature().getName());
		try {
			accessedField.setAccessible(true);
			Long objectNumber = readObject.getAttributeMap().get(accessedField.getName());
			if (accessedField.get(readObject) == null && objectNumber != null) {
				accessedField.set(readObject, ObjectAdministration.getCurrentAdministration().provideObject(objectNumber));
			}
			accessedField.setAccessible(false);
		} catch (IllegalArgumentException | IllegalAccessException e) {
			throw new Error(e);
		}
	}

	pointcut setter(Object_Transactional manipulatedObject) : 
		(set(boolean Object_Transactional+.*) ||
		 set(int Object_Transactional+.*) ||
		 set(long Object_Transactional+.*) ||
		 set(Object+ Object_Transactional+.*)) 
		 && this(manipulatedObject);

	pointcut setterForTransactionObject(Object_Transactional manipulatedObject, RealFramework_Object newValue) :
		set(Framework_Object+ Object_Transactional+.*)
		&& this(manipulatedObject) 
		&& args(newValue);

	pointcut setterInConstructor(Object_Transactional readObject) : 
		 set(* Object_Transactional+.*)
			&& target(readObject)
			&& withincode(Object_Transactional+.new(..));

	pointcut setterForIndexedStringField(
			Object_Transactional manipulatedObject, String newValue) :
		set(@Index_Field String Object_Transactional+.*)
		&& this(manipulatedObject) 
		&& args(newValue);

//	pointcut setterForUniquelyIndexedStringField(
//			Object_Transactional manipulatedObject, String newValue) :
//		set(@Index_Field_Unique String Object_Transactional+.*)
//		&& this(manipulatedObject) 
//		&& args(newValue);

	before(Object_Transactional manipulatedObject) : setterInConstructor(manipulatedObject) && 
														!within(de.fhdw.ml.transactionFramework.aspects.*)  &&
														!within(de.fhdw.ml.transactionFramework.administration.*) {
		this.setObjectNumber(manipulatedObject);
	}

	void around(Object_Transactional manipulatedObject) : setter(manipulatedObject) && 
		  													!within(de.fhdw.ml.transactionFramework.aspects.*) &&
		  													!within(de.fhdw.ml.transactionFramework.administration.*){
		String fieldName = thisJoinPoint.getSignature().getName();
		Field field = ReflectionSupport.getFieldAccess(manipulatedObject, fieldName);
		boolean isTransient = Modifier.isTransient(field.getModifiers());
		if (!isTransient) ObjectAdministration.getCurrentAdministration().prepareObjectWrite(manipulatedObject, fieldName);
		proceed(manipulatedObject);
		if (!isTransient) ObjectAdministration.getCurrentAdministration().finishObjectWrite(manipulatedObject, fieldName);
	}

	void around(Object_Transactional manipulatedObject, RealFramework_Object newValue) 
		: setterForTransactionObject(manipulatedObject, newValue) && 
			!within(de.fhdw.ml.transactionFramework.aspects.*)  &&
			!within(de.fhdw.ml.transactionFramework.administration.*) {
		manipulatedObject.getAttributeMap().put(thisJoinPoint.getSignature().getName(), newValue.getObject$Number());
		ObjectAdministration.getCurrentAdministration().prepareObjectWrite(manipulatedObject, thisJoinPoint.getSignature().getName());
		proceed(manipulatedObject, newValue);
		ObjectAdministration.getCurrentAdministration().finishObjectWrite(manipulatedObject, thisJoinPoint.getSignature().getName());

	}

	before(Object_Transactional manipulatedObject, String newValue) 
		: setterForIndexedStringField(manipulatedObject, newValue) && 
			!within(de.fhdw.ml.transactionFramework.aspects.*) &&
			!within(de.fhdw.ml.transactionFramework.administration.*) {
		ObjectAdministration.getCurrentAdministration().prepareStringIndexUpdate(manipulatedObject, newValue, thisJoinPoint.getSignature().getName());
	}

	pointcut main() :
		execution(public static static void main(..));

	before() : main() {
		ObjectAdministration.getCurrentAdministration().initialiseApplication();
	}

}
