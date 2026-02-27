package app.model;

import app.exception.ValidareException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class LinieFacturaTest {

    @Test
    @DisplayName("test constructor + getteri simplii")
    void testConstructorSiGetters() throws ValidareException{
        Produs produs = new Produs("Produs Test", 10.0, 0, 0);
        int cantitate = 5;
        double pretVanzare = 100.0;
        double cotaTva = 19.0;

        LinieFactura linie = new LinieFactura(produs, cantitate, pretVanzare, cotaTva);

        assertEquals(produs, linie.getProdus(), "produsul nu a fost salvat corect");
        assertEquals(5, linie.getCantitate(), "cantitatea nu este corecta");
        assertEquals(100.0, linie.getPretUnitFaraTva(), 0.001, "pretul unitar nu este corect");
        assertEquals(19.0, linie.getCotaTvaProcent(), 0.001, "cota TVA nu este corecta");

        assertEquals(0, linie.getIdLinie());
        assertEquals(0, linie.getNumarLinie());
    }

    @Test
    @DisplayName("test calcul valori (fara TVA, TVA, cu TVA)")
    void testCalculeTotale() throws ValidareException{
        Produs produs = new Produs("Ciment", 50.0, 0, 0);


        LinieFactura linie = new LinieFactura(produs, 3, 200.0, 19.0);

        assertEquals(600.0, linie.getValoareFaraTva(), 0.001,
                "valoarea fara tva este calculata gresit");

        assertEquals(114.0, linie.getValoareTva(), 0.001,
                "valoarea tva este calculata grsit");

        assertEquals(714.0, linie.getValoareCuTva(), 0.001,
                "valoarea totala cu TVA este calculata gresit");
    }

    @Test
    @DisplayName("test calcule cu TVA 0")
    void testCalculeFaraTva() throws ValidareException{
        Produs produs = new Produs("Export", 100.0, 0, 0);
        LinieFactura linie = new LinieFactura(produs, 2, 50.0, 0.0);

        assertEquals(100.0, linie.getValoareFaraTva(), 0.001);

        assertEquals(0.0, linie.getValoareTva(), 0.001);

        assertEquals(100.0, linie.getValoareCuTva(), 0.001);
    }


    @Test
    @DisplayName("test calcul profit")
    void testCalculProfit() throws ValidareException{

        double costAchizitie = 50.0;
        Produs produs = new Produs("Marfa", costAchizitie, 0, 0);

        double pretVanzare = 80.0;
        int cantitate = 10;

        LinieFactura linie = new LinieFactura(produs, cantitate, pretVanzare, 19.0);

        double profitCalculat = linie.getProfitTotal();

        // venit total (fara tva) = 80 * 10 = 800
        // cost total (marfa) = 50 * 10 = 500
        // profit asteptat = 800 - 500 = 300

        assertEquals(300.0, profitCalculat, 0.001,
                "profitul nu este calculat corect (PretVanzare - CostMediu) * Cantitate");
    }

    @Test
    @DisplayName("test calcul profit negativ")
    void testProfitNegativ() throws ValidareException{
        Produs produs = new Produs("Lichidare", 100.0, 0, 0);
        LinieFactura linie = new LinieFactura(produs, 1, 80.0, 19.0);

        // profit = 80 - 100 = -20
        assertEquals(-20.0, linie.getProfitTotal(), 0.001);
    }

    @Test
    @DisplayName("test toString")
    void testToString() throws ValidareException {
        Produs produs = new Produs("ProdusToString", 10.0, 0, 0);
        LinieFactura linie = new LinieFactura(produs, 5, 20.0, 19.0);

        String rezultat = linie.toString();

        assertTrue(rezultat.contains("ProdusToString"));
        assertTrue(rezultat.contains("Cantitate: 5"));
        assertTrue(rezultat.contains("119.0"));
    }
}