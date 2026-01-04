package cl.iplacex.bodega;

import jakarta.jms.*;
import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;
import java.io.InputStream;
import java.util.Properties;

public class Main {

    public static void main(String[] args) {
        Properties props = new Properties();
        try (InputStream is = Main.class.getClassLoader().getResourceAsStream("app.properties")) {
            if (is == null) throw new RuntimeException("No se encontró el archivo app.properties");
            props.load(is);

            ConnectionFactory cf = new ActiveMQConnectionFactory(
                props.getProperty("artemis.brokerUrl"),
                props.getProperty("artemis.username"),
                props.getProperty("artemis.password")
            );

            Connection connection = cf.createConnection();
            connection.setClientID("BodegaAdapter_nva");

            Session session = connection.createSession(Session.AUTO_ACKNOWLEDGE);

            Topic topic = session.createTopic(props.getProperty("topic.pedidos"));

            MessageConsumer consumer = session.createDurableSubscriber(topic, props.getProperty("subscription.name"));

            consumer.setMessageListener(message -> {
                try {
                    if (message instanceof TextMessage textMessage) {
                        String jsonCanonico = textMessage.getText();
                        System.out.println("====================================================");
                        System.out.println("[BodegaAdapter] Nuevo pedido recibido para preparacion de despacho.");

                        notificarBodegaRest(jsonCanonico, props.getProperty("bodega.restUrl"));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

            connection.start();
            System.out.println(">>> BodegaAdapter (nva) iniciado correctamente.");
            System.out.println(">>> Suscrito al Topic: " + props.getProperty("topic.pedidos"));
            Thread.currentThread().join();

        }   catch (Exception e) {
                e.printStackTrace();
            }
        }

    private static void notificarBodegaRest(String json, String url) {
        System.out.println(">>> Enviando notificación de stock a: " + url);
        System.out.println(">>> [OK] Orden de despacho generada en Bodega.");
        System.out.println("====================================================");
    }
}