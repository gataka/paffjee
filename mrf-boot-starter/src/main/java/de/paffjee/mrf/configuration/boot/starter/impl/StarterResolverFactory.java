package de.paffjee.mrf.configuration.boot.starter.impl;

import de.paffjee.mrf.configuration.boot.starter.pub.BootStarter;
import de.paffjee.mrf.configuration.boot.starter.pub.BootSupport;

import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.util.AnnotationLiteral;
import javax.inject.Inject;
import javax.naming.NamingException;
import java.util.List;
import java.util.Set;

/**
 * Created by BEYH on 27.03.2017.
 */
public class StarterResolverFactory {

    private BeanManager manager;

    @Inject
    public StarterResolverFactory(final BeanManager manager) {
        this.manager = manager;
    }

    @Produces
    public List<BootStarter> findStarters() throws NamingException {
        final Set<Bean<?>> beans = manager.getBeans(BootStarter.class, new AnnotationLiteral<BootSupport>() {
        });
        for (Bean<?> bean : beans) {
            final BootStarter instance = (BootStarter) manager
                    .getReference(bean, BootStarter.class, manager.createCreationalContext(bean));
        }

        return null;
    }
}
