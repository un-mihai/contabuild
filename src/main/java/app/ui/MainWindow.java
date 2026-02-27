package app.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import java.awt.*;

/**
 * Fereastra principala a aplicatiei.
 * Gestioneaza navigarea intre paginile (panourile) UI si afiseaza meniul principal.
 */
public class MainWindow extends JFrame{

    private JPanel mainContainer;

    /**
     * Construieste fereastra, initializeaza containerul principal si afiseaza pagina principala.
     */
    public MainWindow(){
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setLayout(new BorderLayout(10,10));
        setSize(new Dimension(1150, 700));
        setLocationRelativeTo(null);

        mainContainer = new JPanel(new BorderLayout());
        add(mainContainer, BorderLayout.CENTER);

        afiseazaPaginaPrincipala();

        setVisible(true);
    }

    /**
     * Reseteaza continutul si afiseaza meniul principal cu butoanele de navigare.
     */
    public void afiseazaPaginaPrincipala(){
        setTitle("Contabuild - Pagina Principala");
        mainContainer.removeAll();

        JPanel continut = new JPanel(new GridLayout(2, 3, 20, 20));
        continut.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        continut.add(getPanouButonCreareIntrare());
        continut.add(getPanouButonCreareIesire());
        continut.add(getPanouButonVizualizareStoc());

        continut.add(getPanouButonIstoricFacturi());
        continut.add(getPanouButonAsistentAI());
        continut.add(getPanouButonRapoarte());

        mainContainer.add(continut, BorderLayout.CENTER);
        refreshUi();
    }

    private JPanel getPanouButonCreareIntrare(){
        JPanel panouButonCreareIntrare = new JPanel(new GridBagLayout());

        JButton butonCreareIntrare = new JButton("Creaza Intrare", getIconitaButon("/iconite/iconita_creare_intrare.png"));

        butonCreareIntrare.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                schimbaPagina(new PaginaCreareIntrare());
                setTitle("Contabuild - Creare Intrare");
            }
        });

        panouButonCreareIntrare.add(butonCreareIntrare);
        return panouButonCreareIntrare;
    }

    private JPanel getPanouButonCreareIesire(){
        JPanel panouButonCreareIntrare = new JPanel(new GridBagLayout());

        JButton butonCreareIntrare = new JButton("Creaza Iesire", getIconitaButon("/iconite/iconita_creare_iesire.png"));
        butonCreareIntrare.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                schimbaPagina(new PaginaCreareIesire());
                setTitle("Contabuild - Creare Iesire");
            }
        });

        panouButonCreareIntrare.add(butonCreareIntrare);
        return panouButonCreareIntrare;
    }

    private JPanel getPanouButonVizualizareStoc(){
        JPanel panouButonVizualizareStoc = new JPanel(new GridBagLayout());

        JButton butonInventar = new JButton("Vizualizare Stoc", getIconitaButon("/iconite/iconita_stoc.png"));
        butonInventar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                schimbaPagina(new PaginaInventar());
                setTitle("Contabuild - Inventar");
            }
        });

        panouButonVizualizareStoc.add(butonInventar);
        return panouButonVizualizareStoc;
    }

    private JPanel getPanouButonIstoricFacturi(){
        JPanel panou = new JPanel(new GridBagLayout());
        JButton buton = new JButton("Istoric Facturi", getIconitaButon("/iconite/iconita_istoric_facturi.png"));
        buton.addActionListener(e -> {
            schimbaPagina(new PaginaIstoricFacturi());
            setTitle("Contabuild - Istoric Facturi");
        });
        panou.add(buton);
        return panou;
    }

    private JPanel getPanouButonAsistentAI(){
        JPanel panouButonAsistentAI = new JPanel(new GridBagLayout());

        JButton butonAsistentAI = new JButton("Asistent AI", getIconitaButon("/iconite/iconita_asistent_ai.png"));
        butonAsistentAI.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                schimbaPagina(new PaginaAsistentAI());
                setTitle("Contabuild - Asistent AI");
            }
        });

        panouButonAsistentAI.add(butonAsistentAI);
        return panouButonAsistentAI;
    }

    private JPanel getPanouButonRapoarte(){
        JPanel panou = new JPanel(new GridBagLayout());
        JButton buton = new JButton("Rapoarte", getIconitaButon("/iconite/iconita_rapoarte.png"));
        buton.addActionListener(e -> {
            schimbaPagina(new PaginaRapoarte());
        });
        panou.add(buton);
        return panou;
    }

    /**
     * Inlocuieste continutul curent cu un nou panou si adauga un buton de intoarcere la meniul principal.
     */
    public void schimbaPagina(JPanel paginaNoua){
        mainContainer.removeAll();
        JButton buttonInapoi = new JButton("Inapoi");
        buttonInapoi.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                afiseazaPaginaPrincipala();
            }
        });
        mainContainer.add(buttonInapoi, BorderLayout.NORTH);
        mainContainer.add(paginaNoua, BorderLayout.CENTER);
        refreshUi();
    }

    /**
     * Reimprospateaza UI dupa modificari (repaint + revalidate).
     */
    public void refreshUi(){
        repaint();
        revalidate();
    }

    ImageIcon getIconitaButon(String adresa){
        ImageIcon iconita = new ImageIcon(getClass().getResource(adresa));
        Image imagine = iconita.getImage().getScaledInstance(64, 64, Image.SCALE_SMOOTH);

        return new ImageIcon(imagine);
    }
}
