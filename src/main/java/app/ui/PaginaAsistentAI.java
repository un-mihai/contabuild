package app.ui;

import app.model.Produs;
import app.repository.BazaDeDate;
import app.service.GroqService;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * Pagina care foloseste un serviciu AI (GroqService) pentru a genera o strategie de marketing
 * pe baza unui produs selectat din inventar.
 *
 */
public class PaginaAsistentAI extends JPanel{

    private JComboBox<Produs> comboProduse;
    private JTextArea zonaRaspuns;
    private JButton butonGenereaza;

    /**
     * Construieste pagina: incarca produsele in combo-box si pregateste zona de afisare a raspunsului.
     */
    public PaginaAsistentAI() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel panelNord = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel label = new JLabel("Alege produsul pentru promovare:");

        comboProduse = new JComboBox<>();

        List<Produs> produse = BazaDeDate.getInventar();
        for (Produs produs : produse) {
            comboProduse.addItem(produs);
        }

        butonGenereaza = new JButton("Genereaza Strategie");
        butonGenereaza.setBackground(new Color(70, 130, 180));
        butonGenereaza.setForeground(Color.WHITE);

        panelNord.add(label);
        panelNord.add(comboProduse);
        panelNord.add(butonGenereaza);

        add(panelNord, BorderLayout.NORTH);

        zonaRaspuns = new JTextArea();
        zonaRaspuns.setEditable(false);
        zonaRaspuns.setLineWrap(true);
        zonaRaspuns.setWrapStyleWord(true);
        zonaRaspuns.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        zonaRaspuns.setMargin(new Insets(10, 10, 10, 10));

        JScrollPane scroll = new JScrollPane(zonaRaspuns);
        scroll.setBorder(BorderFactory.createTitledBorder("Raspunsul asistentului AI:"));

        add(scroll, BorderLayout.CENTER);

        butonGenereaza.addActionListener(e -> {
            Produs produsSelectat = (Produs) comboProduse.getSelectedItem();

            if (produsSelectat != null) {
                zonaRaspuns.setText("Strategie in creare......");
                butonGenereaza.setEnabled(false);

                new Thread(() -> {
                    try {
                        String raspuns = GroqService.cereSfatMarketing(
                                produsSelectat.getNume(),
                                Double.parseDouble(String.format("%.2f", produsSelectat.getPretVanzareCuTva()))
                        );

                        SwingUtilities.invokeLater(() -> {
                            zonaRaspuns.setText(raspuns);
                            butonGenereaza.setEnabled(true);
                        });

                    } catch (Exception ex) {
                        SwingUtilities.invokeLater(() -> {
                            zonaRaspuns.setText("Eroare la conectare: " + ex.getMessage());
                            ex.printStackTrace();
                            butonGenereaza.setEnabled(true);
                        });
                    }
                }).start();
            } else {
                JOptionPane.showMessageDialog(this, "Selectati un produs din lista!");
            }
        });
    }
}