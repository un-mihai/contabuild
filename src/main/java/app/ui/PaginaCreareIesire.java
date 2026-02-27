package app.ui;

import app.exception.ValidareException;
import app.model.FacturaIesire;
import app.model.LinieFactura;
import app.model.Produs;
import app.repository.BazaDeDate;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Pagina pentru crearea unei facturi de iesire (vanzare).
 * Genereaza automat numarul facturii, permite selectarea produselor din stoc si validarea cantitatilor,
 * apoi salveaza factura si actualizeaza stocul prin BazaDeDate.
 */
public class PaginaCreareIesire extends JPanel {

    private String client;
    private String numarFactura;
    private JPanel containerLinii;
    private boolean antetSalvat = false;

    /**
     * Initializeaza pagina si componentele UI.
     */
    public PaginaCreareIesire() {
        creeazaPagina();
    }


    private JPanel getAntet() {
        JPanel panouAntet = new JPanel(new GridLayout(1, 5));

        JLabel etichetaClient = new JLabel("Client:");
        etichetaClient.setHorizontalAlignment(SwingConstants.CENTER);
        JTextField textClient = new JTextField(10);

        JLabel etichetaNumar = new JLabel("Numar Factura:");
        etichetaNumar.setHorizontalAlignment(SwingConstants.CENTER);
        JTextField textNumarFactura = new JTextField(10);
        String numarGenerat = BazaDeDate.genereazaUrmatorulNumarFacturaIesire();
        textNumarFactura.setText(numarGenerat);
        textNumarFactura.setEditable(false);
        numarFactura = textNumarFactura.getText();

        JButton buttonAntet = new JButton("Confirma Antet");

        buttonAntet.addActionListener(e -> {
            if (buttonAntet.getText().equals("Confirma Antet")) {
                if(!textClient.getText().isEmpty()){
                    buttonAntet.setText("Editeaza");

                    textClient.setEditable(false);
                    client = textClient.getText();

                    antetSalvat = true;
                }
            } else {
                buttonAntet.setText("Confirma Antet");
                textClient.setEditable(true);
                antetSalvat = false;
            }
        });

        panouAntet.add(etichetaClient);
        panouAntet.add(textClient);

        panouAntet.add(etichetaNumar);
        panouAntet.add(textNumarFactura);

        panouAntet.add(buttonAntet);
        panouAntet.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        return panouAntet;
    }

    private JPanel getPanouButonAdaugareLinieProdus() {
        JButton butonAdaugareLinieProdus = new JButton("Adauga Produs");
        butonAdaugareLinieProdus.addActionListener(e -> adaugaLinieProdus());

        JPanel panouButonAdaugareLinieProdus = new JPanel();
        panouButonAdaugareLinieProdus.add(butonAdaugareLinieProdus);

        return panouButonAdaugareLinieProdus;
    }

    private void adaugaLinieProdus() {
        int indexButonAdaugareLinieProdus = containerLinii.getComponentCount() - 2;
        if (indexButonAdaugareLinieProdus < 0){
            indexButonAdaugareLinieProdus = 0;
        }

        PanouLinieProdusIesire panou = new PanouLinieProdusIesire();
        panou.seteazaActiuneStergere(this::stergeLinie);

        containerLinii.add(panou, indexButonAdaugareLinieProdus);
        refreshUi();
    }

    private void stergeLinie(PanouLinieProdusIesire linie) {
        containerLinii.remove(linie);
        refreshUi();
    }

    private JPanel getPanouSalvareFactura() {
        JButton butonSalvareFactura = new JButton("Finalizeaza Vanzarea");
        butonSalvareFactura.addActionListener(e -> salvareFactura());

        JPanel panouButonSalvareFactura = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panouButonSalvareFactura.add(butonSalvareFactura);

        return panouButonSalvareFactura;
    }

    /**
     * Valideaza antetul si liniile, construieste FacturaIesire si o salveaza in baza de date.
     * Include verificari de stoc (cantitate > 0 si cantitate <= stoc).
     */
    private void salvareFactura() {
        if (!antetSalvat) {
            JOptionPane.showMessageDialog(this, "Completeaza antetul facturii!");
            return;
        }

        List<LinieFactura> linii = getLiniiFactura();
        if (linii == null || linii.isEmpty()) {
            return;
        }

        FacturaIesire factura = new FacturaIesire(numarFactura, LocalDate.now(), client);
        try {
            for (LinieFactura linie : linii) {
                factura.adaugaLinie(linie);
            }
            BazaDeDate.salveazaFactura(factura);
            JOptionPane.showMessageDialog(this, "Factura salvata cu succes!");

            MainWindow mainWindow = (MainWindow) SwingUtilities.getWindowAncestor(this);
            mainWindow.schimbaPagina(new PaginaCreareIesire());

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Eroare la salvare: " + e.getMessage());
        }
    }

    /**
     * Colecteaza liniile de vanzare din panourile UI.
     * @return lista de linii sau null daca exista erori de validare (cantitate/stoc/date).
     */
    private List<LinieFactura> getLiniiFactura() {
        List<LinieFactura> liniiFactura = new ArrayList<>();

        for (Component componenta : containerLinii.getComponents()) {
            if (componenta instanceof PanouLinieProdusIesire) {
                PanouLinieProdusIesire panouLinieProdus = (PanouLinieProdusIesire) componenta;

                if (panouLinieProdus.isFinalizat()) {
                    Produs produs = panouLinieProdus.getProdus();
                    int cantitate = panouLinieProdus.getCantitate();

                    if (cantitate == 0){
                        JOptionPane.showMessageDialog(this, "Cantitate invalida pentru: " + produs.getNume());
                        return null;
                    }
                    if (cantitate > produs.getStocTotal()) {
                        JOptionPane.showMessageDialog(this, "Stoc insuficient pentru: " + produs.getNume());
                        return null;
                    }

                    try {
                        LinieFactura linie = new LinieFactura(
                                produs,
                                cantitate,
                                produs.getPretVanzareFaraTva(),
                                produs.getCotaTvaVanzare()
                        );
                        liniiFactura.add(linie);
                    } catch (ValidareException e) {
                        JOptionPane.showMessageDialog(this, "Date invalide la produsul " + produs.getNume());
                        return null;
                    }
                } else {
                    if (panouLinieProdus.getProdus() != null) {
                        JOptionPane.showMessageDialog(this, "Completati cu o cantitate valida pentru " + panouLinieProdus.getProdus().getNume());
                        return null;
                    }
                }
            }
        }

        if (liniiFactura.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Adaugati cel putin un produs.");
            return null;
        }

        return liniiFactura;
    }

    private void creeazaPagina() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        add(getAntet());

        containerLinii = new JPanel();
        containerLinii.setLayout(new BoxLayout(containerLinii, BoxLayout.Y_AXIS));

        PanouLinieProdusIesire primaLinie = new PanouLinieProdusIesire();
        primaLinie.seteazaActiuneStergere(this::stergeLinie);
        containerLinii.add(primaLinie);

        containerLinii.add(getPanouButonAdaugareLinieProdus());
        containerLinii.add(Box.createVerticalGlue());

        JScrollPane scroll = new JScrollPane(containerLinii);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        add(scroll);

        add(getPanouSalvareFactura());
    }

    private void refreshUi() {
        containerLinii.repaint();
        containerLinii.revalidate();
    }
}