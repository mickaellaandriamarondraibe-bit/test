package com.gestionStock.ui.generic;

import javax.swing.JButton;
import java.awt.event.ActionListener;

public final class FabriqueBouton {

    private FabriqueBouton() {
    }

    public static JButton creer(String texte) {
        return new JButton(texte);
    }

    public static JButton creer(String texte, ActionListener action) {
        JButton bouton = new JButton(texte);
        if (action != null) {
            bouton.addActionListener(action);
        }
        return bouton;
    }

    public static JButton creerDesactive(String texte) {
        JButton bouton = new JButton(texte);
        bouton.setEnabled(false);
        return bouton;
    }
}
