package modelo;

import java.io.Serializable;
import java.time.Year;
import java.util.Objects;

public abstract class Vino implements Serializable {

    protected int id;
    protected String nombre;
    protected String bodega;
    protected int anioProduccion;
    protected TipoCrianza tipoCrianza;
    protected Aroma aroma;
    protected Barrica barrica;
    protected double precio;
    protected int stockBotellas;

    protected Vino(int id, String nombre, String bodega, int anioProduccion,
            TipoCrianza tipoCrianza, Aroma aroma, Barrica barrica,
            double precio, int stockBotellas) {
        this.id = id;
        this.nombre = nombre;
        this.bodega = bodega;
        this.anioProduccion = anioProduccion;
        this.tipoCrianza = tipoCrianza;
        this.aroma = aroma;
        this.barrica = barrica;
        this.precio = precio;
        this.stockBotellas = stockBotellas;
        //validar();
    }

    public void validar() {
        if (id <= 0) {
            throw new IllegalArgumentException("ID debe ser > 0");
        }
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("Nombre requerido");
        }
        if (bodega == null || bodega.trim().isEmpty()) {
            throw new IllegalArgumentException("Bodega requerida");
        }

        int actual = Year.now().getValue();
        if (anioProduccion < 1900 || anioProduccion > actual) {
            throw new IllegalArgumentException("Año de producción inválido: " + anioProduccion);
        }

        Objects.requireNonNull(tipoCrianza, "Tipo de crianza requerido");
        Objects.requireNonNull(aroma, "Aroma requerido");
        Objects.requireNonNull(barrica, "Barrica requerida");

        if (precio <= 0) {
            throw new IllegalArgumentException("Precio debe ser > 0");
        }
        if (stockBotellas < 0) {
            throw new IllegalArgumentException("Stock no puede ser negativo");
        }
    }

    public String ficha() {
        return String.format(
                "ID:%d | %s (%s) | Año:%d | %s | Aroma:%s | Barrica:%s | $%.2f | Stock:%d",
                id, nombre, bodega, anioProduccion, tipoCrianza, aroma, barrica, precio, stockBotellas
        );
    }

    // Getters/setters básicos
    public int getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getBodega() {
        return bodega;
    }

    public int getAnioProduccion() {
        return anioProduccion;
    }

    public TipoCrianza getTipoCrianza() {
        return tipoCrianza;
    }

    public Aroma getAroma() {
        return aroma;
    }

    public Barrica getBarrica() {
        return barrica;
    }

    public double getPrecio() {
        return precio;
    }

    public int getStockBotellas() {
        return stockBotellas;
    }

    public void setId(int id) {
        if (id <= 0) {
            throw new IllegalArgumentException("ID debe ser > 0");
        }
        this.id = id;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
        validar();
    }

    public void setBodega(String bodega) {
        this.bodega = bodega;
        validar();
    }

    public void setAnioProduccion(int anioProduccion) {
        this.anioProduccion = anioProduccion;
        validar();
    }

    public void setTipoCrianza(TipoCrianza tipoCrianza) {
        this.tipoCrianza = tipoCrianza;
        validar();
    }

    public void setAroma(Aroma aroma) {
        this.aroma = aroma;
        validar();
    }

    public void setBarrica(Barrica barrica) {
        this.barrica = barrica;
        validar();
    }

    public void setPrecio(double precio) {
        this.precio = precio;
        validar();
    }

    public void setStockBotellas(int stockBotellas) {
        this.stockBotellas = stockBotellas;
        validar();
    }
}
