package modelo;

public class Blanco extends Vino implements Maridaje {

    private UvaBlanca uva;
    private Dulzor dulzor;
    private Acidez acidez;
    private double tempServicioC;

    public Blanco(int id, String nombre, String bodega, int anioProduccion,
            TipoCrianza tipoCrianza, Aroma aroma, Barrica barrica,
            double precio, int stockBotellas,
            UvaBlanca uva, Dulzor dulzor, Acidez acidez, double tempServicioC) {
        super(id, nombre, bodega, anioProduccion, tipoCrianza, aroma, barrica, precio, stockBotellas);
        this.uva = uva;
        this.dulzor = dulzor;
        this.acidez = acidez;
        this.tempServicioC = tempServicioC;
        validar();
    }

    @Override
    public void validar() {
        super.validar();
        if (uva == null) {
            throw new IllegalArgumentException("Uva requerida (blanco)");
        }
        if (dulzor == null) {
            throw new IllegalArgumentException("Dulzor requerido");
        }
        if (acidez == null) {
            throw new IllegalArgumentException("Acidez requerida");
        }
        if (tempServicioC < 0 || tempServicioC > 20) {
            throw new IllegalArgumentException("Temp. servicio inválida");
        }
    }

    public UvaBlanca getUva() {
        return uva;
    }

    public Dulzor getDulzor() {
        return dulzor;
    }

    public Acidez getAcidez() {
        return acidez;
    }

    public double getTempServicioC() {
        return tempServicioC;
    }

    @Override
    public String sugerirMaridaje() {
        return switch (uva) {
            case SAUVIGNON_BLANC ->
                "Mariscos, ensaladas, quesos frescos.";
            case CHARDONNAY ->
                "Pescados, pastas con crema, pollo.";
            case TORRONTES ->
                "Picadas, comida suave, quesos.";
            default ->
                "Pescados, ensaladas y quesos suaves.";
        };
    }
}
