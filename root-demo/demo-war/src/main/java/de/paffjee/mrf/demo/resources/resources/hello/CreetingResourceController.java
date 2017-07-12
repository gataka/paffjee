package de.paffjee.mrf.demo.resources.resources.hello;

import de.paffjee.mrf.demo.component.hello.Creet;
import de.paffjee.mrf.demo.component.hello.Creeting;

import javax.enterprise.inject.spi.*;
import javax.enterprise.util.AnnotationLiteral;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Created by philippbeyerlein on 11.07.17.
 */
@Path("/creeting")
public class CreetingResourceController {

    private final Creet creetHello;
    private BeanManager manager;

    @Inject
    public CreetingResourceController(@Creeting Creet creetHello)
    {
        this.creetHello = creetHello;
        this.manager = manager;
    }

    @GET
    @Path("/hello/{name}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getHello(@PathParam("name") String name){
        CreetingResource hello = new CreetingResource();
        hello.setCreeting(String.format(creetHello.getCreetingFormula(), name));
        return Response.ok(hello).build();
    }

}
