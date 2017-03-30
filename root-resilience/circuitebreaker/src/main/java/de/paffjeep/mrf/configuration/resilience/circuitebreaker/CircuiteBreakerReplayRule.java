package de.paffjeep.mrf.configuration.resilience.circuitebreaker;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Retention(RUNTIME)
@Target(METHOD)
@Documented
public @interface CircuiteBreakerReplayRule {
	/**
	 * Count of FallbackReplay. Default 1
	 */
	int value() default 1;

	/**
	 * Count of delay between the replay. In ms.
	 */
	int delay() default 100;
}
