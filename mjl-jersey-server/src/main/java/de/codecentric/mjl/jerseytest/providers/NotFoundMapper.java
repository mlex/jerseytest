package de.codecentric.mjl.jerseytest.providers;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import de.codecentric.mjl.jerseytest.exceptions.TodoNotFoundException;

@Provider
public class NotFoundMapper implements ExceptionMapper<TodoNotFoundException> {
    @Override
    public Response toResponse(TodoNotFoundException e) {
	return Response.status(Response.Status.BAD_REQUEST).entity("TodoNotFoundException").build();
    }
}
