package de.fhdw.ml.transactionFramework.aspects;

import java.io.ObjectStreamException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;

import de.fhdw.ml.transactionFramework.administration.ObjectAdministration;
import de.fhdw.ml.transactionFramework.administration.ReflectionSupport;
import de.fhdw.ml.transactionFramework.typesAndCollections.Framework_Object;
import de.fhdw.ml.transactionFramework.typesAndCollections.Object_Transactional;

public aspect GeneralTransactionalAspect {
	
	declare precedence : GeneralTransactionalAspect, Object_TransactionalAspect;
	
	private static final String CloneOperationName = "clone";
	
	private HashMap<String, Long> Object_Transactional.transactionalAttributeMap;
	
	HashMap<String, Long> Object_Transactional.getTransactionalAttributeMap(){
		if (transactionalAttributeMap == null) transactionalAttributeMap = new HashMap<String,Long>();
		return transactionalAttributeMap;
	}
	public Object Object_Transactional.writeReplace() throws ObjectStreamException {
		System.out.println("Start writeReplace: " + this.getObject$Number());
		Object_Transactional result = this.copy$Me();
		GeneralTransactionalAspect.setNonStaticTransactionalFieldsNull(result, result.getClass());
		return result;
	}
	public Object Object_Transactional.readResolve() throws ObjectStreamException {
		return this;
	}
	@SuppressWarnings("unchecked")
	public Object_Transactional Object_Transactional.copy$Me() {
		Object_Transactional result = null;
		try {
			Method cloneMethod = ReflectionSupport.getMethodAccess(this, CloneOperationName, new Class[0]);
			cloneMethod.setAccessible(true);
			result = (Object_Transactional) cloneMethod.invoke(this, new Object[0]);
			result.transactionalAttributeMap = (HashMap<String, Long>) result.getTransactionalAttributeMap().clone();
			cloneMethod.setAccessible(false);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | SecurityException e) {
			throw new Error(e);
		}
		return result;
	}

	pointcut serializing(Object_Transactional ths) :
		execution (public Object writeReplace()) && this(ths);
	
	before (Object_Transactional ths) : serializing(ths) {
		ObjectAdministration.getCurrentAdministration().prepareSerializingObject(ths);
	}
	
	private static void setNonStaticTransactionalFieldsNull(Object_Transactional object, Class<?> clss) {
		if (clss.equals(Object.class)) return;
		setNonStaticTransactionalFieldsNull(object, clss.getDeclaredFields());
		setNonStaticTransactionalFieldsNull(object, clss.getSuperclass());
	}
	private static void setNonStaticTransactionalFieldsNull(Object_Transactional object, Field[] fields) {
		for (Field field : fields) {
			if (!Modifier.isStatic(field.getModifiers())) setNonStaticTransactionalFieldNull(object,field);
		}
	}
	private static void setNonStaticTransactionalFieldNull(Object_Transactional object, Field field) {
		if (Framework_Object.class.isAssignableFrom(field.getType())){
			try {
				field.setAccessible(true);
				field.set(object, null);
				System.out.println("To null: " + field.getDeclaringClass().getName() + " : " + field.getName());
				field.setAccessible(false);
			} catch (IllegalArgumentException | IllegalAccessException e) {
				throw new Error(e);
			}
		}
	}

}
