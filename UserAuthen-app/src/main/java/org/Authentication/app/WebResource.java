package org.Authentication.app;
import javax.ws.rs.*;
import javax.ws.rs.core.*;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.onosproject.rest.AbstractWebResource;
import org.tinyradius.packet.AccessRequest;

@Path("UserCredential")
public class WebResource extends AbstractWebResource{

    /**
     * Get hello world greeting.
     * 
     * @return 200 OK
     */
    @GET
    public Response hello(){
        ObjectNode node = mapper().createObjectNode().put("HI","you GET it !");
        return ok(node).build();
    }
    @POST
    @Consumes("application/x-www-form-urlencoded")
    public String UserCredentialCheck(@FormParam("user")String user, 
    @FormParam("pass")String pass){
        Authenticator newUser = new Authenticator();
        AccessRequest myAr = newUser.createRequest(user, pass);
        String result = newUser.authWithRadius(myAr);
        return result;
    }
}