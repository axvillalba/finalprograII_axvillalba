package modelo;

public class Rosado extends Vino implements Maridaje {

    private UvaTinta uvaBase;
    private MetodoRosado metodo;
    private Dulzor dulzor;

    public Rosado(int id, String nombre, String bodega, int anioProduccion,
            TipoCrianza tipoCrianza, Aroma aroma, Barrica barrica,
            double precio, int stockBotellas,
            UvaTinta uvaBase, MetodoRosado metodo, Dulzor dulzor) {
        super(id, nombre, bodega, anioProduccion, tipoCrianza, aroma, barrica, precio, stockBotellas);
        this.uvaBase = uvaBase;
        this.metodo = metodo;
        this.dulzor = dulzor;
        validar();
    }

    @Override
    public void validar() {
        super.validar();
        if (uvaBase == null) {
            throw new IllegalArgumentException("Uva base requerida (rosado)");
        }
        if (metodo == null) {
            throw new IllegalArgumentException("Método rosado requerido");
        }
        if (dulzor == null) {
            throw new IllegalArgumentException("Dulzor requerido (rosado)");
        }
    }

    public UvaTinta getUvaBase() {
        return uvaBase;
    }

    public MetodoRosado getMetodo() {
        return metodo;
    }

    public Dulzor getDulzor() {
        return dulzor;
    }

    @Override
    public String sugerirMaridaje() {
        return "Picadas, sushi, pastas livianas, ensaladas, comida de verano.";
    }
}
