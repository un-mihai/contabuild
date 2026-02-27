package app.model;

import app.exception.ValidareException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

class FacturaIntrareTest {

    @Test
    @DisplayName("test constructor si getteri")
    void testConstructor() {
        LocalDate data = LocalDate.of(2024, 3, 15);
        FacturaIntrare factura = new FacturaIntrare("F-IN-1001", data, "Furnizor SRL");

        assertEquals("F-IN-1001", factura.getNumarFactura());
        assertEquals(data, factura.getDataEmitere());
        assertEquals("Furnizor SRL", factura.getVanzator());
        assertEquals(0.0, factura.getValoareTotala(), 0.001, "O factura noua trebuie sa aiba total 0");
    }

    @Test
    @DisplayName("test adaugare produs")
    void testAdaugaProdus() throws ValidareException{
        FacturaIntrare factura = new FacturaIntrare("Test", LocalDate.now(), "F");
        Produs produs = new Produs("Ciment", 10.0, 0, 0);
        factura.adaugaProdus(produs, 10, 50.0, 19.0);

        assertEquals(1, factura.getLiniiFactura().size());
        assertEquals(500.0, factura.getValoareBaza(), 0.001);
        assertEquals(595.0, factura.getValoareTotala(), 0.001);
    }

    @Test
    @DisplayName("test adaugare linie factura (metoda mostenita)")
    void testAdaugaLinie() throws ValidareException {

        FacturaIntrare factura = new FacturaIntrare("Test", LocalDate.now(), "F");
        Produs produs = new Produs("Vopsea", 20.0, 0, 0);

        LinieFactura linie = new LinieFactura(produs, 2, 100.0, 0.0);

        factura.adaugaLinie(linie);

        assertEquals(200.0, factura.getValoareBaza(), 0.001);
        assertEquals(200.0, factura.getValoareTotala(), 0.001);
    }

    @Test
    @DisplayName("test tip factura")
    void testTip() {
        FacturaIntrare factura = new FacturaIntrare("X", LocalDate.now(), "Y");
        assertEquals(Factura.Tip.INTRARE, factura.getTip());
        assertEquals("I", factura.getTip().getCod());
    }
}