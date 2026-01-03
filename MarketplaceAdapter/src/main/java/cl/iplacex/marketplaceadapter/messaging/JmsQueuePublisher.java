package cl.iplacex.marketplaceadapter.messaging;

import jakarta.jms.*;
import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;


public class JmsQueuePublisher implements AutoCloseable {

    private final Connection connection;
    private final Session session;
    private final MessageProducer producer;

    public JmsQueuePublisher(String brokerUrl, String user, String pass, String queueName) {
        try {
            ConnectionFactory cf = new ActiveMQConnectionFactory(brokerUrl, user, pass);
            this.connection = cf.createConnection();
            this.session = connection.createSession(Session.AUTO_ACKNOWLEDGE);

            Queue queue = session.createQueue(queueName);
            this.producer = session.createProducer(queue);

            this.connection.start();
        } catch (JMSException e) {
            throw new RuntimeException("Error conectando a Artemis", e);
        }
    }

    public void sendText(String body) {
        try {
            TextMessage msg = session.createTextMessage(body);
            producer.send(msg);
        } catch (JMSException e) {
            throw new RuntimeException("Error enviando mensaje JMS", e);
        }
    }

    @Override
    public void close() {
        try { producer.close(); } catch (Exception ignored) {}
        try { session.close(); } catch (Exception ignored) {}
        try { connection.close(); } catch (Exception ignored) {}
    }
}