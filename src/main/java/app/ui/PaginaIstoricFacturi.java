package app.ui;

import app.model.Factura;
import app.model.FacturaIesire;
import app.model.FacturaIntrare;
import app.repository.BazaDeDate;
import app.service.PdfService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Pagina care afiseaza istoricul facturilor (intrari si iesiri).
 * Permite selectarea unei facturi si generarea unui PDF prin PdfService.
 */
public class PaginaIstoricFacturi extends JPanel{

    /**
     * Incarca facturile din baza de date, populeaza tabelul si configureaza actiunea de generare PDF.
     */
    public PaginaIstoricFacturi() {
        setLayout(new BorderLayout());

        String[] coloane = {
                "Tip", "Numar", "Data", "Partener",
                "Linii", "Valoare Baza", "TVA", "Total", "Profit"
        };

        DefaultTableModel model = new DefaultTableModel(coloane, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        List<Factura> facturi = BazaDeDate.getToateFacturile();

        for (Factura factura : facturi) {
            String tip = (factura instanceof FacturaIntrare) ? "Intrare" : "Iesire";
            String partener = (factura instanceof FacturaIntrare) ?
                    ((FacturaIntrare) factura).getVanzator() :
                    ((FacturaIesire) factura).getClient();

            String profitAfisat = "-";
            if (factura instanceof FacturaIesire) {
                profitAfisat = String.format("%.2f", ((FacturaIesire) factura).getProfitStatic());
            }

            int nrLinii = BazaDeDate.getNumarLiniiFactura(factura.getIdFactura());
            double valoareTva = factura.getValoareTotala() - factura.getValoareBaza();

            model.addRow(new Object[]{
                    tip,
                    factura.getNumarFactura(),
                    factura.getDataEmitere(),
                    partener,
                    nrLinii,
                    String.format("%.2f", factura.getValoareBaza()),
                    String.format("%.2f", valoareTva),
                    String.format("%.2f", factura.getValoareTotala()),
                    profitAfisat
            });
        }

        JTable tabel = new JTable(model);

        JScrollPane scrollPane = new JScrollPane(tabel);
        add(scrollPane, BorderLayout.CENTER);

        JPanel panouButoane = new JPanel();
        JButton butonGenerarePdf = new JButton("Genereaza PDF");
        butonGenerarePdf.addActionListener(e -> {
            int randSelectat = tabel.getSelectedRow();
            if(randSelectat != -1) {

                Factura facturaSelectata = facturi.get(randSelectat);

                BazaDeDate.incarcaLiniiFactura(facturaSelectata);

                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setDialogTitle("Salveaza Factura PDF");
                fileChooser.setSelectedFile(new java.io.File("Factura_" + facturaSelectata.getNumarFactura() + ".pdf"));

                int userSelection = fileChooser.showSaveDialog(this);

                if (userSelection == JFileChooser.APPROVE_OPTION) {
                    String caleFisier = fileChooser.getSelectedFile().getAbsolutePath();
                    if(!caleFisier.endsWith(".pdf")) {
                        caleFisier += ".pdf";
                    }

                    try {
                        PdfService.genereazaPdfFactura(facturaSelectata, caleFisier);
                        JOptionPane.showMessageDialog(this, "PDF generat cu succes!\n" + caleFisier);

                        Desktop.getDesktop().open(new java.io.File(caleFisier));

                    } catch (Exception ex) {
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(this, "Eroare la generare PDF: " + ex.getMessage());
                    }
                }
            } else {
                JOptionPane.showMessageDialog(this, "Selectati o factura din tabel!");
            }
        });

        panouButoane.add(butonGenerarePdf);
        add(panouButoane, BorderLayout.SOUTH);
    }
}