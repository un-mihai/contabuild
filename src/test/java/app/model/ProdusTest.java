package app.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ProdusTest {

    @Test
    @DisplayName("test constructor complet")
    void testConstructorComplet() {
        int id = 10;
        String nume = "Bormasina";
        double costMediu = 200.0;
        double adaos = 50.0;
        double tva = 19.0;
        int stoc = 5;

        Produs produs = new Produs(id, nume, costMediu, adaos, tva, stoc);

        assertEquals(10, produs.getIdProdus(), "id-ul nu a fost setat corect");
        assertEquals("Bormasina", produs.getNume(), "numele nu a fost setat corect");
        assertEquals(200.0, produs.getCostMediu(), 0.001, "costul mediu nu a fost setat corect");
        assertEquals(50.0, produs.getAdaosComercial(), 0.001, "adaosul nu a fost setat corect");
        assertEquals(19.0, produs.getCotaTvaVanzare(), 0.001, "cota TVA nu a fost setata corect");
        assertEquals(5, produs.getStocTotal(), "stocul nu a fost setat corect");
    }

    @Test
    @DisplayName("test constructor simplu")
    void testConstructorSimplu() {
        String nume = "Surub";
        double costMediu = 0.5;
        double adaos = 10.0;
        double tva = 19.0;

        Produs produs = new Produs(nume, costMediu, adaos, tva);

        assertEquals(0, produs.getIdProdus(), "Id-ul ar trebui sa fie 0 implicit");
        assertEquals("Surub", produs.getNume());
        assertEquals(0.5, produs.getCostMediu(), 0.001);
        assertEquals(0, produs.getStocTotal(), "Stocul ar trebui sa fie 0 implicit");
    }

    @Test
    @DisplayName("test calcul pret vanzare fara tva")
    void testGetPretVanzareFaraTva() {
        Produs produs = new Produs("Test", 100.0, 20.0, 19.0);

        double rezultatAsteptat = 120.0;
        double rezultatActual = produs.getPretVanzareFaraTva();

        assertEquals(rezultatAsteptat, rezultatActual, 0.01,
                "pretul de vanzare fara tva e calculat gresit");
    }

    @Test
    @DisplayName("test calcul pret vanzare cu tva")
    void testGetPretVanzareCuTva() {
        Produs produs = new Produs("Test", 100.0, 20.0, 19.0);

        double rezultatAsteptat = 142.8;
        double rezultatActual = produs.getPretVanzareCuTva();

        assertEquals(rezultatAsteptat, rezultatActual, 0.01,
                "pretul de vanzare cu tva e calculat gresit");
    }

    @Test
    @DisplayName("test calcul pentru valori de 0")
    void testCalculeCuZero() {
        Produs produs = new Produs("Gratis", 0.0, 50.0, 19.0);

        assertEquals(0.0, produs.getPretVanzareFaraTva(), 0.001);
        assertEquals(0.0, produs.getPretVanzareCuTva(), 0.001);
    }

    @Test
    @DisplayName("test toString")
    void testToString() {
        Produs produs = new Produs(99, "ProdusX", 100.0, 0.0, 0.0, 50);

        String rezultat = produs.toString();

        assertTrue(rezultat.contains("ID: 99"));
        assertTrue(rezultat.contains("Nume: ProdusX"));
        assertTrue(rezultat.contains("Stoc: 50"));
    }
}