package app.model;

import app.exception.ValidareException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

class FacturaIesireTest {

    @Test
    @DisplayName("test constructor")
    void testConstructor() {
        FacturaIesire factura = new FacturaIesire("F-OUT-55", LocalDate.now(), "Client Popescu");

        assertEquals("F-OUT-55", factura.getNumarFactura());
        assertEquals("Client Popescu", factura.getClient());
    }

    @Test
    @DisplayName("test calcul profit")
    void testCalculProfit() throws ValidareException {
        FacturaIesire factura = new FacturaIesire("ProfitTest", LocalDate.now(), "C");

        Produs p1 = new Produs("Produs A", 100.0, 0, 0);
        LinieFactura l1 = new LinieFactura(p1, 2, 150.0, 19.0);

        Produs p2 = new Produs("Produs B", 50.0, 0, 0);
        LinieFactura l2 = new LinieFactura(p2, 5, 50.0, 19.0);

        factura.adaugaLinie(l1);
        factura.adaugaLinie(l2);

        // Baza: (2*150) + (5*50) = 300 + 250 = 550
        assertEquals(550.0, factura.getValoareBaza(), 0.001);

        // Profit asteptat: 100 (de la p1) + 0 (de la p2) = 100
        assertEquals(100.0, factura.getProfit(), 0.001, "profitul total e calculat gresit");
    }

    @Test
    @DisplayName("test tip factura")
    void testTip() {
        FacturaIesire factura = new FacturaIesire("X", LocalDate.now(), "C");
        assertEquals(Factura.Tip.IESIRE, factura.getTip());
        assertEquals("E", factura.getTip().getCod());
    }

    @Test
    @DisplayName("test adaugare produs direct")
    void testAdaugaProdusDirect() throws ValidareException{
        FacturaIesire factura = new FacturaIesire("Direct", LocalDate.now(), "C");
        Produs p = new Produs("X", 10, 0, 0);

        factura.adaugaProdus(p, 1, 200.0, 10.0);

        assertEquals(200.0, factura.getValoareBaza(), 0.001);

        // Total = 200 + 10% = 220
        assertEquals(220.0, factura.getValoareTotala(), 0.001);
    }
}