package cl.iplacex.procesador;

import cl.iplacex.procesador.translator.WebTranslator;
import cl.iplacex.procesador.translator.MarketplaceTranslator;
import jakarta.jms.*;
import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;
import java.io.InputStream;
import java.util.Properties;

public class Main {

    private static void main(String[] args) {
        Properties props = new Properties();

        try (InputStream is = Main.class.getClassLoader().getResourceAsStream("app.properties")) {
            if (is == null) throw new RuntimeException("no se encontró el archivo app.properties en resources")
            props.load(is);

            String brokerUrl    = props.getProperty("artemis.brokerUrl");
            String user         = props.getProperty("artemis.username");
            String pass         = props.getProperty("artemis.password");
            String topicName    = props.getProperty("topic.pedidos");
            String webQueueName = props.getProperty("queue.web");
            String mkpQueueName = props.getProperty("queue.mkp");

            // configuracion JMS
            ConnectionFactory cf = new ActiveMQConnectionFactory(brokerUrl, user, pass);
            Connection connection = cf.createConnection();
            Session session = connection.createSession(Session.AUTO_ACKNOWLEDGE);

            // Topic central
            Topic topicPedidos = session.createTopic(topicName);
            MessageProducer topicProducer = session.createProducer(topicPedidos);

            // CONSUMIDOR TIENDA WEB ( XML A TOPIC )
            Queue webQueue = session.createQueue(webQueueName);
            MessageConsumer webConsumer = session.createConsumer(webQueue);
            webConsumer.setMessageListener(new WebTranslator(session, topicProducer));

            // CONSUMIDOR MARKETPLACE ( JSON A TOPIC )
            Queue mkpQueue = session.createQueue(mkpQueueName);
            MessageConsumer mkpConsumer = session.createConsumer(mkpQueue);
            mkpConsumer.setMessageListener(new MarketplaceTranslator(session, topicProducer));

            connection.start();
            System.out.println("====================================================");
            System.out.println("Procesador de Pedidos (nva) iniciado correctamente.");
            System.out.println("====================================================");
            System.out.println("Escuchando Web en: " + webQueueName);
            System.out.println("Escuchando Mkp en: " + mkpQueueName);
            System.out.println("Publicando en Topic: " + topicName);
            System.out.println("====================================================");

            // Mantener la aplicacion corriendo
            Thread.currentThread().join();

        } catch (Exception e) {
            System.err.println("Error en el Procesador de Pedidos:");
            e.printStackTrace();
        }
    }
}