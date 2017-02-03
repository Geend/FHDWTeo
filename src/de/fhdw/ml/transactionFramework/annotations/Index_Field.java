package de.fhdw.ml.transactionFramework.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target( java.lang.annotation.ElementType.FIELD )
@Retention(RetentionPolicy.RUNTIME)
public @interface Index_Field {
}
