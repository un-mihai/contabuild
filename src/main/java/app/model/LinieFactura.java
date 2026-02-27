package app.model;

import app.config.Constante;
import app.exception.ValidareException;

/**
 * Reprezinta un rand de pe o factura.
 * Leaga un produs de o cantitate si o valoare tva specifica tranzactiei.
 */
public class LinieFactura {
    private int idLinie;
    private int numarLinie;
    private Produs produs;
    private double pretUnitFaraTva;
    private double cotaTvaProcent;
    private int cantitate;

    /**
     * Constructor simplu pentru liniile create de utilizator
     * (id-ul si numarul sunt initializate cu 0)
     */
    public LinieFactura(Produs produs, int cantitate, double pretUnitFaraTva, double cotaTvaProcent) throws ValidareException {
        this.idLinie = 0;
        this.numarLinie = 0;
        this.produs = produs;
        this.pretUnitFaraTva = pretUnitFaraTva;
        if(cotaTvaProcent > Constante.TVA){
            throw new ValidareException("TVA-ul de vanzare nu poate fi mai mare decat TVA-ul declarat");
        }
        this.cotaTvaProcent = cotaTvaProcent;
        if(cantitate < 0){
            throw new ValidareException("Cantitatea nu poate fi negativa");
        }
        this.cantitate = cantitate;
    }

    /**
     * Constructor complet (pentru liniile incarcate din baza de date)
     */
    public LinieFactura(int idLinie, int numarLinie, Produs produs, int cantitate,
                        double pretUnitFaraTva, double cotaTvaProcent) throws ValidareException {
        this.idLinie = idLinie;
        this.numarLinie = numarLinie;
        this.produs = produs;
        this.pretUnitFaraTva = pretUnitFaraTva;
        if(cotaTvaProcent > Constante.TVA){
            throw new ValidareException("TVA-ul de vanzare nu poate fi mai mare decat TVA-ul legal");
        }
        this.cotaTvaProcent = cotaTvaProcent;
        if(cantitate < 0){
            throw new ValidareException("Cantitatea nu poate fi negativa");
        }
        this.cantitate = cantitate;
    }


    public int getIdLinie() {
        return idLinie;
    }

    public int getNumarLinie() {
        return numarLinie;
    }

    public Produs getProdus() {
        return produs;
    }

    public int getCantitate() {
        return cantitate;
    }

    public double getValoareFaraTva(){
        return this.pretUnitFaraTva * cantitate;
    }

    public double getValoareTva(){
        return getValoareFaraTva() * (this.cotaTvaProcent / 100.00);
    }

    public double getValoareCuTva(){
        return getValoareFaraTva() + getValoareTva();
    }

    public double getProfitTotal(){
        return getValoareFaraTva() - produs.getCostMediu() * cantitate;
    }

    public double getCotaTvaProcent(){
        return cotaTvaProcent;
    }

    public double getPretUnitFaraTva(){
        return pretUnitFaraTva;
    }

    public String toString() {
        return produs +
                "\nCantitate: " + cantitate +
                "\nValoare: " + getValoareCuTva();
    }
}