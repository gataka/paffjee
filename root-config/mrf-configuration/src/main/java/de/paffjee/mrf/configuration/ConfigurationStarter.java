package de.paffjee.mrf.configuration;

import org.apache.commons.configuration2.ConfigurationUtils;
import org.apache.commons.configuration2.ImmutableConfiguration;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.SystemConfiguration;
import org.apache.commons.configuration2.builder.fluent.Configurations;
import org.apache.commons.configuration2.ex.ConfigurationException;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.Annotated;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.inject.Singleton;
import java.io.Serializable;
import java.util.Iterator;

/**
 * Created by BEYH on 28.03.2017.
 */
@ApplicationScoped
@Singleton
public class ConfigurationStarter implements Serializable{
    private static final String GLOABAL_HUB = "com.bmw.global.env.hub";
    private static final String GLOABAL_ENVIRONMENT = "com.bmw.global.env.environment";
    private static final String GLOABAL_APPID = "com.bmw.global.env.appID";
    private static final String CONFIG_FILE_NAME_TEMPLATE = "%s-configuration.properties";

    private ImmutableConfiguration configurationInstance;

    /**
     * Default Constructor which loads the properties.
     */
    @PostConstruct
    public void init()  {

        try {

            final Configurations configs = new Configurations();
            // first load default properties
            PropertiesConfiguration config = new PropertiesConfiguration();
            config = configs.properties(String.format(CONFIG_FILE_NAME_TEMPLATE, "default"));

            SystemConfiguration systemConfiguration = new SystemConfiguration();
            PropertiesConfiguration bmwConfig = new PropertiesConfiguration();
            for (Iterator<String> iterator = systemConfiguration.getKeys("com.bmw"); iterator.hasNext();) {
                String key = iterator.next();
                bmwConfig.setProperty(key, systemConfiguration.getProperty(key));
            }
            config.copy(bmwConfig);

            config.copy(bmwConfig.subset(
                    String.format("com.bmw.%s.%s.%s", systemConfiguration.getString(GLOABAL_HUB, "NONE").toLowerCase(),
                            systemConfiguration.getString(GLOABAL_ENVIRONMENT, "CI").toLowerCase(),
                            systemConfiguration.getString(GLOABAL_APPID, "nothing").toLowerCase())));

            ConfigurationUtils.enableRuntimeExceptions(config);
            this.configurationInstance = ConfigurationUtils.unmodifiableConfiguration(config);

        } catch (final ConfigurationException e) {
            // Something went wrong
            throw new RuntimeException("Failed to initialise Configuration service", e);
        }

    }

    /**
     * @return the configurationInstance
     */
    public ImmutableConfiguration getConfiguration() {
        return this.configurationInstance;
    }

    @Produces
    @Value(type = String.class)
    public String produceStringPropertie(InjectionPoint injectionPoint)
    {
        final Annotated annotated = injectionPoint.getAnnotated();
        final Value value = annotated.getAnnotation(Value.class);
        return getConfiguration().getString(value.key(),value.defaultValue());
    }

    @Produces
    @Value(type = Integer.class)
    public Integer produceIntegerPropertie(InjectionPoint injectionPoint)
    {
        final Annotated annotated = injectionPoint.getAnnotated();
        final Value value = annotated.getAnnotation(Value.class);
        return getConfiguration().getInteger(value.key(),Integer.valueOf(value.defaultValue()));
    }
}
