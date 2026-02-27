package app.ui;

import app.exception.ValidareException;
import app.model.FacturaIntrare;
import app.model.LinieFactura;
import app.model.Produs;
import app.repository.BazaDeDate;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Pagina pentru introducerea unei facturi de intrare (achizitie).
 * Permite completarea antetului (furnizor + numar) si adaugarea mai multor linii de produse,
 * apoi salveaza factura in baza de date.
 */
public class PaginaCreareIntrare extends JPanel {

    private String furnizor;
    private String numarFactura;
    private JPanel containerLinii;
    private boolean antetSalvat = false;

    /**
     * Initializeaza pagina si componentele UI.
     */
    public PaginaCreareIntrare(){
        creeazaPagina();
    }

    private JPanel getAntet(){
        JPanel panouAntet = new JPanel(new GridLayout(1, 5));

        JLabel etichetaFurnizor = new JLabel("Furnizor:");
        etichetaFurnizor.setHorizontalAlignment(SwingConstants.CENTER);
        JTextField textFurnizor = new JTextField(10);

        JLabel etichetaNumarFactura = new JLabel("Numar Factura:");
        etichetaNumarFactura.setHorizontalAlignment(SwingConstants.CENTER);
        JTextField textNumarFactura = new JTextField(10);

        JButton buttonAntet = new JButton("Salveaza");

        buttonAntet.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(buttonAntet.getText().equals("Salveaza")){
                    if(!textFurnizor.getText().isEmpty() && !textNumarFactura.getText().isEmpty()) {

                        buttonAntet.setText("Editeaza");

                        textFurnizor.setEditable(false);
                        textNumarFactura.setEditable(false);

                        furnizor = textFurnizor.getText();
                        numarFactura = textNumarFactura.getText();

                        antetSalvat = true;
                    }
                }
                else{
                    buttonAntet.setText("Salveaza");

                    textFurnizor.setEditable(true);
                    textNumarFactura.setEditable(true);

                    antetSalvat = false;
                }
            }
        });


        panouAntet.add(etichetaFurnizor);
        panouAntet.add(textFurnizor);

        panouAntet.add(etichetaNumarFactura);
        panouAntet.add(textNumarFactura);

        panouAntet.add(buttonAntet);
        panouAntet.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        return panouAntet;
    }


    private JPanel getPanouButonAdaugareLinieProdus(){
        JButton butonAdaugareLinieProdus = new JButton("Adauga Produs");
        butonAdaugareLinieProdus.addActionListener(e -> adaugaLinieProdus());

        JPanel panouButonAdaugareLinieProdus = new JPanel();
        panouButonAdaugareLinieProdus.add(butonAdaugareLinieProdus);

        return panouButonAdaugareLinieProdus;
    }

    private void adaugaLinieProdus(){
        int indexButonAdaugareLinieProdus = containerLinii.getComponentCount() - 2;
        if (indexButonAdaugareLinieProdus < 0){
            indexButonAdaugareLinieProdus = 0;
        }

        containerLinii.add(getPanouLinieProdus(), indexButonAdaugareLinieProdus);

        containerLinii.repaint();
        containerLinii.revalidate();

    }

    private void stergeLinieProdus(PanouLinieProdus linie){
        containerLinii.remove(linie);
        containerLinii.repaint();
        containerLinii.revalidate();
    }


    private JPanel getPanouLinieProdus(){
        PanouLinieProdus panouLinieProdus = new PanouLinieProdus();
        panouLinieProdus.seteazaActiuneStergere(this::stergeLinieProdus);
        return panouLinieProdus;
    }

    private JPanel getPanouButonSalvareFactura(){
        JButton butonSalvareFactura = new JButton("Salveaza Factura");
        butonSalvareFactura.addActionListener(e -> salvareFactura());

        JPanel panouButonSalvareFactura = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panouButonSalvareFactura.add(butonSalvareFactura);

        return panouButonSalvareFactura;
    }

    /**
     * Valideaza antetul si liniile, construieste FacturaIntrare si o baga in baza de date.
     * Daca o linie este incompleta sau contine valori invalide, operatia este oprita.
     */
    private void salvareFactura() {
        if(!antetSalvat){
            JOptionPane.showMessageDialog(this, "Te rog sa completezi antetul");
            return;
        }
        FacturaIntrare facturaIntrare = new FacturaIntrare(numarFactura, LocalDate.now(), furnizor);
        if(getLiniiFactura() != null) {
            for(LinieFactura linieFactura : getLiniiFactura()) {
                facturaIntrare.adaugaLinie(linieFactura);
            }
            try{
                BazaDeDate.salveazaFactura(facturaIntrare);
            }catch (SQLException e){
                JOptionPane.showMessageDialog(this, "Ceva problema la salvarea facturii");
                e.printStackTrace();
                return;
            }

            JOptionPane.showMessageDialog(this, "Factura salvata cu succes!");

            MainWindow mainWindow = (MainWindow) SwingUtilities.getWindowAncestor(this);
            mainWindow.schimbaPagina(new PaginaCreareIntrare());
        }
        else{
            JOptionPane.showMessageDialog(new JOptionPane(), "Trebuie cel putin un produs");
        }
    }

    /**
     * Colecteaza liniile de factura din panourile UI.
     * @return lista de linii sau null daca exista campuri incomplete / valori invalide.
     */
    private List<LinieFactura> getLiniiFactura(){
        List<LinieFactura> liniiFactura = new ArrayList<>();
        for (Component componenta : containerLinii.getComponents()){
            if(componenta instanceof PanouLinieProdus){
                PanouLinieProdus linie = (PanouLinieProdus) componenta;
                if(linie.isFinalizat()){
                    Produs produs = new Produs(linie.getNumeProdus(), linie.getPretUnitar(), linie.getAdaosComercial(), linie.getCotaTvaVanzare());
                    try{
                        liniiFactura.add(new LinieFactura(produs, linie.getCantitate(), linie.getPretUnitar(), linie.getCotaTva()));
                    }catch (ValidareException e){
                        JOptionPane.showMessageDialog(new JOptionPane(), "Nu toate datele sunt corecte");
                        return null;
                    }

                }else{
                    return null;
                }

            }
        }
        return liniiFactura;
    }

    private void creeazaPagina(){
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        add(getAntet());

        containerLinii = new JPanel();
        containerLinii.setLayout(new BoxLayout(containerLinii, BoxLayout.Y_AXIS));
        containerLinii.add(getPanouLinieProdus());
        containerLinii.add(getPanouButonAdaugareLinieProdus());
        containerLinii.add(Box.createVerticalGlue());

        JScrollPane scrollPane = new JScrollPane(containerLinii);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        add(scrollPane);
        add(getPanouButonSalvareFactura());
    }


}
