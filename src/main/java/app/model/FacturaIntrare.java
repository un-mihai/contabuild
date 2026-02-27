package app.model;

import app.exception.ValidareException;

import java.time.LocalDate;

/**
 * Reprezinta o factura de achizitie de la un furnizor.
 */
public class FacturaIntrare extends Factura {

    private String vanzator;

    /**
     * Construtor simplu pentru facturile create de utilizator
     */
    public FacturaIntrare(String numarFactura, LocalDate dataEmitere, String vanzator) {
        super(numarFactura, dataEmitere);
        this.vanzator = vanzator;
    }

    /**
     * Constructor complet pentru facturile incarcate din baza de date
     */
    public FacturaIntrare(int idFactura, String numarFactura, LocalDate dataEmitere, String vanzator) {
        super(idFactura, numarFactura, dataEmitere);
        this.vanzator = vanzator;
    }

    public String getVanzator() {
        return vanzator;
    }

    public void adaugaProdus(Produs produs, int cantitate, double pretUnitFaraTva, double cotaTvaProcent) throws ValidareException {
        this.liniiFactura.add(new LinieFactura(produs, cantitate, pretUnitFaraTva, cotaTvaProcent));
        this.valoareBaza += pretUnitFaraTva * cantitate;
        this.valoareTotala += pretUnitFaraTva * (1 + cotaTvaProcent/100) * cantitate;
    }

    public Tip getTip(){
        return Tip.INTRARE;
    }
}