package de.paffjee.mrf.configuration.boot.starter.pub;

import javax.interceptor.InvocationContext;

/**
 * Created by BEYH on 27.03.2017.
 */
public interface BootStarter {

    public Object startup(InvocationContext context);
}
