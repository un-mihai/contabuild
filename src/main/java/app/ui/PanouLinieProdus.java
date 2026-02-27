package app.ui;

import app.model.Produs;
import app.repository.BazaDeDate;

import javax.swing.*;
import java.awt.*;
import java.util.function.Consumer;

/**
 * Panou reutilizabil pentru o linie de produs dintr-o factura de intrare.
 * Flux: utilizatorul verifica numele produsului (existent sau nou), completeaza cantitatea/pret/TVA
 * si apoi blocheaza valorile prin butonul de salvare.
 */
public class PanouLinieProdus extends JPanel {
    private Produs produs = null;

    private JLabel etichetaNumeProdus;
    private JTextField textNumeProdus;
    private JButton butonNumeProdus;
    private boolean numeBlocat = false;

    private JLabel etichetaCantitate;
    private JTextField textCantitate;

    private JLabel etichetaPretUnitar;
    private JTextField textPretUnitar;

    private JLabel etichetaCotaTva;
    private JTextField textCotaTva;

    private JLabel etichetaAdaosComerical;
    private JTextField textAdaosComercial;

    private JLabel etichetaCotaTvaVanzare;
    private JTextField textCotaTvaVanzare;

    private JButton butonValoriProdus;
    private boolean valoriBlocate = true;

    private JButton butonStergeProdus;

    /**
     * Construieste componenta UI pentru o linie de produs.
     */
    public PanouLinieProdus(){
        setLayout(new FlowLayout(FlowLayout.LEFT));

        etichetaNumeProdus = new JLabel("Nume Produs:");
        textNumeProdus = new JTextField(10);

        butonNumeProdus = new JButton("Verifica");
        butonNumeProdus.addActionListener(e -> gestioneazaActiuneNumeProdus());

        etichetaCantitate = new JLabel("Cantitate:");
        textCantitate = new JTextField(5);
        textCantitate.setEditable(false);

        etichetaPretUnitar = new JLabel("Pret Unitar:");
        textPretUnitar = new JTextField(5);
        textPretUnitar.setEditable(false);

        etichetaCotaTva = new JLabel("Cota TVA:");
        textCotaTva = new JTextField(5);
        textCotaTva.setEditable(false);

        etichetaAdaosComerical = new JLabel("Adaos Comercial:");
        textAdaosComercial = new JTextField(5);
        textAdaosComercial.setEditable(false);

        etichetaCotaTvaVanzare = new JLabel("Cota TVA Vanzare:");
        textCotaTvaVanzare = new JTextField(5);
        textCotaTvaVanzare.setEditable(false);

        butonValoriProdus = new JButton();
        butonValoriProdus.addActionListener(e -> gestioneazaActiuneValoriProdus());

        butonStergeProdus = new JButton("X");
        butonStergeProdus.setForeground(Color.RED);

        add(etichetaNumeProdus);
        add(textNumeProdus);
        add(butonNumeProdus);

        add(etichetaCantitate);
        add(textCantitate);

        add(etichetaPretUnitar);
        add(textPretUnitar);

        add(etichetaCotaTva);
        add(textCotaTva);

        add(etichetaAdaosComerical);
        add(textAdaosComercial);

        add(etichetaCotaTvaVanzare);
        add(textCotaTvaVanzare);

        add(butonValoriProdus);

        add(butonStergeProdus);


    }

    private void gestioneazaActiuneNumeProdus(){
        if(!numeBlocat){
            if(textNumeProdus.getText().isEmpty()){
                JOptionPane.showMessageDialog(this, "Campul numelui nu poate fi gol");
                return;
            }

            numeBlocat = true;
            butonNumeProdus.setText("Modifica");
            textNumeProdus.setEditable(false);

            if(produs == null || !produs.getNume().equals(textNumeProdus.getText())){
                produs = BazaDeDate.getProdusDupaNume(textNumeProdus.getText());
            }

            seteazaValoriEditabile(produs);

            valoriBlocate = false;
            butonValoriProdus.setText("Salveaza");
        }
        else{
            numeBlocat = false;
            butonNumeProdus.setText("Verifica");
            textNumeProdus.setEditable(true);

            seteazaValoriNeeditabile();

            butonValoriProdus.setText("");
        }
    }

