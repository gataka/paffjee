package de.paffjeep.mrf.configuration.resilience.circuitebreaker;

import java.lang.annotation.Documented;

/**
 * Created by BEYH on 27.03.2017.
 */
@Documented
public @interface CircuiteBreakerConfiguration {
    /**
     * big number to prevent the semaphore to become the bottleneck.
     *
     * @return 500
     */
    int execution_isolation_semapthore_maxConcurrentRequests() default 500;

    /**
     * execution isolation strategy (use THREAD - it executes commands on a
     * separate thread and concurrent requests are limited by the number of
     * threads in the thread-pool.)
     *
     * @return SEMAPHORE
     */
    String execution_isolation_strategy() default "SEMAPHORE";

    /**
     * execution timeout (This property sets the time in milliseconds after
     * which the caller will observe a timeout and walk away from the command
     * execution.)
     *
     * @return 10000
     */
    int execution_isolation_thread_timeoutInMilliseconds() default 10000;

    /**
     * execution timeout enabled
     *
     * @return true
     */
    boolean execution_timeout_enabled() default true;

    /**
     * circuit breaker enabled
     *
     * @return true
     */
    boolean circuitBreaker_enabled() default true;

    /**
     * circuit breaker request volume threshold (This property sets the minimum
     * number of requests in a rolling window that will trip the circuit.)
     * adjust to project/command specific value
     *
     * @return 20
     */
    int circuitBreaker_requestVolumeThreshold() default 20;

    /**
     * circuit breaker sleep window (This property sets the amount of time,
     * after tripping the circuit, to reject requests before allowing attempts
     * again to determine if the circuit should again be closed.) adjust to
     * project/command specific value
     *
     * @return 5000
     */
    int circuitBreaker_sleepWindowInMilliseconds() default 5000;

    /**
     * circuit breaker error threshold (This property sets the error percentage
     * at or above which the circuit should trip open and start short-circuiting
     * requests to fallback logic.) adjust to project/command specific value
     *
     * @return
     */
    int circuitBreaker_errorThresholdPercentage() default 30;

    /**
     * thread pool size.
     *
     * @return 30
     */
    int threadpool_coreSize() default 30;

    /**
     * maximumSize.
     *
     * @return 50
     */
    int threadpool_maximumSize() default 50;

    /**
     * thread pool size.
     *
     * @return 30
     */
    int max_QueueSize() default 30;

    /**
     * thread pool size.
     *
     * @return 30
     */
    int queueSizeRejectionThreshold() default 10;

    /**
     * allowMaximumSizeToDivergeFromCoreSize.
     *
     * @return true.
     */
    boolean allowMaximumSizeToDivergeFromCoreSize() default true;

    /**
     * metrics rolling window (This property sets the duration of the
     * statistical rolling window, in milliseconds.) adjust to project/command
     * specific value
     *
     * @return 10000
     */
    int metrics_rollingStats_timeInMilliseconds() default 10000;

    /**
     * metrics rolling window buckets (This property sets the number of buckets
     * the rolling statistical window is divided into.) adjust to
     * project/command specific value
     *
     * @return
     */
    int metrics_rollingStats_numBuckets() default 10;
}
