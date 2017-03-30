package de.paffjee.mrf.configuration.boot.starter.impl;

import de.paffjee.mrf.configuration.boot.starter.pub.BootStarter;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;

/**
 * Created by BEYH on 27.03.2017.
 */
@Singleton
public class StarterResolver {

   private List<BootStarter> starters;

   @Inject
   public StarterResolver(final List<BootStarter> starters)
   {
       this.starters = starters;
   }

    public List<BootStarter> getStarters() {
        return starters;
    }

    public void setStarters(final List<BootStarter> starters) {
        this.starters = starters;
    }
}
