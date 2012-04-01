package de.codecentric.mjl.jerseytest;

import javax.ws.rs.core.Context;
import javax.ws.rs.ext.Provider;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.internal.util.reflection.Whitebox;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.ClientResponse.Status;
import com.sun.jersey.api.core.ClassNamesResourceConfig;
import com.sun.jersey.spi.container.servlet.WebComponent;
import com.sun.jersey.spi.inject.SingletonTypeInjectableProvider;
import com.sun.jersey.test.framework.JerseyTest;
import com.sun.jersey.test.framework.WebAppDescriptor;
import com.sun.jersey.test.framework.spi.container.TestContainerFactory;
import com.sun.jersey.test.framework.spi.container.grizzly2.web.GrizzlyWebTestContainerFactory;

import de.codecentric.mjl.jerseytest.client.TodoClient;
import de.codecentric.mjl.jerseytest.exceptions.TodoNotFoundException;
import de.codecentric.mjl.jerseytest.providers.NotFoundMapper;
import de.codecentric.mjl.jerseytest.resources.TodoResource;
import de.codecentric.mjl.jerseytest.services.TodoService;

public class TodoResourceTest extends JerseyTest {

    public static TodoService todoServiceMock = Mockito.mock(TodoService.class);

    @Override
    public WebAppDescriptor configure() {
	return new WebAppDescriptor.Builder()
		.initParam(WebComponent.RESOURCE_CONFIG_CLASS,
			ClassNamesResourceConfig.class.getName())
		.initParam(
			ClassNamesResourceConfig.PROPERTY_CLASSNAMES,
			TodoResource.class.getName() + ";"
				+ MockTodoServiceProvider.class.getName() + ";"
				+ NotFoundMapper.class.getName()).build();
    }

    @Override
    public TestContainerFactory getTestContainerFactory() {
	return new GrizzlyWebTestContainerFactory();
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
	TodoClient todoClient = new TodoClient(getBaseURI().toString());
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