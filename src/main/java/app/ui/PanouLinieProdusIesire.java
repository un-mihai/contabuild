package app.ui;

import app.model.Produs;
import app.repository.BazaDeDate;

import javax.swing.*;
import java.awt.*;
import java.util.function.Consumer;

/**
 * Panou reutilizabil pentru o linie de produs dintr-o factura de iesire (vanzare).
 * Permite cautarea produsului in inventar si introducerea cantitatii vandute, cu afisarea stocului curent.
 */
public class PanouLinieProdusIesire extends JPanel {
    private Produs produs = null;

    private JLabel etichetaNumeProdus;
    private JTextField textNumeProdus;
    private JButton butonNumeProdus;
    private boolean numeBlocat = false;

    private JLabel etichetaCantitate;
    private JTextField textCantitate;
    private JLabel etichetaStoc;

    private JButton butonSterge;

    /**
     * Construieste componenta UI pentru selectarea produsului si introducerea cantitatii.
     */
    public PanouLinieProdusIesire() {
        setLayout(new FlowLayout(FlowLayout.LEFT));

        etichetaNumeProdus = new JLabel("Produs:");
        textNumeProdus = new JTextField(15);
        butonNumeProdus = new JButton("Cauta");
        butonNumeProdus.addActionListener(e -> gestioneazaActiuneNumeProdus());

        etichetaCantitate = new JLabel("Cantitate:");
        textCantitate = new JTextField(5);
        textCantitate.setEditable(false);

        etichetaStoc = new JLabel(" (Stoc: -)");
        etichetaStoc.setForeground(Color.BLUE);
        etichetaStoc.setVisible(false);

        butonSterge = new JButton("X");
        butonSterge.setForeground(Color.RED);

        add(etichetaNumeProdus);
        add(textNumeProdus);
        add(butonNumeProdus);
        add(etichetaCantitate);
        add(textCantitate);
        add(etichetaStoc);
        add(butonSterge);
    }

    private void gestioneazaActiuneNumeProdus() {
        if (!numeBlocat) {
            Produs produs = BazaDeDate.getProdusDupaNume(textNumeProdus.getText());
            if (produs != null) {
                this.produs = produs;
                numeBlocat = true;

                butonNumeProdus.setText("Schimba");
                textNumeProdus.setEditable(false);
                textCantitate.setEditable(true);
                textCantitate.requestFocus();

                etichetaStoc.setText(" (Stoc actual: " + this.produs.getStocTotal() + ")");
                etichetaStoc.setVisible(true);

                if (this.produs.getStocTotal() == 0) {
                    etichetaStoc.setForeground(Color.RED);
                    JOptionPane.showMessageDialog(this, "Atentie! Produsul nu este pe stoc.");
                } else {
                    etichetaStoc.setForeground(Color.BLUE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Produsul nu a fost gasit!");
            }
        } else {
            numeBlocat = false;
            produs = null;

            butonNumeProdus.setText("Cauta");
            textNumeProdus.setEditable(true);

            textCantitate.setText("");
            textCantitate.setEditable(false);

            etichetaStoc.setText(" (Stoc: -)");
            etichetaStoc.setVisible(false);
        }
    }

    /**
     * Seteaza callback-ul pentru stergerea liniei din panoul parinte.
     * @param functiaDeStergere functie care va elimina aceasta linie din containerul parinte.
     */
    public void seteazaActiuneStergere(Consumer<PanouLinieProdusIesire> functiaDeStergere) {
        for (var al : butonSterge.getActionListeners()) {
            butonSterge.removeActionListener(al);
        }
        butonSterge.addActionListener(e -> functiaDeStergere.accept(this));
    }

    /**
     * Verifica daca linia este valida: produs selectat si cantitate > 0.
     */
    public boolean isFinalizat() {
        if (produs == null) return false;
        try {
            int cantitate = Integer.parseInt(textCantitate.getText());
            return cantitate > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public Produs getProdus() {
        return produs;
    }

    public int getCantitate() {
        try {
            return Integer.parseInt(textCantitate.getText());
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}