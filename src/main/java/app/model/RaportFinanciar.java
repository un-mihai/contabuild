package app.model;

import java.time.LocalDate;

public class RaportFinanciar {
    private LocalDate dataStart;
    private LocalDate dataFinal;

    private double totalCheltuieliBaza;
    private double totalTvaDeductibil;

    private double totalVanzariBaza;
    private double totalTvaColectat;
    private double totalProfit;

    public RaportFinanciar(LocalDate start, LocalDate end) {
        this.dataStart = start;
        this.dataFinal = end;
    }

    public void adaugaLaCheltuieli(double baza, double tva) {
        this.totalCheltuieliBaza += baza;
        this.totalTvaDeductibil += tva;
    }

    public void adaugaLaVanzari(double baza, double tva, double profit) {
        this.totalVanzariBaza += baza;
        this.totalTvaColectat += tva;
        this.totalProfit += profit;
    }

    public double getSoldTva() {
        return totalTvaColectat - totalTvaDeductibil;
    }

    @Override
    public String toString() {
        return String.format(
                "Raport (%s - %s):\n" +
                        "--------------------------\n" +
                        "Vanzari (fara TVA):  %.2f RON\n" +
                        "Cost Marfa Vanduta:  %.2f RON\n" +
                        "Profit Brut:         %.2f RON\n" +
                        "--------------------------\n" +
                        "Achizitii (fara TVA):%.2f RON\n" +
                        "--------------------------\n" +
                        "TVA Colectat:        %.2f RON\n" +
                        "TVA Deductibil:      %.2f RON\n" +
                        "Sold TVA:            %.2f RON",
                dataStart, dataFinal,
                totalVanzariBaza,
                (totalVanzariBaza - totalProfit), // Costul marfii
                totalProfit,
                totalCheltuieliBaza,
                totalTvaColectat,
                totalTvaDeductibil,
                getSoldTva()
        );
    }
}