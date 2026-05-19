package com.gestionStock.ui;

import com.gestionStock.dto.LigneStockGlobalDto;
import com.gestionStock.service.EtatStockService;
import com.gestionStock.ui.generic.FabriqueBouton;
import com.gestionStock.ui.generic.FabriqueTableau;

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

public class StockGlobalFrame extends JFrame {

    private final EtatStockService etatStockService;

    private final JTextField champDate;
    private final JPanel panneauTableau;

    public StockGlobalFrame() {
        etatStockService = new EtatStockService();

        setTitle("Etat Stock Global");
        setSize(1000, 550);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel panneauHaut = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panneauHaut.add(new JLabel("Date limite (YYYY-MM-DD): "));
        champDate = new JTextField(LocalDate.now().toString(), 10);
        panneauHaut.add(champDate);
        panneauHaut.add(FabriqueBouton.creer("Filtrer", e -> rechargerTableau()));

        panneauTableau = new JPanel(new BorderLayout());

        add(panneauHaut, BorderLayout.NORTH);
        add(panneauTableau, BorderLayout.CENTER);

        rechargerTableau();
    }

    private void rechargerTableau() {
        try {
            LocalDate dateLimite = LocalDate.parse(champDate.getText().trim());
            List<LigneStockGlobalDto> lignes = etatStockService.getStockGlobalLignesByDate(dateLimite);

            JTable tableau = FabriqueTableau.creerDepuisDonneesEtGetters(
                    lignes,
                    LigneStockGlobalDto.class
            );

            tableau.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseClicked(java.awt.event.MouseEvent e) {
                    if (e.getClickCount() == 2) {
                        int ligneVue = tableau.getSelectedRow();
                        if (ligneVue >= 0) {
                            int ligneModele = tableau.convertRowIndexToModel(ligneVue);
                            Long articleId = lignes.get(ligneModele).getArticleId();
                            new StockDetailFrame(articleId).setVisible(true);
                        }
                    }
                }
            });

            panneauTableau.removeAll();
            panneauTableau.add(new JScrollPane(tableau), BorderLayout.CENTER);
            panneauTableau.revalidate();
            panneauTableau.repaint();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }
}
