package cl.iplacex.marketplaceadapter.service;

import cl.iplacex.marketplaceadapter.model.CanonicalOrder;
import cl.iplacex.marketplaceadapter.model.MarketplaceOrder;

import java.time.OffsetDateTime;
import java.util.UUID;
import java.util.stream.Collectors;

public class MarketplaceToCanonicalMapper {

    public CanonicalOrder map(MarketplaceOrder src) {
        CanonicalOrder dst = new CanonicalOrder();
        dst.idPedido = src.id;
        dst.origen = "mkp";
        dst.fecha = src.fecha;

        // Cliente
        dst.cliente = new CanonicalOrder.Cliente();
        if (src.cliente != null) {
            dst.cliente.nombre = src.cliente.nombre;
            dst.cliente.apellido = src.cliente.apellido;
            dst.cliente.rut = src.cliente.rut;
        }
        dst.cliente.telefono = null;

        // Dirección
        dst.direccion = new CanonicalOrder.Direccion();
        if (src.direccion != null) {
            dst.direccion.calle = src.direccion.calle;
            dst.direccion.numero = src.direccion.numero;
            dst.direccion.comuna = src.direccion.comuna;
            dst.direccion.ciudad = src.direccion.ciudad;
            dst.direccion.region = null;

            if (src.direccion.calle != null && src.direccion.numero != null) {
                dst.direccion.calleYNumero = src.direccion.calle + " " + src.direccion.numero;
            } else {
                dst.direccion.calleYNumero = null;
            }
        }

        // Items
        dst.items = (src.items == null) ? java.util.List.of() :
                src.items.stream().map(it -> {
                    CanonicalOrder.Item ci = new CanonicalOrder.Item();
                    ci.sku = null;
                    ci.nombreProducto = it.producto;
                    ci.cantidad = it.cantidad;
                    ci.precioUnitario = it.precioUnitario;
                    ci.montoTotal = it.montoTotal;
                    ci.comisionMarketplace = it.comisionMarketplace;
                    return ci;
                }).collect(Collectors.toList());

        // Totales
        dst.totales = new CanonicalOrder.Totales();
        dst.totales.costoEnvio = null;

        double total = 0.0;
        for (CanonicalOrder.Item it : dst.items) {
            if (it.montoTotal != null) total += it.montoTotal;
            else total += (it.precioUnitario * it.cantidad);
        }
        dst.totales.total = total;

        // Metadatos (enriquecimiento)
        dst.metadatos = new CanonicalOrder.Metadatos();
        dst.metadatos.canal = "mkp";
        dst.metadatos.traceId = UUID.randomUUID().toString();
        dst.metadatos.timestampIngesta = OffsetDateTime.now().toString();

        return dst;
    }
}