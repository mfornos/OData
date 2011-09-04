package play.modules.odata;

import java.util.Properties;

import org.odata4j.producer.ODataProducer;
import org.odata4j.producer.ODataProducerFactory;
import org.odata4j.producer.jpa.JPAProducer;

import play.Play;
import play.db.jpa.JPA;

public class JPAProducerFactory implements ODataProducerFactory {

    @Override
    public ODataProducer create(final Properties props) {
        String namespace = Play.configuration.getProperty("odata.jpa.namespace", "Play");
        int maxResults = Integer.parseInt(Play.configuration.getProperty("odata.jpa.maxresults", "50"));

        JPAProducer producer = new JPAProducer(JPA.entityManagerFactory, namespace, maxResults);

        return producer;
    }
}
