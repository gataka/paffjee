package de.paffjeep.mrf.configuration.resilience.circuitebreaker;

import de.paffjeep.mrf.configuration.resilience.circuitebreaker.impl.CircuiteBreakerListner;

import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Singleton;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by BEYH on 27.03.2017.
 */
@Singleton
public class CircuiteBreakerMonitor implements CircuiteBreakerListner {

    private ConcurrentMap<String,String> commandMap;

    public CircuiteBreakerMonitor()
    {
        commandMap = new ConcurrentHashMap<>();
    }

    public void init()
    {

    }

    @Lock(LockType.READ)
    @Override
    public void commandFound(final String commandGroup, final String commandName) {
        if(!commandMap.containsKey(commandName))
        {
            addCommand(commandGroup, commandName);
        }
    }
    @Lock(LockType.WRITE)
    public void addCommand(final String commandGroup, final String commandName)
    {
        commandMap.putIfAbsent(commandName,commandGroup);
    }

    public void getMonitor()
    {
        //TODO when monitoring module exists.
    }
}
