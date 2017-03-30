package de.paffjee.mrf.configuration.boot.starter.impl;

import de.paffjee.mrf.configuration.boot.starter.EnableStarterApplication;

import javax.inject.Inject;
import javax.interceptor.AroundConstruct;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;

/**
 * Created by BEYH on 27.03.2017.
 */
@Interceptor
@EnableStarterApplication
public class StarterApplicationInterceptor {


    private StarterResolver resolver;

    @Inject
    public StarterApplicationInterceptor(final StarterResolver resolver)
    {
        this.resolver = resolver;
    }

    @AroundConstruct
    public Object invokeConstructor(InvocationContext context) throws Exception {

        return context.proceed();
    }
}
