package de.paffjeep.mrf.configuration.resilience.circuitebreaker;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Retention(RUNTIME)
@Target(METHOD)
public @interface CircuiteBreakerFallback {
	/**
	 * Order of the Fallbacks;
	 */
	int value() default 1;

	/**
	 * Default null;
	 *
	 * @return
	 */
	CircuiteBreakerReplayRule replayrule() default @CircuiteBreakerReplayRule(value = 0);

}
