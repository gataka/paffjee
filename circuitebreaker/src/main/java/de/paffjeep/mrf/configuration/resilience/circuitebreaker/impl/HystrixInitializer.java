package de.paffjeep.mrf.configuration.resilience.circuitebreaker.impl;

import com.netflix.config.ConfigurationManager;
import com.netflix.hystrix.contrib.servopublisher.HystrixServoMetricsPublisher;
import com.netflix.hystrix.strategy.HystrixPlugins;
import de.paffjeep.mrf.configuration.resilience.circuitebreaker.ResilienceCBconfiguration;
import org.apache.commons.configuration.MapConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Startup class for Hystrix initialization and configuration.
 * 
 * @author Goran Jovic (ESG GmbH)
 *
 */
@Startup
@Singleton
public class HystrixInitializer {

	private static final Logger LOGGER = LoggerFactory.getLogger(HystrixInitializer.class);

    @Inject
    private ResilienceCBconfiguration configuration;

    @Inject
    private ConcurrencyStrategy concurrencyStrategy;

	/**
	 * Default constructor.
	 */
	public HystrixInitializer() {

	}

	/**
	 * Init method.
	 */
	@PostConstruct
	public void postConstruct() {
		try {
			HystrixPlugins.reset();

			final Map<String, Object> map = new HashMap<>();

			for (final Iterator<String> keys = configuration.getConfiguration().getKeys(); keys.hasNext();) {
				final String key = keys.next();
				final Object value = configuration.getConfiguration().getProperty(key);

				map.put(key.replaceFirst("resilience", "hystrix"), value);
			}
			ConfigurationManager.install(new MapConfiguration(map));
            HystrixPlugins.getInstance().registerConcurrencyStrategy(concurrencyStrategy);
            HystrixPlugins.getInstance().registerEventNotifier(new EventNotifier());
			HystrixPlugins.getInstance().registerMetricsPublisher(HystrixServoMetricsPublisher.getInstance());
			LOGGER.info("Hystrix initialized successfully.");
		} finally {
			MDC.clear();
		}
	}

	/**
	 * Destroy method.
	 */
	@PreDestroy
	public void preDestroy() {
		HystrixPlugins.reset();
	}

}
