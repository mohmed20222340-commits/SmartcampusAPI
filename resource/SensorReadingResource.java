package com.smartcampus.resource;

import com.smartcampus.exception.SensorUnavailableException;
import com. smartcampus.model.Sensor;
import com. smartcampus.model.SensorReading;
import com.smartcampus.store.DataStore;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Produces (MediaType.APPLICATION_JSON)
@Consumes (MediaType.APPLICATION_JSON)
public class SensorReadingResource{
        
private final String sensorId;
private final DataStore store = DataStore.getInstance();
public SensorReadingResource(String sensorId) {
this. sensorId = sensorId;
}

@GET
public Response getReadings() {
    Sensor sensor = store.getSensors().get(sensorId) ; 
    if (sensor == null) {
       return Response. status (404)
        .entity(error (404, "NOT_FOUND",
            "Sensor not found: " + sensorId))
        .build();
    }
       List<SensorReading> history = store.getReadings()
        .getOrDefault(sensorId, new ArrayList<>());
return Response.ok(history).build();
}

 @POST
public Response addReading(SensorReading reading) {
    Sensor sensor = store.getSensors().get(sensorId) ;
    if (sensor == null) {
      return Response.status (404)
        .entity(error (404, "NOT_FOUND",
        "Sensor not found: " + sensorId))
    .build();
}

    if ("MAINTENANCE".equalsIgnoreCase(sensor.getStatus())) {
        throw new SensorUnavailableException(
        "Sensor '" + sensorId
        + "' is currently under MAINTENANCE"
        + " and cannot accept new readings.");
    }

    if (reading == null) {
    return Response. status ( 400)
        .entity(error (400, "BAD _REQUEST",
            "Reading value is required."))
         .build();
    }

    SensorReading newReading = new SensorReading (reading.getValue());

    store.getReadings()
        .computeIfAbsent (sensorId, k -> new ArrayList())
        .add(newReading);

    sensor.setCurrentValue(newReading.getValue());
    
    return Response.status(201).entity(newReading).build();

    }
private Map<String, Object> error(int status,
          String err, String msg) {
        Map<String, Object> map = new LinkedHashMap<>() ;
        map. put ("status", status);
        map. put ("error", err); 
        map.put ("message", msg);
        return map;
    }
}