package cl.iplacex.marketplaceadapter.model;

import java.util.List;

public class MarketplaceOrder {
    public String id;
    public String fecha;
    public Cliente cliente;
    public Direccion direccion;
    public List<Item> items;

    public static class Cliente {
        public String nombre;
        public String apellido;
        public String rut;
    }

    public static class Direccion {
        public String calle;
        public String numero;
        public String comuna;
        public String ciudad;
    }

    public static class Item {
        public String producto;
        public int cantidad;
        public double precioUnitario;
        public double montoTotal;
        public double comisionMarketplace;
    }
}