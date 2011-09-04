package play.modules.odata;

import org.odata4j.producer.resources.ODataProducerProvider;

import play.Logger;
import play.Play;
import play.PlayPlugin;
import play.exceptions.UnexpectedException;
import play.modules.odata.auth.Authenticator;
import play.modules.odata.auth.ODataSecurity;

import com.sun.jersey.api.container.ContainerFactory;
import com.sun.jersey.api.core.ClasspathResourceConfig;
import com.sun.jersey.api.core.ResourceConfig;

public class ODataPlugin extends PlayPlugin {

    public static PlayContainer container;
    public static String contextPath;
    private boolean started;

    @Override
    public void onApplicationStart() {
        try {
            if (!started) {
                Logger.info("Creating Jersey Play! container");
                createContainer();
                started = true;
                Logger.info("Jersey container created");
            } else if (Play.mode != Play.Mode.PROD) {
                createContainer();
            }
        } catch (Throwable t) {
            throw new UnexpectedException(t);
        }
    }

    @Override
    public void onConfigurationRead() {
        contextPath = Play.configuration.getProperty("odata.context.path", "/OData/");

        String producerClassName = Play.configuration.getProperty("odata.producer.class",
                JPAProducerFactory.class.getName());
        Logger.info("Setting ODataProduderProvider: %s", producerClassName);
        System.setProperty(ODataProducerProvider.FACTORY_PROPNAME, producerClassName);
    }

    private void createAuthenticator() throws InstantiationException, IllegalAccessException, ClassNotFoundException {
        String authClass = Play.configuration.getProperty("odata.authenticator.class");
        if (authClass != null) {
            Logger.info("Binding Authenticator: ", authClass);
            ODataSecurity.authenticator = (Authenticator) Class.forName(authClass).newInstance();
        }
    }

    private void createContainer() throws InstantiationException, IllegalAccessException, ClassNotFoundException {
        createAuthenticator();
        ResourceConfig config = createResourceConfig();
        container = ContainerFactory.createContainer(PlayContainer.class, config);
    }

    private ResourceConfig createResourceConfig() throws InstantiationException, IllegalAccessException,
            ClassNotFoundException {
        String rConfName = Play.configuration.getProperty("odata.resource.config.class",
                ClasspathResourceConfig.class.getName());
        Logger.info("Instantiating ResourceConfig: %s", rConfName);
        return (ResourceConfig) Class.forName(rConfName).newInstance();
    }

}
