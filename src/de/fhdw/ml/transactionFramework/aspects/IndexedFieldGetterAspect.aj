package de.fhdw.ml.transactionFramework.aspects;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collection;

import de.fhdw.ml.transactionFramework.typesAndCollections.Object_Transactional;
import de.fhdw.ml.transactionFramework.administration.ObjectAdministration;
import de.fhdw.ml.transactionFramework.annotations.Index_Field;
import de.fhdw.ml.transactionFramework.annotations.Index_Getter;

public aspect IndexedFieldGetterAspect {
	
	private static final String GetterFieldCorrespondanceErrorMessagePrefix = "Getter method declares index field ";
	private static final String GetterFieldCorrespondanceErrorMessageSuffix = ", which is not an indexed field!";
	
	pointcut staticStringIndexGetter (String index) :
		execution(@Index_Getter public static Collection<Object_Transactional> *.get$By*(String)) && args(index);
	
	Collection<Object_Transactional> around (String index) : staticStringIndexGetter(index) {
		Class<?> declaringClass = thisJoinPoint.getSignature().getDeclaringType();
		try {
			Method method = declaringClass.getDeclaredMethod(thisJoinPoint.getSignature().getName(), new Class<?>[]{String.class});
			Index_Getter annotation = method.getAnnotation(Index_Getter.class);
			String fieldName = annotation.fieldName();
			Field field = declaringClass.getDeclaredField(fieldName);
			Index_Field fieldAnnotation = field.getAnnotation(Index_Field.class);
			if (fieldAnnotation == null) throw new Error(GetterFieldCorrespondanceErrorMessagePrefix + fieldName + GetterFieldCorrespondanceErrorMessageSuffix);
			return ObjectAdministration.getCurrentAdministration().getByIndex(declaringClass.getName(), fieldName, index);
		} catch (NoSuchMethodException | SecurityException | NoSuchFieldException e) {
			throw new Error(e);
		}
	}

}
