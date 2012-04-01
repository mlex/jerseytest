package de.codecentric.mjl.jerseytest.providers;

import javax.ws.rs.core.Context;
import javax.ws.rs.ext.Provider;

import com.sun.jersey.spi.inject.SingletonTypeInjectableProvider;

import de.codecentric.mjl.jerseytest.services.TodoService;

@Provider
public class TodoServiceProvider extends SingletonTypeInjectableProvider<Context, TodoService> {

    public TodoServiceProvider() {
	super(TodoService.class, new TodoService());
    }

}
