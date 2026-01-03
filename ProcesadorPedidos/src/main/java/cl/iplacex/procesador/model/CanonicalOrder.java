package cl.iplacex.procesador.model;

import java.util.List;

public class CanonicalOrder {
    public String idPedido;
    public String origen;
    public String fecha;
    public Cliente cliente;
    public Direccion direccion;
    public List<Item> items;
    public Totales totales;
    public Metadatos metadatos;

    public static class Cliente {
        public String nombre;
        public String apellido;
        public String rut;
        public String telefono;
    }

    public static class Direccion {
        public String calle;
        public String numero;
        public String comuna;
        public String ciudad;
        public String calleYNumero;
    }

    public static class Item {
        public String sku;
        public String nombreProducto;
        public int cantidad;
        public double precioUnitario;
        public double montoTotal;
    }

    public static class Totales {
        public double total;
    }

    public static class Metadatos {
        public String canal;
        public String traceId;
        public String timestampIngesta;
    }
}