package de.paffjeep.mrf.configuration.resilience.circuitebreaker.impl;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommand.Setter;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandKey;
import com.netflix.hystrix.HystrixCommandProperties;
import com.netflix.hystrix.HystrixCommandProperties.ExecutionIsolationStrategy;
import com.netflix.hystrix.HystrixThreadPoolKey;
import com.netflix.hystrix.HystrixThreadPoolProperties;
import com.netflix.hystrix.exception.HystrixRuntimeException;
import de.paffjeep.mrf.configuration.resilience.circuitebreaker.CircuiteBreakerCommand;
import de.paffjeep.mrf.configuration.resilience.circuitebreaker.CircuiteBreakerFallback;
import de.paffjeep.mrf.configuration.resilience.circuitebreaker.EnableCircuiteBreaker;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Priority;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.TreeMap;

@Priority(Interceptor.Priority.LIBRARY_BEFORE)
@EnableCircuiteBreaker
@Interceptor
public class CircuiteBreaker implements Serializable {

    private Logger cbLogger = LoggerFactory.getLogger(CircuiteBreaker.class);
    private CBContext cbtx;

    /**
     * Default constructor
     */
    public CircuiteBreaker() {
        super();
        cbtx = new CBContext();
    }

    @AroundInvoke
    public Object observeWithCB(final InvocationContext context) throws Throwable {
        final CircuiteBreakerCommand cbCommand = context.getMethod().getAnnotation(CircuiteBreakerCommand.class);
        final Map<Integer, Method> fallbacks = new TreeMap<>();
        final Method[] methods = context.getTarget().getClass().getMethods();
        for (int i = 0; i < methods.length; i++) {
            if (methods[i].isAnnotationPresent(CircuiteBreakerFallback.class)) {
                if (methods[i].getAnnotation(CircuiteBreakerFallback.class).value() > 1) {
                    fallbacks.put(methods[i].getAnnotation(CircuiteBreakerFallback.class).value(), methods[i]);
                } else {
                    fallbacks.put(i, methods[i]);
                }

            }
        }

        if (cbCommand != null) {
            // Sett the default Hystrix properties from the annotation
            cbLogger.debug("Start of CircuiteBreaker processing");
            final HystrixCommandProperties.Setter commandPropperties = HystrixCommandProperties.Setter().withFallbackEnabled(!fallbacks.isEmpty())
                    .withCircuitBreakerEnabled(cbCommand.configuration().circuitBreaker_enabled()).withCircuitBreakerErrorThresholdPercentage(cbCommand.configuration().circuitBreaker_errorThresholdPercentage())
                    .withCircuitBreakerSleepWindowInMilliseconds(cbCommand.configuration().circuitBreaker_sleepWindowInMilliseconds()).withCircuitBreakerRequestVolumeThreshold(cbCommand.configuration().circuitBreaker_requestVolumeThreshold())
                    .withMetricsRollingStatisticalWindowBuckets(cbCommand.configuration().metrics_rollingStats_numBuckets()).withMetricsRollingStatisticalWindowInMilliseconds(cbCommand.configuration().metrics_rollingStats_timeInMilliseconds())
                    .withExecutionIsolationStrategy(ExecutionIsolationStrategy.valueOf(cbCommand.configuration().execution_isolation_strategy()))
                    .withExecutionIsolationSemaphoreMaxConcurrentRequests(cbCommand.configuration().execution_isolation_semapthore_maxConcurrentRequests())
                    .withExecutionTimeoutEnabled(cbCommand.configuration().execution_timeout_enabled()).withExecutionTimeoutInMilliseconds(cbCommand.configuration().execution_isolation_thread_timeoutInMilliseconds());

            final HystrixThreadPoolProperties.Setter threadpoolProperties = HystrixThreadPoolProperties.Setter().withAllowMaximumSizeToDivergeFromCoreSize(cbCommand.configuration().allowMaximumSizeToDivergeFromCoreSize())
                    .withCoreSize(cbCommand.configuration().threadpool_coreSize()).withMaxQueueSize(cbCommand.configuration().max_QueueSize()).withMaximumSize(cbCommand.configuration().threadpool_maximumSize())
                    .withQueueSizeRejectionThreshold(cbCommand.configuration().queueSizeRejectionThreshold());

            final Setter hystrixSettings = Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey(cbCommand.commandGroup())).andCommandKey(HystrixCommandKey.Factory.asKey(cbCommand.commandName()))
                    .andCommandPropertiesDefaults(commandPropperties).andThreadPoolKey(HystrixThreadPoolKey.Factory.asKey(cbCommand.commandGroup() + "-pool"))
                    .andThreadPoolPropertiesDefaults(threadpoolProperties);

            Object result = null;
            try {
                result = new HystrixCommand<Object>(hystrixSettings) {


                    @Override
                    protected Object run() throws Exception {

                        if (cbCommand.replayRule().value() > 0) {
                            cbLogger.warn("Trying to replay");

                            for (int i = 0; i < cbCommand.replayRule().value(); i++) {
                                try {
                                    Thread.sleep(cbCommand.replayRule().delay());
                                    return context.proceed();
                                } catch (final Exception replayException) {
                                    cbLogger.warn("Execution of the replay failed.", replayException);
                                    cbtx.setCommandException(replayException);
                                }
                            }
                        }
                        try {
                            cbLogger.debug("Execute the method of Command " + cbCommand.commandName());
                            return context.proceed();
                        } catch (final Exception normalPlayException) {
                            cbtx.setCommandException(normalPlayException);
                            throw normalPlayException;
                        }
                    }

                    @Override
                    protected Object getFallback() {

                        Object result = null;

                        for (final Integer order : fallbacks.keySet()) {
                            final Method fallbackMethod = fallbacks.get(order);
                            try {
                                cbLogger.warn("Execution of the fallback .");
                                result = fallbackMethod.invoke(context.getTarget(), ArrayUtils.add(context.getParameters(), cbtx));
                            } catch (final Exception fallbackException) {
                                cbLogger.error("CB-FAILED", "Execution of the fallback failed: ", fallbackException);
                                if (fallbackMethod.getAnnotation(CircuiteBreakerFallback.class).replayrule().value() > 0) {
                                    cbLogger.warn("Trying to replay");
                                    cbtx.setFallbackException(fallbackException);
                                    for (int i = 0; i < fallbackMethod.getAnnotation(CircuiteBreakerFallback.class).replayrule().value(); i++) {
                                        try {
                                            Thread.sleep(fallbackMethod.getAnnotation(CircuiteBreakerFallback.class).replayrule().delay());

                                            result = fallbackMethod.invoke(context.getParameters(), cbtx);
                                            break;
                                        } catch (final Exception replayException) {
                                            cbLogger.warn("Execution of the replay failed.", replayException);
                                            cbtx.setFallbackException(replayException);
                                        }

                                    }

                                }

                            }
                            if (result != null) {
                                break;
                            }
                        }
                        return result;
                    }

                }.execute();
            } catch (final Exception e) {
                cbLogger.error("CB-ERROR", "Exception in CB-Execution", e);
                if (e instanceof HystrixRuntimeException) {
                    throw e.getCause();
                }
            }

            return result;

        } else {
            cbLogger.warn("Circuite Breaker active but no Command Annotation found. Execute Method without CB.");
            return context.proceed();
        }
    }

    public class CBContext {
        private Exception commandException;
        private Exception fallbackException;

        private CBContext() {

        }

        /**
         * @return the failException
         */
        public Exception getCommandException() {
            return commandException;
        }

        /**
         * @param commandException
         *            the failException to set
         */
        protected CBContext setCommandException(final Exception commandException) {
            this.commandException = commandException;
            return this;
        }

        /**
         * @return the fallbackException
         */
        public Throwable getFallbackException() {
            return fallbackException;
        }

        /**
         * @param fallbackException
         *            the fallbackException to set
         */
        protected CBContext setFallbackException(final Exception fallbackException) {
            this.fallbackException = fallbackException;
            return this;
        }

    }
}
