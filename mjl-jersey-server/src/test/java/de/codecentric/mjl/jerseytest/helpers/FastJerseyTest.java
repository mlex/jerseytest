package de.codecentric.mjl.jerseytest.helpers;

import java.net.URI;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriBuilder;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.core.DefaultResourceConfig;
import com.sun.jersey.spi.inject.SingletonTypeInjectableProvider;
import com.sun.jersey.test.framework.AppDescriptor;
import com.sun.jersey.test.framework.LowLevelAppDescriptor;
import com.sun.jersey.test.framework.spi.container.TestContainer;
import com.sun.jersey.test.framework.spi.container.TestContainerFactory;

/**
 * An abstract JUnit 4.x-based unit test class for testing JAX-RS and Jersey-based applications
 * inspired by the JerseyTest class of the jersey test framework.
 * 
 * LowLevelJerseyTest differs from JerseyTest in that only low-level test-containsers can be used.
 * Taking advantage of this restriction, LowLevelJerseyTest allows easy mocking of objects injected
 * via @Context and on-the-fly configuration of request- and response-filters. Another difference to
 * JerseyTest is, that the test-container is only started once per test-suite (and not once per
 * test). Although this contradicts with the rule of absolute isolation of test-cases, the
 * performance gain outweighs this shortcoming.
 * 
 * The test-container has to be configured in methods annotated with @BeforeClass using the static
 * methods {@link #addClass}, {@link #addSingleton}, {@link #addFilter},
 * {@link #addProviderForContext}, and so on. Note, that the test-container is initialized and start
 * before any @Before annotated methods are executed. So calling any of the above methods in @Before
 * annotated methods won't have any effect.
 * 
 * The method {@link #resource()} returns a {@link WebResource}, that is already configured to point
 * to the root path of the test-container.
 * 
 * @author Michael Lex <michael.lex@codecentric.de>
 */
abstract public class FastJerseyTest {

    private static DefaultResourceConfig resourceConfig = new DefaultResourceConfig();

    private static TestContainerFactory testContainerFactory;

    private static TestContainer testContainer;

    private static Client client;

    public static void addClass(Class<?> resourceClass) {
	resourceConfig.getClasses().add(resourceClass);
    }

    public static void addSingleton(Object resourceSingleton) {
	resourceConfig.getSingletons().add(resourceSingleton);
    }

    public static <T> void addProviderForContext(Class<T> contextClass, T contextObject) {
	addSingleton(new SingletonTypeInjectableProvider<Context, T>(contextClass, contextObject) {
	});
    }

    public static void addRequestFilter(Object filter) {
	resourceConfig.getContainerRequestFilters().add(filter);
    }

    public static void addResponseFilter(Object filter) {
	resourceConfig.getContainerResponseFilters().add(filter);
    }

    public static void setTestContainerFactory(TestContainerFactory newTestContainerFactory) {
	testContainerFactory = newTestContainerFactory;
    }

    @BeforeClass
    public static void cleanStaticVariables() {
	resourceConfig = new DefaultResourceConfig();
    }

    public static void initServer() {
	AppDescriptor ad = new LowLevelAppDescriptor.Builder(resourceConfig).build();
	TestContainerFactory tcf = testContainerFactory;
	if (tcf == null) {
	    tcf = new FilteringInMemoryTestContainerFactory();
	}
	testContainer = tcf.create(UriBuilder.fromUri("http://localhost/").port(9998).build(), ad);
	client = testContainer.getClient();
	if (client == null) {
	    client = Client.create(ad.getClientConfig());
	}
    }

    public static void startServer() {
	if (testContainer != null) {
	    testContainer.start();
	}
    }

    @AfterClass
    public static void stopServer() {
	testContainer.stop();
	testContainer = null;
	client = null;
    }

    public Client client() {
	return client;
    }

    public URI getBaseUri() {
	return testContainer.getBaseUri();
    }

    public WebResource resource() {
	return client.resource(getBaseUri());
    }

    @Before
    public void startServerBeforeFirstTestRun() {
	if (testContainer == null) {
	    initServer();
	    startServer();
	}
    }
}
