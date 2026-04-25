
package com.smartcampus.resource;
import com.smartcampus.exception.RoomNotEmptyException;
import com.smartcampus.model.Room;
import com.smartcampus.store.DataStore;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.net.URI;
import java.util. ArrayList;
import java.util.LinkedHashMap;
import java.util.List; 
import java.util.Map;

@Path ("/rooms")
@Produces (MediaType.APPLICATION_JSON)
@Consumes (MediaType.APPLICATION_JSON)
public class RoomResource {
    

    private final DataStore store = DataStore.getInstance();

    @GET
    public Response getAllRooms() {
    List<Room> roomList = new ArrayList<> (store.getRooms().values());
    return Response.ok(roomList). build();}
    @POST
    public Response createRoom(Room room, @Context UriInfo uriInfo) {
    if (room == null || room.getId() == null
      ||room. getId(). trim().isEmpty()) {
    return Response.status (400)
        .entity(error (400, "BAD_REQUEST",
    "Room ID is required."))
        . build();
    }
    if (store.getRooms().containsKey(room.getId())) {
    return Response.status (409)
    .entity(error (409,"CONFLICT",
        "Room'" +room.getId() + "' already exists."))
    .build();
    }

    store.getRooms().put(room.getId(), room);
    URI location = uriInfo.getAbsolutePathBuilder ()
    .path(room.getId())
            .build();
    return Response.created(location).entity(room).build();
    }
    @GET
    @Path ("/{roomId}")
    public Response getRoom(@PathParam("roomId") String roomId) {
    Room room = store.getRooms ().get (roomId);
    if (room == null) {
    return Response.status (404)
    .entity(error (404, "NOT_FOUND",
    "Room not found: " + roomId))
     .build();
    }
    return Response.ok(room). build();
    }
    @DELETE
    @Path("/{roomId}")
    public Response deleteRoom(@PathParam("roomid") String roomid) {
    Room room = store.getRooms().get(roomid); 
    if (room == null) {
    return Response.status (404)
    .entity (error (404, "NOT_FOUND",
    "Room not found: " + roomid))
            .build();
    }
    if(!room.getSensorIds().isEmpty()) {
    throw new RoomNotEmptyException (
        "Room '" + roomid + "' cannot be deleted."
        + "It has " + room.getSensorIds().size()
        + " sensor(s) still assigned.");
    }
    store.getRooms().remove(roomid);
    return Response.noContent(). build();
    }

    private Map<String, Object> error(int status,
    String err, String msg) {
    Map<String, Object> map = new LinkedHashMap<>();
    map.put ("status", status);
    map.put("error", err);
    map. put ("message", msg);
    return map;}}