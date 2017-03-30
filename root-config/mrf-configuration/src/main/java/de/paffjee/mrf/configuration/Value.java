package de.paffjee.mrf.configuration;

import javax.enterprise.util.Nonbinding;
import javax.inject.Qualifier;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by BEYH on 28.03.2017.
 */
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Target({ ElementType.FIELD,ElementType.PARAMETER,ElementType.LOCAL_VARIABLE,ElementType.TYPE_PARAMETER,ElementType.METHOD})
@Qualifier
public @interface Value {
    Class<?> type();

    @Nonbinding String key() default "";

    @Nonbinding String defaultValue() default "novalue";
}
