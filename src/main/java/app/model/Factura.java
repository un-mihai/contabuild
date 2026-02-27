package app.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Clasa de baza abstracta pentru toate tipurile de facturi.
 * Gestioneaza lista de linii, totalurile si datele comune (numar, data, etc).
 */
public abstract class Factura {

    /**
     * Defineste tipurile de facturi suportate de aplicatie.
     * 'I' pentru Intrare (Achizitie), 'E' pentru Iesire (Vanzare).
     */
    public enum Tip{
        INTRARE("I"),
        IESIRE("E");

        private final String cod;

        Tip(String cod){
            this.cod = cod;
        }

        public String getCod(){
            return cod;
        }
    }

    protected int idFactura;
    protected String numarFactura;
    protected LocalDate dataEmitere;
    protected double valoareBaza;
    protected double valoareTotala;
    protected List<LinieFactura> liniiFactura;

    /**
     *Constructor simplu pentru facturile create de utilizator
     * (id ul este setat in baza de date)
     */
    public Factura(String numarFactura, LocalDate dataEmitere) {
        this.idFactura = 0;
        this.numarFactura = numarFactura;
        this.dataEmitere = dataEmitere;
        this.liniiFactura = new ArrayList<>();
        this.valoareBaza = 0;
        this.valoareTotala = 0;
    }

    /**
     * Constructor complet pentru facturile incarcate din baza de date
     */
    public Factura(int idFactura, String numarFactura, LocalDate dataEmitere) {
        this.idFactura = idFactura;
        this.numarFactura = numarFactura;
        this.dataEmitere = dataEmitere;
        this.liniiFactura = new ArrayList<>();
        this.valoareBaza = 0;
        this.valoareTotala = 0;
    }

    public int getIdFactura() {
        return idFactura;
    }

    public String getNumarFactura() {
        return numarFactura;
    }

    public LocalDate getDataEmitere() {
        return dataEmitere;
    }

    public double getValoareBaza() {
        return valoareBaza;
    }

    public double getValoareTotala() {
        return valoareTotala;
    }

    public List<LinieFactura> getLiniiFactura() {
        return liniiFactura;
    }

    public void adaugaLinie(LinieFactura linie) {
        liniiFactura.add(linie);
        valoareBaza += linie.getValoareFaraTva();
        valoareTotala += linie.getValoareCuTva();
    }

    public void setValoareBaza(double valoareBaza) {
        this.valoareBaza = valoareBaza;
    }

    public void setValoareTotala(double valoareTotala) {
        this.valoareTotala = valoareTotala;
    }

    public abstract Tip getTip();
}