package com.gestionStock.ui;

import com.gestionStock.dao.GenericDao;
import com.gestionStock.dto.LigneStockDetailDto;
import com.gestionStock.model.Article;
import com.gestionStock.service.EtatStockService;
import com.gestionStock.ui.generic.FabriqueBouton;
import com.gestionStock.ui.generic.FabriqueTableau;

import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.time.LocalDate;
import java.util.List;

public class StockDetailFrame extends JFrame {

    private final EtatStockService etatStockService;
    private final GenericDao<Article> articleDao;

    private final JComboBox<Article> choixArticle;
    private final JTextField champDate;
    private final JPanel panneauTableau;

    public StockDetailFrame(Long articleIdInitial) {
        etatStockService = new EtatStockService();
        articleDao = new GenericDao<>(Article.class);

        setTitle("Etat Stock Detail");
        setSize(1100, 560);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel panneauHaut = new JPanel(new FlowLayout(FlowLayout.LEFT));

        choixArticle = new JComboBox<>();
        champDate = new JTextField(LocalDate.now().toString(), 10);

        panneauHaut.add(new JLabel("Produit: "));
        panneauHaut.add(choixArticle);
        panneauHaut.add(new JLabel("  Date limite (YYYY-MM-DD): "));
        panneauHaut.add(champDate);
        panneauHaut.add(FabriqueBouton.creer("Afficher", e -> rechargerTableau()));

        panneauTableau = new JPanel(new BorderLayout());

        add(panneauHaut, BorderLayout.NORTH);
        add(panneauTableau, BorderLayout.CENTER);

        try {
            List<Article> articles = articleDao.findAll();
            int indexSelection = -1;
            for (int i = 0; i < articles.size(); i++) {
                Article article = articles.get(i);
                choixArticle.addItem(article);
                if (articleIdInitial != null && articleIdInitial.equals(article.getId())) {
                    indexSelection = i;
                }
            }
            if (indexSelection >= 0) {
                choixArticle.setSelectedIndex(indexSelection);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
        rechargerTableau();
    }

    private void rechargerTableau() {
        try {
            Article article = (Article) choixArticle.getSelectedItem();
            if (article == null) {
                return;
            }

            LocalDate dateLimite = LocalDate.parse(champDate.getText().trim());
            List<LigneStockDetailDto> lignes = etatStockService.getStockDetailLignesByDate(article.getId(), dateLimite);

            JTable tableau = FabriqueTableau.creerDepuisDonneesEtGetters(lignes, LigneStockDetailDto.class
            );

            panneauTableau.removeAll();
            panneauTableau.add(new JLabel("Produit: " + article.getLibelle()), BorderLayout.NORTH);
            panneauTableau.add(new JScrollPane(tableau), BorderLayout.CENTER);
            panneauTableau.revalidate();
            panneauTableau.repaint();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }
}
