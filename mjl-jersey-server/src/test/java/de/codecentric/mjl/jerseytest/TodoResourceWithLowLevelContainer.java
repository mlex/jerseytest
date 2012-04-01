package de.codecentric.mjl.jerseytest;

import javax.ws.rs.core.Context;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.internal.util.reflection.Whitebox;

import com.sun.jersey.api.core.DefaultResourceConfig;
import com.sun.jersey.spi.inject.SingletonTypeInjectableProvider;
import com.sun.jersey.test.framework.JerseyTest;
import com.sun.jersey.test.framework.LowLevelAppDescriptor;
import com.sun.jersey.test.framework.spi.container.TestContainerFactory;
import com.sun.jersey.test.framework.spi.container.grizzly2.GrizzlyTestContainerFactory;

import de.codecentric.mjl.jerseytest.client.TodoClient;
import de.codecentric.mjl.jerseytest.exceptions.TodoNotFoundException;
import de.codecentric.mjl.jerseytest.providers.NotFoundMapper;
import de.codecentric.mjl.jerseytest.resources.TodoResource;
import de.codecentric.mjl.jerseytest.services.TodoService;

public class TodoResourceWithLowLevelContainer extends JerseyTest {

    public static TodoService todoServiceMock = Mockito.mock(TodoService.class);

    @Override
    public LowLevelAppDescriptor configure() {
	DefaultResourceConfig resourceConfig = new DefaultResourceConfig();
	resourceConfig.getSingletons().add(
		new SingletonTypeInjectableProvider<Context, TodoService>(TodoService.class,
			todoServiceMock) {
		});
	resourceConfig.getClasses().add(NotFoundMapper.class);
	resourceConfig.getClasses().add(TodoResource.class);
	return new LowLevelAppDescriptor.Builder(resourceConfig).build();
    }

    @Override
    public TestContainerFactory getTestContainerFactory() {
	return new GrizzlyTestContainerFactory();
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

    private TodoClient todoClient() {
	TodoClient todoClient = new TodoClient(getBaseURI().toString());
	Whitebox.setInternalState(todoClient, "client", client());
	return todoClient;
    }
}
