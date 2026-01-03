package cl.iplacex.tiendaweb.ext.carrito.observer;

import cl.iplacex.tiendaweb.ext.carrito.domain.LineaPedidoImpl;
import cl.iplacex.tiendaweb.ext.carrito.domain.Pedido;
import cl.iplacex.tiendaweb.ext.carrito.event.PedidoCompletadoEvent;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;

import jakarta.jms.*;
import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;
import java.io.StringWriter;

// @Component
// public class PedidoCompletadoListener {
//     private final Logger logger = LoggerFactory.getLogger(PedidoCompletadoListener.class);

//     @EventListener
//     public void gestionarPedidoCompletado(PedidoCompletadoEvent event) {
//         var pedido = event.getPedido();
//         try {
//             JAXBContext context = JAXBContext.newInstance(Pedido.class, LineaPedidoImpl.class);
//             Marshaller marshaller = context.createMarshaller();
//             marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
//             StringWriter writer = new StringWriter();
//             marshaller.marshal(pedido, writer);
//             var xml = writer.toString();
//             logger.info("Pedido recibido como evento: {}", xml);
//         } catch (JAXBException e) {
//             logger.error("Error al convertir a XML", e);
//             throw new RuntimeException(e);
//         }
//     }
// }

@Component
public class PedidoCompletadoListener {

    // Configuracion del Broker
    @Value("${artemis.broker-url}")
    private String brokerUrl;

    @Value("${artemis.user}")
    private String user;
    
    @Value("${artemis.password}")
    private String password;

    @Value("${artemis.queue-web}")
    private String queueName;

    @EventListener
    public void handlePedidoCompletado(PedidoCompletadoEvent event) {
        Pedido pedido = event.getPedido();
        
        try {
            // Convertir el pedido a XML
            JAXBContext context = JAXBContext.newInstance(Pedido.class, LineaPedidoImpl.class);
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            StringWriter writer = new StringWriter();
            marshaller.marshal(pedido, writer);
            String xmlContent = writer.toString();

            System.out.println("--- XML Generado para Tienda Web ---");
            System.out.println(xmlContent);

            // Enviar el XML al broker de mensajeria
            enviarMensajeria(xmlContent);

        } catch (Exception e) {
            System.err.println("Error al procesar el pedido para mensajeria: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void enviarMensajeria(String xml) {
        try (ActiveMQConnectionFactory cf = new ActiveMQConnectionFactory(this.brokerUrl, this.user, this.password);
            Connection connection = cf.createConnection();
            Session session = connection.createSession(Session.AUTO_ACKNOWLEDGE)) {

            Queue queue = session.createQueue(this.queueName);
            MessageProducer producer = session.createProducer(queue);
            TextMessage message = session.createTextMessage(xml);
            producer.send(message);
            System.out.println("Pedido enviado exitosamente a la cola: " + this.queueName);
    
        } catch (Exception e) {
            System.err.println("Error enviando a Artemis: " + e.getMessage());
        }
    }
}
