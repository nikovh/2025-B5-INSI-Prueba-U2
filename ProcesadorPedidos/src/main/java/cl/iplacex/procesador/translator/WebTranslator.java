package cl.iplacex.procesador.translator;

import cl.iplacex.procesador.model.CanonicalOrder;
import com.google.gson.Gson;
import jakarta.jms.*;
import java.time.OffsetDateTime;
import java.util.UUID;

public class WebTranslator implements MessageListener {
    private final Session session;
    private final MessageProducer producer;
    private final Gson gson = new Gson();

    public WebTranslator(Session session, MessageProducer producer) {
        this.session = session;
        this.producer = producer;
    }

    @Override
    public void onMessage(Message message) {
        try {
            if (message instanceof TextMessage textMessage ) {
                String xml = textMessage.getText();
                System.out.println("[WebTranslator] Recibido mensaje XML desde TiendaWeb");
                
                CanonicalOrder canonical =  new CanonicalOrder();
                canonical.idPedido = UUID.randomUUID().toString();
                canonical.origen = "web";
                canonical.fecha = OffsetDateTime.now().toString();

                // metadatos
                canonical.metadatos = new CanonicalOrder.Metadatos();
                canonical.metadatos.canal = "web";
                canonical.metadatos.traceId = UUID.randomUUID().toString();
                canonical.metadatos.timestampIngesta = OffsetDateTime.now().toString();

                // envviar al topic nva_pedidos
                TextMessage outMessage = session.createTextMessage(gson.toJson(canonical));
                producer.send(outMessage);
                System.out.println("[WebTranslator] Pedido enviado al Topic nva_peidods");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}