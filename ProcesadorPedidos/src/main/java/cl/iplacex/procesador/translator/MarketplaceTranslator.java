package cl.iplacex.procesador.translator;

import cl.iplacex.procesador.model.CanonicalOrder;
import com.google.gson.Gson;
import jakarta.jms.*;

public class MarketplaceTranslator implements MessageListener {
    private final Session session;
    private final MessageProducer producer;
    private final Gson gson = new Gson();

    public MarketplaceTranslator(Session session, MessageProducer producer) {
        this.session = session;
        this.producer = producer;
    }

    @Override
    public void onMessage(Message message) {
        try {
            if (message instanceof TextMessage textMessage ) {
                String jsonRecibido = textMessage.getText();
                System.out.println("[MarketplaceTranslator] Recibido mensaje JSON desde Marketplace");

                // convertir JSON recibido a CanonicalOrder
                CanonicalOrder canonical = gson.fromJson(jsonRecibido, CanonicalOrder.class);

                canonical.origen = "mkp";

                // enviar al topic nva_pedidos
                TextMessage outMessage = session.createTextMessage(gson.toJson(canonical));
                producer.send(outMessage);
                System.out.println("[MarketplaceTranslator] Pedido unificado y enviado al Topic nva_pedidos");
            }

        } catch (Exception e) {
            System.err.println("Error en MarketplaceTranslator: " + e.getMessage());
        }
    }
}
