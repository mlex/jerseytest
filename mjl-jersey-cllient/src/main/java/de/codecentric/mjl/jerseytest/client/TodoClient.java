package de.codecentric.mjl.jerseytest.client;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse.Status;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;

import de.codecentric.mjl.jerseytest.exceptions.TodoNotFoundException;

public class TodoClient {

    public static final String TODO_RESOURCE_PATH = "/todo";

    private final String uri;

    private final Client client = new Client();

    public TodoClient(String uri) {
	this.uri = uri;
    }

    public WebResource resource() {
	return client.resource(uri).path(TODO_RESOURCE_PATH);
    }

    public WebResource resource(String todo) {
	return resource().path("/" + todo);
    }

    public String getAllTodos() {
	String todos = resource().get(String.class);
	return todos;
    }

    public void addTodo(String todoToAdd) {
	resource().post(todoToAdd);
    }

    public void removeTodo(String todoToRemove) {
	try {
	    resource(todoToRemove).delete();
	} catch (UniformInterfaceException e) {
	    if (e.getResponse().getClientResponseStatus() == Status.BAD_REQUEST
		    && "TodoNotFoundException".equals(e.getResponse().getEntity(String.class))) {
		throw new TodoNotFoundException("Todo '" + todoToRemove + "' not found");
	    } else {
		throw e;
	    }
	}
    }
}
