package app.model;

/**
 * Reprezinta un produs din stocul magazinului.
 * Contine informatii despre costuri, adaos comercial si stocul actual.
 */
public class Produs {
    private int idProdus;
    private String nume;
    private double costMediu;
    private double adaosComercial;
    private double cotaTvaVanzare;
    private int stocTotal;

    /**
     * Constructor complet pentru un produs existent (incarcat din baza de date).
     */
    public Produs(int idProdus, String nume, double costMediu, double adaosComercial, double cotaTvaVanzare, int stocTotal) {
        this.idProdus = idProdus;
        this.nume = nume;
        this.costMediu = costMediu;
        this.adaosComercial = adaosComercial;
        this.cotaTvaVanzare = cotaTvaVanzare;
        this.stocTotal = stocTotal;
    }

    /**
     * Constructor simplu pentru un produs introdus de catre utilizator.
     * (id-ul  si stocul sunt initializate cu 0)
     */
    public Produs(String nume, double costMediu, double adaosComercial, double cotaTvaVanzare) {
        this.idProdus = 0;
        this.nume = nume;
        this.costMediu = costMediu;
        this.adaosComercial = adaosComercial;
        this.cotaTvaVanzare = cotaTvaVanzare;
        this.stocTotal = 0;
    }

    public int getIdProdus() {
        return idProdus;
    }

    public String getNume() {
        return nume;
    }

    public double getCostMediu() {
        return costMediu;
    }

    public double getAdaosComercial() {
        return adaosComercial;
    }

    public double getPretVanzareFaraTva() {
        return costMediu * (1 + adaosComercial/100.00);
    }

    public double getCotaTvaVanzare() {
        return cotaTvaVanzare;
    }

    public double getPretVanzareCuTva(){
        return getPretVanzareFaraTva() * (1 + cotaTvaVanzare/100.00);
    }

    public int getStocTotal(){
        return stocTotal;
    }

    public String toString() {
        return String.format("ID: %d Nume: %s Pret vanzare: %.2f Stoc: %d",
                idProdus, nume, getPretVanzareFaraTva(), stocTotal);
    }
}

