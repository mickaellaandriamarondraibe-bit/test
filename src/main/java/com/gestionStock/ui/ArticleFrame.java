package com.gestionStock.ui;

import com.gestionStock.dao.GenericDao;
import com.gestionStock.model.Article;
import com.gestionStock.ui.generic.FabriqueBouton;
import com.gestionStock.ui.generic.FabriqueTableau;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import java.awt.BorderLayout;

public class ArticleFrame extends JFrame {

    private final GenericDao<Article> articleDao;
    private final JTextField champLibelle;
    private final JPanel panneauTableau;

    public ArticleFrame() {
        this.articleDao = new GenericDao<>(Article.class);

        setTitle("Saisie Article");
        setSize(700, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel panneauFormulaire = new JPanel();
        panneauFormulaire.setLayout(new BoxLayout(panneauFormulaire, BoxLayout.X_AXIS));

        JLabel labelLibelle = new JLabel("Libelle produit: ");
        champLibelle = new JTextField(25);

        panneauFormulaire.add(labelLibelle);
        panneauFormulaire.add(champLibelle);
        panneauFormulaire.add(FabriqueBouton.creer("Enregistrer", e -> enregistrerArticle()));

        panneauTableau = new JPanel(new BorderLayout());

        add(panneauFormulaire, BorderLayout.NORTH);
        add(panneauTableau, BorderLayout.CENTER);

        rechargerTableau();
    }

    private void enregistrerArticle() {
        try {
            String libelle = champLibelle.getText().trim();
            if (libelle.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Le libelle est obligatoire.");
                return;
            }

            articleDao.save(new Article(libelle));
            champLibelle.setText("");
            rechargerTableau();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void rechargerTableau() {
        try {
            JTable tableau = FabriqueTableau.creerDepuisDao(Article.class);
            panneauTableau.removeAll();
            panneauTableau.add(new JScrollPane(tableau), BorderLayout.CENTER);
            panneauTableau.revalidate();
            panneauTableau.repaint();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }
}
