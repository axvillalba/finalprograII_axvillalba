package modelo;

public class Tinto extends Vino implements Maridaje {

    private UvaTinta uva;
    private NivelTaninos taninos;
    private Cuerpo cuerpo;

    public Tinto(int id, String nombre, String bodega, int anioProduccion,
            TipoCrianza tipoCrianza, Aroma aroma, Barrica barrica,
            double precio, int stockBotellas,
            UvaTinta uva, NivelTaninos taninos, Cuerpo cuerpo) {
        super(id, nombre, bodega, anioProduccion, tipoCrianza, aroma, barrica, precio, stockBotellas);
        this.uva = uva;
        this.taninos = taninos;
        this.cuerpo = cuerpo;
        validar();
    }

    @Override
    public void validar() {
        super.validar();
        if (uva == null) {
            throw new IllegalArgumentException("Uva requerida (tinto)");
        }
        if (taninos == null) {
            throw new IllegalArgumentException("Taninos requeridos");
        }
        if (cuerpo == null) {
            throw new IllegalArgumentException("Cuerpo requerido");
        }
    }

    public UvaTinta getUva() {
        return uva;
    }

    public NivelTaninos getTaninos() {
        return taninos;
    }

    public Cuerpo getCuerpo() {
        return cuerpo;
    }

    @Override
    public String sugerirMaridaje() {
        return switch (uva) {
            case MALBEC ->
                "Carnes rojas, empanadas, pastas con salsa intensa.";
            case CABERNET_SAUVIGNON ->
                "Asados, quesos duros, platos especiados.";
            case MERLOT ->
                "Pollo al horno, pastas suaves, quesos semiduros.";
            case SYRAH ->
                "Cordero, comidas especiadas, parrilla.";
            case PINOT_NOIR ->
                "Hongos, aves, pescados grasos.";
            default ->
                "Carnes y quesos (maridaje general).";
        };
    }
}
