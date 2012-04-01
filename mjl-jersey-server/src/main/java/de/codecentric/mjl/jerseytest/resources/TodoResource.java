package de.codecentric.mjl.jerseytest.resources;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;

import org.apache.commons.lang3.StringUtils;

import de.codecentric.mjl.jerseytest.services.TodoService;

/**
 * Example resource class hosted at the URI path "/myresource"
 */
@Path("/todo")
public class TodoResource {

    @Context
    private TodoService todoService;

    @GET
    @Produces("text/plain")
    public String getTodos() {
	return StringUtils.join(todoService.getAllTodos(), ",");
    }

    @POST
    @Consumes("text/plain")
    public void addTodo(String newTodo) {
	todoService.addTodo(newTodo);
    }

    @DELETE
    @Path("/{todo}")
    public void removeTodo(@PathParam("todo") String todoToRemove) {
	todoService.removeTodo(todoToRemove);
    }

}
