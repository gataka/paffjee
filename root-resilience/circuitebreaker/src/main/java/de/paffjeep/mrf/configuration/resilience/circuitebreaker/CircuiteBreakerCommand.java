/**
 * 
 */
package de.paffjeep.mrf.configuration.resilience.circuitebreaker;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;

@Retention(RetentionPolicy.RUNTIME)
@Target(METHOD)
@Documented
/**
 * @author BEYH Resilience configuration.
 */
public @interface CircuiteBreakerCommand {

	String commandName() default "default";

	String commandGroup() default "default";

	boolean replayAfterFallback() default false;

	CircuiteBreakerReplayRule replayRule() default @CircuiteBreakerReplayRule(value = 0);

	CircuiteBreakerConfiguration configuration() default @CircuiteBreakerConfiguration();

}