    private void gestioneazaActiuneValoriProdus(){
        if(numeBlocat) {
            if (valoriBlocate) {
                butonValoriProdus.setText("Salveaza");
                seteazaValoriEditabile(produs);
            } else {
                if (textCantitate.getText().isEmpty()) {
                    JOptionPane.showMessageDialog(this.getParent(), "Te rog completeaza cantitatea");
                    return;
                } else {
                    try {
                        Integer.parseInt(textCantitate.getText());
                    } catch (NumberFormatException e) {
                        JOptionPane.showMessageDialog(this.getParent(), "Cantitatea nu este un numar valid");
                        return;
                    }
                }


                if (textPretUnitar.getText().isEmpty()) {
                    JOptionPane.showMessageDialog(this.getParent(), "Te rog completeaza pretul unitar");
                    return;
                } else {
                    try {
                        Double.parseDouble(textPretUnitar.getText());
                    } catch (NumberFormatException e) {
                        JOptionPane.showMessageDialog(this.getParent(), "Pretul unitar nu este un numar valid");
                        return;
                    }
                }

                if (textCotaTva.getText().isEmpty()) {
                    JOptionPane.showMessageDialog(this.getParent(), "Te rog completeaza cota de TVA");
                    return;
                } else {
                    try {
                        Double.parseDouble(textCotaTva.getText());
                    } catch (NumberFormatException e) {
                        JOptionPane.showMessageDialog(this.getParent(), "Cantitatea nu este un numar valid");
                        return;
                    }
                }

                if (textAdaosComercial.getText().isEmpty()) {
                    JOptionPane.showMessageDialog(this.getParent(), "Te rog completeaza adaosul comercial");
                    return;
                } else {
                    try {
                        Double.parseDouble(textAdaosComercial.getText());
                    } catch (NumberFormatException e) {
                        JOptionPane.showMessageDialog(this.getParent(), "Adaosul comercial nu este un numar valid");
                        return;
                    }
                }

                if (textCotaTvaVanzare.getText().isEmpty()) {
                    JOptionPane.showMessageDialog(this.getParent(), "Te rog completeaza cota de TVA la vanzare");
                    return;
                } else {
                    try {
                        Double.parseDouble(textCotaTvaVanzare.getText());
                    } catch (NumberFormatException e) {
                        JOptionPane.showMessageDialog(this.getParent(), "Cota TVA vanzare nu este un numar valid");
                        return;
                    }
                }

                butonValoriProdus.setText("Modifica");
                seteazaValoriNeeditabile();
            }
        }

    }

    private void seteazaValoriEditabile(Produs produs){
        valoriBlocate = false;

        textCantitate.setEditable(true);
        textPretUnitar.setEditable(true);
        textCotaTva.setEditable(true);

        if(produs != null){
            textAdaosComercial.setText(String.format("%.2f", produs.getAdaosComercial()));
            textAdaosComercial.setEditable(false);

            textCotaTvaVanzare.setText(String.format("%.2f", produs.getCotaTvaVanzare()));
            textCotaTvaVanzare.setEditable(false);
        }
        else{
            textAdaosComercial.setEditable(true);
            textCotaTvaVanzare.setEditable(true);
        }
    }

    private void seteazaValoriNeeditabile(){
        valoriBlocate = true;

        textCantitate.setEditable(false);
        textPretUnitar.setEditable(false);
        textCotaTva.setEditable(false);
        textAdaosComercial.setEditable(false);
        textCotaTvaVanzare.setEditable(false);
    }

    /**
     * Seteaza callback-ul apelat cand utilizatorul apasa butonul de stergere.
     * @param functiaDeStergereDinParinte functie primita din panoul parinte, care va elimina aceasta linie din UI.
     */
    public void seteazaActiuneStergere(Consumer<PanouLinieProdus> functiaDeStergereDinParinte) {
        for (var al : butonStergeProdus.getActionListeners()) {
            butonStergeProdus.removeActionListener(al);
        }

        butonStergeProdus.addActionListener(e -> functiaDeStergereDinParinte.accept(this));
    }

    /**
     * Indica daca linia este completata si blocata (gata de salvare in factura).
     */
    public boolean isFinalizat(){
        return numeBlocat && valoriBlocate;
    }

    public String getNumeProdus(){
        return textNumeProdus.getText();
    }

    public int getCantitate(){
        return Integer.parseInt(textCantitate.getText());
    }

    public double getPretUnitar(){
        return Double.parseDouble(textPretUnitar.getText());
    }

    public double getCotaTva(){
        return Double.parseDouble(textCotaTva.getText());
    }

    public double getAdaosComercial(){
        return Double.parseDouble(textAdaosComercial.getText());
    }

    public double getCotaTvaVanzare(){
        return Double.parseDouble(textCotaTvaVanzare.getText());
    }

}
