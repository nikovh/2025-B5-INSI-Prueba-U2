package cl.iplacex.marketplaceadapter.service;

import cl.iplacex.marketplaceadapter.model.MarketplaceOrder;
import com.google.gson.Gson;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class MarketplaceClient {

    private final HttpClient http = HttpClient.newHttpClient();
    private final Gson gson = new Gson();

    private final String baseUrl;
    private final String ordersTodayPath;

    public MarketplaceClient(String baseUrl, String ordersTodayPath) {
        this.baseUrl = baseUrl;
        this.ordersTodayPath = ordersTodayPath;
    }

    public MarketplaceOrder[] getOrdersToday() {
        try {
            String url = baseUrl + ordersTodayPath;
            HttpRequest req = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .build();

            HttpResponse<String> res = http.send(req, HttpResponse.BodyHandlers.ofString());
            if (res.statusCode() != 200) {
                throw new RuntimeException("Marketplace respondió " + res.statusCode() + ": " + res.body());
            }
            return gson.fromJson(res.body(), MarketplaceOrder[].class);
        } catch (Exception e) {
            throw new RuntimeException("Error consultando /orders/today", e);
        }
    }
}