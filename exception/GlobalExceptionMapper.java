package com. smartcampus. exception;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider; 
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Logger;

@Provider
public class GlobalExceptionMapper
        implements ExceptionMapper<Throwable> {
    

private static final Logger logger
    = Logger.getLogger (GlobalExceptionMapper.class.getName ());

@Override
public Response toResponse (Throwable e) {
logger. severe ("Unexpected server error: " + e.getMessage());

Map<String, Object> body = new LinkedHashMap<>();
body.put ("status", 500);
body.put("error", "INTERNAL_SERVER_ERROR");
body.put ("message",
        "An unexpected error occurred. Please contact support.");
return Response. status (500)
.type(MediaType.APPLICATION_JSON)
 .entity (body)
.build(); }
}  