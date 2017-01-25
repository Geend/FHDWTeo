package de.fhdw.ml.transactionFramework.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target( java.lang.annotation.ElementType.METHOD )
@Retention(RetentionPolicy.RUNTIME)
public @interface Index_Getter {
	String fieldName();
}
