
package com. smartcampus.resource;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType; 
import javax.ws.rs.core.Response;
import java.util.LinkedHashMap;
import java.util.Map;

@Path ("/")
@Produces (MediaType.APPLICATION_JSON)
public class DiscoveryResource {
    
    @GET
public Response discover () {
    

Map<String, Object> info = new LinkedHashMap();
info.put ("api", "Smart Campus API"); 
info.put ("version", "1.0.0"); 
info.put ("contact", "admin@smartcampus.ac.uk"); 
info.put ("description",
"RESTfUl API for Room ana Sensor Management");
Map<String, String> links = new LinkedHashMap<>();
links.put ("rooms", "/SmartCampusAPI/api/v1/rooms");
links.put ("sensors", "/SmartCampusAPI/api/v1/sensors"); 
info.put ("resources", links);
return Response.ok(info).build();
}
}