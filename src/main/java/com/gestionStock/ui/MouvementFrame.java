package com.gestionStock.ui;

import com.gestionStock.dao.GenericDao;
import com.gestionStock.model.Article;
import com.gestionStock.model.Mouvement;
import com.gestionStock.model.TypeMouvement;
import com.gestionStock.service.StockService;
import com.gestionStock.ui.generic.FabriqueBouton;
import com.gestionStock.ui.generic.FabriqueTableau;

import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.math.BigDecimal;
import java.time.LocalDate;

public class MouvementFrame extends JFrame {

    private final GenericDao<Article> articleDao;

    private final StockService stockService;

    private final JComboBox<Article> choixArticle = new JComboBox<Article>();
    private final JComboBox<TypeMouvement> choixType;

    private final JTextField champDate;
    private final JTextField champQte;
    private final JTextField champPu;

    private final JPanel panneauTableau;

    public MouvementFrame() {
        articleDao = new GenericDao<>(Article.class);
        stockService = new StockService();

        setTitle("Saisie Mouvement");
        setSize(1000, 550);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel panneauFormulaire = new JPanel();
        panneauFormulaire.setLayout(new BoxLayout(panneauFormulaire, BoxLayout.X_AXIS));

        choixType = new JComboBox<>(TypeMouvement.values());

        champDate = new JTextField(10);
        champDate.setText(LocalDate.now().toString());
        champQte = new JTextField(7);
        champPu = new JTextField(8);

        choixType.addActionListener(e -> adapterChampPrix());

        panneauFormulaire.add(new JLabel("Produit: "));
        panneauFormulaire.add(choixArticle);
        panneauFormulaire.add(new JLabel("  Type: "));
        panneauFormulaire.add(choixType);
        panneauFormulaire.add(new JLabel("  Date (YYYY-MM-DD): "));
        panneauFormulaire.add(champDate);
        panneauFormulaire.add(new JLabel("  Qte: "));
        panneauFormulaire.add(champQte);
        panneauFormulaire.add(new JLabel("  PU: "));
        panneauFormulaire.add(champPu);
        panneauFormulaire.add(FabriqueBouton.creer("Enregistrer", e -> enregistrerMouvement()));

        panneauTableau = new JPanel(new BorderLayout());

        add(panneauFormulaire, BorderLayout.NORTH);
        add(panneauTableau, BorderLayout.CENTER);

        chargerListes();
        rechargerTableau();
        adapterChampPrix();
    }

    private void chargerListes() {
        try {
            choixArticle.removeAllItems();
            for (Article article : articleDao.findAll()) {
                choixArticle.addItem(article);
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void adapterChampPrix() {
        TypeMouvement type = (TypeMouvement) choixType.getSelectedItem();
        boolean entree = type == TypeMouvement.ENTREE;
        champPu.setEnabled(entree);
        if (!entree) {
            champPu.setText("auto");
        } else if ("auto".equalsIgnoreCase(champPu.getText().trim())) {
            champPu.setText("");
        }
    }

    private void enregistrerMouvement() {
        try {
            Article article = (Article) choixArticle.getSelectedItem();
            TypeMouvement type = (TypeMouvement) choixType.getSelectedItem();

            if (article == null || type == null) {
                JOptionPane.showMessageDialog(this, "Article et type sont obligatoires.");
                return;
            }

            LocalDate date = LocalDate.parse(champDate.getText().trim());
            BigDecimal qte = new BigDecimal(champQte.getText().trim());

            if (type == TypeMouvement.ENTREE) {
                BigDecimal pu = new BigDecimal(champPu.getText().trim());
                stockService.enregistrerEntree(article, date, qte, pu);
            } else {
                stockService.enregistrerSortie(article,date, qte);
            }

            champQte.setText("");
            if (type == TypeMouvement.ENTREE) {
                champPu.setText("");
            }

            rechargerTableau();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void rechargerTableau() {
        try {
            JTable tableau = FabriqueTableau.creerDepuisDaoTrieDateHeure(
                    Mouvement.class,
                    Mouvement::getDateMouvement,
                    true
            );

            panneauTableau.removeAll();
            panneauTableau.add(new JScrollPane(tableau), BorderLayout.CENTER);
            panneauTableau.revalidate();
            panneauTableau.repaint();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }
}
