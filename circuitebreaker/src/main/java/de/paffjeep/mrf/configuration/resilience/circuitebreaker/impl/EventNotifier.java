package de.paffjeep.mrf.configuration.resilience.circuitebreaker.impl;

import com.netflix.hystrix.HystrixCommandKey;
import com.netflix.hystrix.HystrixEventType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Custom implementation of Hystrix event Notifier.
 *
 * @author Goran Jovic (ESG GmbH)
 *
 */
public class EventNotifier extends com.netflix.hystrix.strategy.eventnotifier.HystrixEventNotifier {

	private static final Logger LOGGER = LoggerFactory.getLogger(EventNotifier.class);

	/**
	 * Default constructor.
	 */
	public EventNotifier() {
		super();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.netflix.hystrix.strategy.eventnotifier.HystrixEventNotifier#markEvent
	 * (com.netflix.hystrix.HystrixEventType,
	 * com.netflix.hystrix.HystrixCommandKey)
	 */
	@Override
	public void markEvent(final HystrixEventType eventType, final HystrixCommandKey key) {
		// for monitoring purpose log ERROR message if circuit is open or
		// thread/semaphore execution is rejected else log WARN messages
        switch (eventType) {
		case SHORT_CIRCUITED:
			LOGGER.warn("Hystrix circuit breaker open for command {}", key);
			break;
		case THREAD_POOL_REJECTED:
		case SEMAPHORE_REJECTED:
			LOGGER.warn("Hystrix execution rejected for command {}", key);
			break;
		default:
			LOGGER.debug("Hystrix event for command {}: {}", key, eventType);
		}
	}

}
