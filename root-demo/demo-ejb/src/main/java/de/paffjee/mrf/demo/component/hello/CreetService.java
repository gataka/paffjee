package de.paffjee.mrf.demo.component.hello;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by philippbeyerlein on 12.07.17.
 */
@RequestScoped
public class CreetService {

    public static final String FORMAT_PATTERN = " %s";

    @Produces
    @Creeting
    public Creet generateDefaultCreet(InjectionPoint injectionPoint)
    {
        return new Creet(getCreetingFromInjectionPoint(injectionPoint));

    }


    private String getCreetingFromInjectionPoint(InjectionPoint injectionPoint) {
        StringBuilder builder = new StringBuilder();

        Creeting qualifiersCreeting =  injectionPoint.getAnnotated().getAnnotation(Creeting.class);
        builder.append(qualifiersCreeting.value()).append(FORMAT_PATTERN);
        return builder.toString();
    }
}
