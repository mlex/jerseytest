package de.codecentric.mjl.jerseytest;

import javax.ws.rs.core.Context;
import javax.ws.rs.ext.Provider;

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.internal.util.reflection.Whitebox;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.ClientResponse.Status;
import com.sun.jersey.spi.inject.SingletonTypeInjectableProvider;

import de.codecentric.mjl.jerseytest.client.TodoClient;
import de.codecentric.mjl.jerseytest.exceptions.TodoNotFoundException;
import de.codecentric.mjl.jerseytest.helpers.FastJerseyTest;
import de.codecentric.mjl.jerseytest.helpers.FilteringInMemoryTestContainerFactory;
import de.codecentric.mjl.jerseytest.providers.NotFoundMapper;
import de.codecentric.mjl.jerseytest.resources.TodoResource;
import de.codecentric.mjl.jerseytest.services.TodoService;

public class FastTodoResourceTest extends FastJerseyTest {

    public static TodoService todoServiceMock = Mockito.mock(TodoService.class);

    @BeforeClass
    public static void configure() {
	addClass(TodoResource.class);
	addClass(NotFoundMapper.class);
	addProviderForContext(TodoService.class, todoServiceMock);
	setTestContainerFactory(new FilteringInMemoryTestContainerFactory());
    }

    @Before
    public void resetMocks() {
	// the mock is stored in a static field and must be reset manually before each test
	Mockito.reset(todoServiceMock);
    }

    @Test(expected = TodoNotFoundException.class)
    public void removeTodoShouldThrowNotFoundException() {
	final String todo = "test-todo";
	Mockito.doThrow(new TodoNotFoundException()).when(todoServiceMock).removeTodo(todo);
	todoClient().removeTodo(todo);
    }

    @Test
    public void shouldReturn400OnNotFoundException() {
	String todo = "test-todo";
	Mockito.doThrow(new TodoNotFoundException()).when(todoServiceMock).removeTodo(todo);
	ClientResponse response = resource().path("todo/" + todo).delete(ClientResponse.class);
	Assert.assertEquals(Status.BAD_REQUEST, response.getClientResponseStatus());
	Assert.assertEquals("TodoNotFoundException", response.getEntity(String.class));
    }

    private TodoClient todoClient() {
	TodoClient todoClient = new TodoClient(getBaseUri().toString());
	Whitebox.setInternalState(todoClient, "client", client());
	return todoClient;
    }

    @Provider
    public static class MockTodoServiceProvider extends
	    SingletonTypeInjectableProvider<Context, TodoService> {

	public MockTodoServiceProvider() {
	    super(TodoService.class, todoServiceMock);
	}
    }
}