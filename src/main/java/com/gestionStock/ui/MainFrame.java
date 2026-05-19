package com.gestionStock.ui;

import com.gestionStock.ui.generic.FabriqueBouton;

import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.GridLayout;

public class MainFrame extends JFrame {

    public MainFrame() {
        setTitle("Gestion de Stock");
        setSize(520, 220);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panneau = new JPanel(new GridLayout(2, 2, 12, 12));

        panneau.add(FabriqueBouton.creer("Saisie Article", e -> new ArticleFrame().setVisible(true)));
        panneau.add(FabriqueBouton.creer("Saisie Mouvement", e -> new MouvementFrame().setVisible(true)));
        panneau.add(FabriqueBouton.creer("Etat Stock Global", e -> new StockGlobalFrame().setVisible(true)));
        panneau.add(FabriqueBouton.creer("Etat Stock Detail", e -> new StockDetailFrame(null).setVisible(true)));

        setContentPane(panneau);
    }
}
