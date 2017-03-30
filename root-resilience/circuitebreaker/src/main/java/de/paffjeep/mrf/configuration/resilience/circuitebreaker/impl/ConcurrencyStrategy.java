package de.paffjeep.mrf.configuration.resilience.circuitebreaker.impl;

import com.netflix.hystrix.HystrixThreadPoolKey;
import com.netflix.hystrix.strategy.properties.HystrixProperty;
import org.slf4j.MDC;

import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.enterprise.concurrent.ManagedThreadFactory;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Custom implementation of Hystrix concurrency strategy.
 *
 * @author Goran Jovic (ESG GmbH)
 */
@Stateless
public class ConcurrencyStrategy extends com.netflix.hystrix.strategy.concurrency.HystrixConcurrencyStrategy {

    @Resource(name = "concurrent/hystrixthreadfactory")
    private ManagedThreadFactory factory;

    /**
     * Default constructor.
     */
    public ConcurrencyStrategy() {
        super();
    }

    @Override
    public ThreadPoolExecutor getThreadPool(final HystrixThreadPoolKey threadPoolKey,
            final HystrixProperty<Integer> corePoolSize, final HystrixProperty<Integer> maximumPoolSize,
            final HystrixProperty<Integer> keepAliveTime, final TimeUnit unit,
            final BlockingQueue<Runnable> workQueue) {

        final int dynamicCoreSize = corePoolSize.get();
        final int dynamicMaximumSize = maximumPoolSize.get();
        if (factory == null) {
            return super.getThreadPool(threadPoolKey, corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
        }
        if (dynamicCoreSize > dynamicMaximumSize) {
            return new ThreadPoolExecutor(dynamicCoreSize, dynamicCoreSize, keepAliveTime.get(), unit, workQueue,
                    factory);
        } else {
            return new ThreadPoolExecutor(dynamicCoreSize, dynamicMaximumSize, keepAliveTime.get(), unit, workQueue,
                    factory);
        }
    }

    /*
         * (non-Javadoc)
         *
         * @see com.netflix.hystrix.strategy.concurrency.HystrixConcurrencyStrategy#
         * wrapCallable(java.util.concurrent.Callable)
         */
    @Override
    public <T> Callable<T> wrapCallable(final Callable<T> callable) {

        return new CallableWrapper<>(callable);
    }

    /**
     * CallableWrapper implementation. Task of wrapper is to create a CDLL
     * logging context for the new thread in the thread pool for each request.
     *
     * @author Goran Jovic (ESG GmbH)
     */
    private static class CallableWrapper<K> implements Callable<K> {

        private final Callable<K> actual;
        private final Map<String, String> contextMap;

        /**
         * Constructor with callable.
         *
         * @param actual callable
         */
        public CallableWrapper(final Callable<K> actual) {

            this.actual = actual;

            // get transaction id from parent thread

            this.contextMap = MDC.getCopyOfContextMap();
        }

        /*
         * (non-Javadoc)
         *
         * @see java.util.concurrent.Callable#call()
         */
        @Override
        public K call() throws Exception {// NOSONAR

            // set transaction id from parent thread
            MDC.setContextMap(contextMap);

            try {
                return actual.call();
            } finally {
                MDC.clear();
            }

        }

    }
}
