package cl.iplacex.marketplaceadapter.model;

import java.util.List;

public class CanonicalOrder {
    public String       idPedido;
    public String       origen;   // "mkp"
    public String       fecha;

    public Cliente      cliente;
    public Direccion    direccion;
    public List<Item>   items;
    public Totales      totales;
    public Metadatos    metadatos;

    public static class Cliente {
        public String   nombre;
        public String   apellido;
        public String   rut;
        public String   telefono; // null
    }

    public static class Direccion {
        public String   calle;
        public String   numero;
        public String   comuna;
        public String   ciudad;
        public String   region;       // null
        public String   calleYNumero; // opcional
    }

    public static class Item {
        public String   sku; // null
        public String   nombreProducto;
        public int      cantidad;
        public double   precioUnitario;
        public Double   montoTotal; // puede venir
        public Double   comisionMarketplace;
    }

    public static class Totales {
        public Double   costoEnvio; // null
        public double   total;
    }

    public static class Metadatos {
        public String   canal; // "mkp"
        public String   traceId;
        public String   timestampIngesta;
    }
}