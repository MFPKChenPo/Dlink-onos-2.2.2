package org.Authentication.app;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.*;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.onosproject.rest.AbstractWebResource;
import org.tinyradius.packet.AccessRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("UserCredential")
public class WebResource extends AbstractWebResource{

    private static HashMap<String, Boolean> userMap = new HashMap<String, Boolean>();
    private final Logger log = LoggerFactory.getLogger(getClass());
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

    @POST
    @Path("/insertNewUser")
    @Consumes("application/x-www-form-urlencoded")
    public int insertNewUser(@FormParam("ip")String newIp)
    {
        if(userMap.get(newIp) == null)
        {
            userMap.put(newIp, true);
            return 0;
        }
        return -1;
    }

    @POST
    @Path("/getUser")
    @Consumes("application/x-www-form-urlencoded")
    public boolean getUser(@FormParam("ip")String newIp)
    {
        log.info(newIp);
        if(userMap.get(newIp) != null)
            return true;
        else
            return false;
    }
}