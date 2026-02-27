package app.ui;

import app.model.RaportFinanciar;
import app.repository.BazaDeDate;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

/**
 * Pagina dedicata generarii de rapoarte financiare.
 * Permite utilizatorului sa selecteze o perioada si afiseaza totalurile calculate.
 */
public class PaginaRapoarte extends JPanel {

    private JTextField textDataStart;
    private JTextField textDataFinal;
    private JTextArea zonaRaport;

    public PaginaRapoarte() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel panouControl = new JPanel(new GridBagLayout());
        panouControl.setBorder(BorderFactory.createTitledBorder("Perioada Raportare"));

        JLabel lblStart = new JLabel("Data Start (YYYY-MM-DD): ");
        textDataStart = new JTextField(10);

        JLabel lblFinal = new JLabel("Data Final (YYYY-MM-DD): ");
        textDataFinal = new JTextField(10);

        populeazaDateLunaCurenta();

        JButton btnGenereaza = new JButton("Genereaza Raport");
        btnGenereaza.setBackground(new Color(70, 130, 180));
        btnGenereaza.setForeground(Color.WHITE);

        JButton btnLunaCurenta = new JButton("Luna Curenta");

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        gbc.gridx = 0; gbc.gridy = 0;
        panouControl.add(lblStart, gbc);

        gbc.gridx = 1; gbc.gridy = 0;
        panouControl.add(textDataStart, gbc);

        gbc.gridx = 2; gbc.gridy = 0;
        panouControl.add(lblFinal, gbc);

        gbc.gridx = 3; gbc.gridy = 0;
        panouControl.add(textDataFinal, gbc);

        gbc.gridx = 4; gbc.gridy = 0;
        panouControl.add(btnLunaCurenta, gbc);

        gbc.gridx = 5; gbc.gridy = 0;
        panouControl.add(btnGenereaza, gbc);

        add(panouControl, BorderLayout.NORTH);

        zonaRaport = new JTextArea();
        zonaRaport.setEditable(false);
        zonaRaport.setFont(new Font("Monospaced", Font.BOLD, 14));
        zonaRaport.setMargin(new Insets(15, 15, 15, 15));

        JScrollPane scroll = new JScrollPane(zonaRaport);
        scroll.setBorder(BorderFactory.createTitledBorder("Rezultat Financiar"));

        add(scroll, BorderLayout.CENTER);


        btnLunaCurenta.addActionListener(e -> populeazaDateLunaCurenta());

        btnGenereaza.addActionListener(e -> {
            genereazaRaport();
        });
    }

    private void populeazaDateLunaCurenta() {
        LocalDate azi = LocalDate.now();
        LocalDate inceput = azi.withDayOfMonth(1);
        LocalDate sfarsit = azi.withDayOfMonth(azi.lengthOfMonth());

        textDataStart.setText(inceput.toString());
        textDataFinal.setText(sfarsit.toString());
    }

    private void genereazaRaport() {
        try {
            String strStart = textDataStart.getText();
            String strFinal = textDataFinal.getText();

            if (strStart.isEmpty() || strFinal.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Va rugam completati ambele date.");
                return;
            }

            LocalDate dataStart = LocalDate.parse(strStart);
            LocalDate dataFinal = LocalDate.parse(strFinal);

            if (dataStart.isAfter(dataFinal)) {
                JOptionPane.showMessageDialog(this, "Data de start nu poate fi dupa data de final.");
                return;
            }

            RaportFinanciar raport = BazaDeDate.genereazaRaport(dataStart, dataFinal);

            zonaRaport.setText(raport.toString());

        } catch (DateTimeParseException ex) {
            JOptionPane.showMessageDialog(this, "Format data invalid! Folositi YYYY-MM-DD (ex: 2024-03-01)");
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Eroare la generare: " + ex.getMessage());
        }
    }
}