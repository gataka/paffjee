package de.paffjee.mrf.configuration;

import org.jglue.cdiunit.AdditionalClasses;
import org.jglue.cdiunit.AdditionalClasspaths;
import org.jglue.cdiunit.AdditionalPackages;
import org.jglue.cdiunit.CdiRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;

import static junit.framework.TestCase.assertEquals;

/**
 * Created by BEYH on 28.03.2017.
 */
@RunWith(CdiRunner.class)
@AdditionalClasses({ConfigurationStarter.class})
public class TestConfigurationStarter {

    @Inject
    private ConfigurationStarter starter;

    @Inject
    @Value(type=String.class,key = "test.string.value")
    private String testStringvalue;

    @Inject
    @Value(type = Integer.class, key = "test.integer.value")
    private Integer testIntegerValue;

    @Test
    public void testConfigInject()
    {
        assertEquals("test", testStringvalue);
        assertEquals(123, testIntegerValue.intValue());



    }
}
