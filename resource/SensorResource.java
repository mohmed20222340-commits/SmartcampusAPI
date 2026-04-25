 package com. smartcampus. resource;
import com.smartcampus.exception.LinkedResourceNotFoundException;
import com.smartcampus.model.Room; 
import com.smartcampus.model.Sensor;
import com.smartcampus.store.DataStore;
 
import javax.ws.rs.*;
import javax.ws.rs.core.*; 
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
 
@Path ("/sensors")
@Produces (MediaType.APPLICATION_JSON)
@Consumes (MediaType.APPLICATION_JSON)
public class SensorResource {


    private final DataStore store = DataStore.getInstance();
     @GET
    public Response getSensors (@QueryParam ("type") String type) {
        List<Sensor> result = store.getSensors().values().stream()
         .filter(s -> type == null
                  || s.getType().equalsIgnoreCase(type))
        .collect(Collectors.toList());
        return Response.ok(result).build();
    }

    @POST
        public Response createSensor (Sensor sensor) {
        if (sensor == null) {
        return Response. status (400)
            .entity(error (400, "BAD_REQUEST",
        "Request body is empty."))
        .build();

    
    }
    if (sensor.getId() == null || sensor.getId().trim().isEmpty()) {
    return Response. status(400)
    .entity(error (400, "PAD_REQUEST",
    "Sensor ID is required."))
     .build();
    }

    if (sensor.getRoomId() == null
           || !store.getRooms ().containsKey(sensor.getRoomId())) {
    throw new LinkedResourceNotFoundException (
          "Room with ID '" + sensor.getRoomId()
            + "' does not exist in the system.");

    }

    if(store.getSensors().containsKey(sensor.getId())) {
    return Response.status (409)
        .entity(error (409, "CONFLICT",
          "Sensor '" + sensor.getId()
           + "' already exists."))
         .build();
    }

    if (sensor.getStatus()== null
          ||sensor.getStatus().trim().isEmpty()) {
      sensor. setStatus ("ACTIVE");
    }
    store.getSensors().put(sensor.getId(), sensor);
        Room room = store.getRooms().get(sensor.getRoomId()) ;
        room.getSensorIds().add(sensor.getId());
 
        return Response. status (201). entity(sensor).build();
    }

    @GET
    @Path ("/{sensorId}")
    public Response getSensor(@PathParam("sensorId") String sensorId) {
         Sensor sensor = store.getSensors().get(sensorId);
         if (sensor ==null) {
            return Response. status (404)
                .entity (error (404, "NOT_FOUND",
                    "Sensor not found: " + sensorId))
                .build();
         }
        return Response.ok(sensor). build();
    }

    @Path("/{sensor Id}/readings")
    public SensorReadingResource getReadingResource(
        @PathParam("sensorId") String sensorId) {
      return new SensorReadingResource(sensorId);
    }

   private Map<String, Object> error (int status, 
                String err, String msg){
        Map<String, Object> map = new LinkedHashMap<>();
        map. put("status", status);
        map. put("error", err); 
        map. put ("message", msg);
        return map;
    }
}