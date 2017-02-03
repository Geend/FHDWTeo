package de.fhdw.ml.transactionFramework.administration;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import de.fhdw.ml.transactionFramework.typesAndCollections.Object_Transactional;

public class ReflectionSupport {
	
	public static Field getFieldAccess(Object_Transactional object, String fieldName) {
		Field result = null;
		Class<?> currentClass = object.getClass();
		while (result == null) {
			try {
				result = currentClass.getDeclaredField(fieldName);
			} catch (NoSuchFieldException e) {
				currentClass = currentClass.getSuperclass();
			} catch (SecurityException e) {
				throw new Error(e);
			}
		}
		return result;
	}
	public static Method getMethodAccess(Object_Transactional object, String methodName, Class<?>... parameterTypes) {
		Method result = null;
		Class<?> currentClass = object.getClass();
		while (result == null) {
			try {
				result = currentClass.getDeclaredMethod(methodName, parameterTypes);
			} catch (NoSuchMethodException e) {
				currentClass = currentClass.getSuperclass();
			} catch (SecurityException e) {
				throw new Error(e);
			}
		}
		return result;
	}


}
