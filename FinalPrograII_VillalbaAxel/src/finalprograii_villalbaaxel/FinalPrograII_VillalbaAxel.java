package finalprograii_villalbaaxel;

import gui.Principal;
import servicio.GestorVinos;
import modelo.*;

import javax.swing.SwingUtilities;
import java.util.List;

public class FinalPrograII_VillalbaAxel {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {

            GestorVinos gestor = new GestorVinos();

            // Inventario de prueba para testear la UI
            gestor.setInventario(cargarVinosDePrueba());

            Principal p = new Principal(gestor);
            p.setLocationRelativeTo(null);
            p.setVisible(true);
        });
    }

    private static List<Vino> cargarVinosDePrueba() {
        return List.of(
            new Tinto(
                1, "Rutini Malbec", "Rutini", 2021,
                TipoCrianza.RESERVA, Aroma.FRUTAL, Barrica.ROBLE_FRANCES,
                12500, 12,
                UvaTinta.MALBEC, NivelTaninos.MEDIO, Cuerpo.MEDIO
            ),
            new Tinto(
                2, "Trumpeter Cabernet", "La Rural", 2020,
                TipoCrianza.RESERVA, Aroma.ESPECIADO, Barrica.ROBLE_AMERICANO,
                9900, 8,
                UvaTinta.CABERNET_SAUVIGNON, NivelTaninos.ALTO, Cuerpo.ROBUSTO
            ),
            new Tinto(
                3, "Merlot Reserva", "Trapiche", 2019,
                TipoCrianza.RESERVA, Aroma.BALSAMICO, Barrica.ROBLE_FRANCES,
                11000, 6,
                UvaTinta.MERLOT, NivelTaninos.MEDIO, Cuerpo.MEDIO
            ),

            new Blanco(
                4, "Norton Chardonnay", "Norton", 2022,
                TipoCrianza.JOVEN, Aroma.CITRICO, Barrica.SIN_BARRICA,
                7200, 15,
                UvaBlanca.CHARDONNAY, Dulzor.SECO, Acidez.MEDIA, 10
            ),
            new Blanco(
                5, "Trapiche Torrontés", "Trapiche", 2023,
                TipoCrianza.JOVEN, Aroma.FLORAL, Barrica.SIN_BARRICA,
                6800, 10,
                UvaBlanca.TORRONTES, Dulzor.SEMI_SECO, Acidez.ALTA, 9
            ),
            new Blanco(
                6, "Sauvignon Blanc", "Rutini", 2022,
                TipoCrianza.JOVEN, Aroma.HERBAL, Barrica.SIN_BARRICA,
                7900, 9,
                UvaBlanca.SAUVIGNON_BLANC, Dulzor.SECO, Acidez.ALTA, 8
            ),

            new Rosado(
                7, "Rosé de Verano", "Nieto Senetiner", 2023,
                TipoCrianza.JOVEN, Aroma.FRUTAL, Barrica.SIN_BARRICA,
                7600, 18,
                UvaTinta.PINOT_NOIR, MetodoRosado.PRENSADO_DIRECTO, Dulzor.SECO
            ),
            new Rosado(
                8, "Rosado Premium", "Rutini", 2022,
                TipoCrianza.JOVEN, Aroma.FLORAL, Barrica.SIN_BARRICA,
                8500, 9,
                UvaTinta.MALBEC, MetodoRosado.SANGRADO, Dulzor.SEMI_SECO
            ),
            new Rosado(
                9, "Rosado Dulce", "La Rural", 2023,
                TipoCrianza.JOVEN, Aroma.FRUTAL, Barrica.SIN_BARRICA,
                7000, 11,
                UvaTinta.SYRAH, MetodoRosado.MEZCLA, Dulzor.DULCE
            )
        );
    }
}
