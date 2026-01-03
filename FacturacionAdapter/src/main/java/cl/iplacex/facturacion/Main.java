package cl.iplacex.facturacion;

import jakarta.jms.*;
import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;
import java.io.InputStream;
import java.util.Properties;

public class Main {

    public static void main(String[] args) {
        Properties props = new Properties();
        try (InputStream is = Main.class.getClassLoader().getResourceAsStream("app.properties")) {
            if (is == null) {
                System.err.println("No se encontró el archivo app.properties");
                return;
            }
            props.load(is);

            // configuracion del Broker
            ConnectionFactory cf = new ActiveMQConnectionFactory(
                props.getProperty("artemis.brokerUrl"),
                props.getProperty("artemis.username"),
                props.getProperty("artemis.password")
            );

            Connection connection = cf.createConnection();
            connection.setClientID("FacturacionAdapter_nva");

            Session session = connection.createSession(Session.AUTO_ACKNOWLEDGE);

            // configurar el Topic y la suscripcion durable
            Topic topic = session.createTopic(props.getProperty("topic.pedidos"));
            MessageConsumer consumer = session.createDurableSubscriber(topic, props.getProperty("subscription.name"));

            consumer.setMessageListener(message -> {
                try {
                    if (message instanceof TextMessage textMessage) {
                        String jsonCanonico = textMessage.getText();
                        System.out.println("====================================================");
                        System.out.println("[FacturacionAdapter] Nuevo pedido recibido para procesar.");

                        enviarAFacturacionSoap(jsonCanonico, props.getProperty("facturacion.soapUrl"));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

            connection.start();
            System.out.println("FacturacionAdapter (nva) está iniciado. Esperando pedidos del Topic");
            Thread.currentThread().join();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void enviarAFacturacionSoap(String json, String url) {
        System.out.println(">>> Transformando JSON Canónico a formato SOAP...");
        System.out.println(">>> Invocando servicio en: " + url);
        System.out.println(">>> [OK] Factura generada exitosamente.");
        System.out.println("====================================================");
    }
}