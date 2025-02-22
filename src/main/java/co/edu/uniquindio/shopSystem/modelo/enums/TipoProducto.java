package co.edu.uniquindio.shopSystem.modelo.enums;

import lombok.Getter;

@Getter
public enum TipoProducto {
    ALIMENTOS("Productos alimenticios"),
    BEBIDAS("Bebidas y refrescos"),
    LACTEOS("Productos lácteos"),
    CARNES("Carnes y embutidos"),
    PANADERIA("Panadería y repostería"),
    FRUTAS_VERDURAS("Frutas y verduras"),
    CONGELADOS("Alimentos congelados"),
    LIMPIEZA("Productos de limpieza"),
    HIGIENE("Productos de higiene personal"),
    MASCOTAS("Productos para mascotas"),
    HOGAR("Artículos para el hogar"),
    ELECTRONICA("Electrodomésticos y electrónica");

    private final String descripcion;

    TipoProducto(String descripcion) {
        this.descripcion = descripcion;
    }

}

