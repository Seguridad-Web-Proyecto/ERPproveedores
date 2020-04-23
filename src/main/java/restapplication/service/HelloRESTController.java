/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package restapplication.service;

/**
 *
 * @author jcami
 */

import javax.ejb.Stateless;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Stateless
@Path("")
public class HelloRESTController {
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String hello() {
        return "Hola!. Esta es la ruta de servicios web!!!";
    }
    
}
