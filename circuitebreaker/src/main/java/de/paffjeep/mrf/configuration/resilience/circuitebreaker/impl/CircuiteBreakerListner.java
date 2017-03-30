package de.paffjeep.mrf.configuration.resilience.circuitebreaker.impl;

/**
 * Created by BEYH on 27.03.2017.
 */
public interface CircuiteBreakerListner {

    public void commandFound(String commandGroup,String commandName);

}
