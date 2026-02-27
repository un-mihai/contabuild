package app.model;

import app.exception.ValidareException;

import java.time.LocalDate;

/**
 * Reprezinta o factura de vanzare catre un client.
 * Aceasta clasa determina profitul.
 */
public class FacturaIesire extends Factura {
    private String client;
    private double profitStatic;

    /**
     * Constructor simplu pentru facturile create de user
     */
    public FacturaIesire(String numarFactura, LocalDate dataEmitere, String client) {
        super(numarFactura, dataEmitere);
        this.client = client;
        this.profitStatic = 0;
    }

    /**
     * Constructor complet pentru facturile incarcate din baza de date
     */
    public FacturaIesire(int idFactura, String numarFactura, LocalDate dataEmitere, String client, double profitStatic) {
        super(idFactura, numarFactura, dataEmitere);
        this.client = client;
        this.profitStatic = profitStatic;
    }

    /**
     * Adauga un produs pe factura de iesire si actualizeaza automat totalurile (Baza si Total).
     */
    public void adaugaProdus(Produs produs, int cantitate, double pretUnitFaraTva, double cotaTvaProcent) throws ValidareException {
        this.liniiFactura.add(new LinieFactura(produs, cantitate, pretUnitFaraTva, cotaTvaProcent));
        this.valoareBaza += pretUnitFaraTva * cantitate;
        this.valoareTotala += pretUnitFaraTva * (1 + cotaTvaProcent/100) * cantitate;
    }

    public String getClient(){
        return client;
    }

    public Tip getTip(){
        return Tip.IESIRE;
    }

    /**
     * Calculeaza profitul total al acestei facturi prin insumarea profitului fiecarei linii.
     *
     * @return Valoarea totala a profitului (Venituri - Costuri Marfuri).
     */
    public double getProfit(){
        double profit = 0;
        for(LinieFactura linieFactura : liniiFactura){
            profit += linieFactura.getProfitTotal();
        }
        return profit;
    }

    public double getProfitStatic(){
        return profitStatic;
    }
}