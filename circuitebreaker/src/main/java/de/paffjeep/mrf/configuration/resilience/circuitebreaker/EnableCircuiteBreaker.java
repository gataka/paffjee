package de.paffjeep.mrf.configuration.resilience.circuitebreaker;

import javax.interceptor.InterceptorBinding;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Created by BEYH on 02.03.2017.
 */
@InterceptorBinding
@Target({ TYPE })
@Retention(RUNTIME)
@Inherited
public @interface EnableCircuiteBreaker {
}
