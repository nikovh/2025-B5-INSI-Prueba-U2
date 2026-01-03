package cl.iplacex.marketplaceadapter;

import cl.iplacex.marketplaceadapter.messaging.JmsQueuePublisher;
import cl.iplacex.marketplaceadapter.model.CanonicalOrder;
import cl.iplacex.marketplaceadapter.model.MarketplaceOrder;
import cl.iplacex.marketplaceadapter.service.MarketplaceClient;
import cl.iplacex.marketplaceadapter.service.MarketplaceToCanonicalMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.InputStream;
import java.util.Properties;

public class Main {

    public static void main(String[] args) throws Exception {
        Properties p = new Properties();
        try (InputStream is = Main.class.getClassLoader().getResourceAsStream("app.properties")) {
            if (is == null) throw new RuntimeException("No se encontró app.properties en resources");
            p.load(is);
        }

        String baseUrl = p.getProperty("marketplace.baseUrl");
        String path = p.getProperty("marketplace.ordersTodayPath");

        String brokerUrl = p.getProperty("artemis.brokerUrl");
        String user = p.getProperty("artemis.username");
        String pass = p.getProperty("artemis.password");
        String queue = p.getProperty("queue.nva_mkp_pedidos");

        MarketplaceClient client = new MarketplaceClient(baseUrl, path);
        MarketplaceToCanonicalMapper mapper = new MarketplaceToCanonicalMapper();

        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        MarketplaceOrder[] orders = client.getOrdersToday();
        System.out.println("Pedidos obtenidos desde Marketplace: " + (orders == null ? 0 : orders.length));

        try (JmsQueuePublisher publisher = new JmsQueuePublisher(brokerUrl, user, pass, queue)) {
            if (orders != null) {
                for (MarketplaceOrder o : orders) {
                    CanonicalOrder canonical = mapper.map(o);
                    String canonicalJson = gson.toJson(canonical);

                    publisher.sendText(canonicalJson);
                    System.out.println("Enviado a " + queue + ": idPedido=" + canonical.idPedido);
                }
            }
        }

        System.out.println("OK");
    }
}