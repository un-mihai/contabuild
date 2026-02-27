package app.ui;

import app.model.Produs;
import app.repository.BazaDeDate;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
/**
 * Pagina de vizualizare a inventarului curent.
 * Afiseaza produsele intr-un JTable doar-citire, folosind datele din BazaDeDate.
 */
public class PaginaInventar extends JPanel{

    /**
     * Incarca inventarul si construieste tabelul de afisare.
     */
    public PaginaInventar(){
        setLayout(new BorderLayout());
        String[] numeColoane = {"Id", "Nume", "Cost Mediu", "Adaos Comercial", "Pret Vanzare", "Stoc Total"};

        DefaultTableModel modelTabel = new DefaultTableModel(numeColoane, 0){
            @Override
            public boolean isCellEditable(int row, int column){
                return false;
            }
        };

        List<Produs> produse = BazaDeDate.getInventar();
        for(Produs produs : produse){
            modelTabel.addRow(new Object[] {
                    produs.getIdProdus(),
                    produs.getNume(),
                    String.format("%.2f",produs.getCostMediu()),
                    String.format("%.2f",produs.getAdaosComercial()),
                    String.format("%.2f",produs.getPretVanzareCuTva()),
                    produs.getStocTotal()
            });
        }

        JTable tabel = new JTable(modelTabel);
        JScrollPane containerTabel = new JScrollPane(tabel);
        add(containerTabel, BorderLayout.CENTER);
    }
}
