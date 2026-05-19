package com.gestionStock.service;

import com.gestionStock.dao.GenericDao;
import com.gestionStock.model.Article;
import com.gestionStock.model.Methode;
import com.gestionStock.model.Mouvement;
import com.gestionStock.model.TypeMouvement;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StockService {

    private final GenericDao<Mouvement> mouvementDao;

    public StockService() {
        this.mouvementDao = new GenericDao<>(Mouvement.class);
    }

    public Mouvement enregistrerEntree( Article article, Methode methode, LocalDate date, BigDecimal qte, BigDecimal pu
    ) throws Exception {

        Mouvement dernier = getDernierMouvement(article);

        BigDecimal ancienStock = getStockApres(dernier);
        BigDecimal ancienneValeurStock = getValeurStockApres(dernier);

        BigDecimal valeur = qte.multiply(pu);
        BigDecimal stockApres = ancienStock.add(qte);
        BigDecimal valeurStockApres = ancienneValeurStock.add(valeur);

        BigDecimal cumpApres = valeurStockApres.divide(
                stockApres,
                2,
                RoundingMode.HALF_UP    
        );

        Mouvement mouvement = new Mouvement();
        mouvement.setArticleId(article.getId());
        mouvement.setMethodeId(methode.getId());
        mouvement.setDateMouvement(date);
        mouvement.setTypeMouvement(TypeMouvement.ENTREE);
        mouvement.setQte(qte);
        mouvement.setPu(pu);
        mouvement.setValeur(valeur);
        mouvement.setStockApres(stockApres);
        mouvement.setValeurStockApres(valeurStockApres);
        mouvement.setCumpApres(cumpApres);

        return mouvementDao.save(mouvement);
    }

    
    public Mouvement enregistrerSortie(
            Article article,
            Methode methode,
            LocalDate date,
            BigDecimal qte
    ) throws Exception {

        Mouvement dernier = getDernierMouvement(article);

        if (dernier == null) {
            throw new IllegalArgumentException("Aucun stock disponible pour cet article.");
        }

        BigDecimal ancienStock = dernier.getStockApres();
        BigDecimal ancienneValeurStock = dernier.getValeurStockApres();
        BigDecimal dernierCump = dernier.getCumpApres();

        if (qte.compareTo(ancienStock) > 0) {
            throw new IllegalArgumentException("Stock insuffisant.");
        }

        BigDecimal pu;
        BigDecimal valeur;

        String libelleMethode = methode.getLibelle() == null ? "" : methode.getLibelle().trim().toUpperCase();
        if ("FIFO".equals(libelleMethode)) {
            valeur = methode(article.getId(), qte, true);
            pu = valeur.divide(qte, 2, RoundingMode.HALF_UP);
        } else if ("LIFO".equals(libelleMethode)) {
            valeur = methode(article.getId(), qte, false);
            pu = valeur.divide(qte, 2, RoundingMode.HALF_UP);
        } else {
            pu = dernierCump;
            valeur = qte.multiply(pu);
        }

        BigDecimal stockApres = ancienStock.subtract(qte);
        BigDecimal valeurStockApres = ancienneValeurStock.subtract(valeur);

        Mouvement mouvement = new Mouvement();
        mouvement.setArticleId(article.getId());
        mouvement.setMethodeId(methode.getId());
        mouvement.setDateMouvement(date);
        mouvement.setTypeMouvement(TypeMouvement.SORTIE);
        mouvement.setQte(qte);
        mouvement.setPu(pu);
        mouvement.setValeur(valeur);
        mouvement.setStockApres(stockApres);
        mouvement.setValeurStockApres(valeurStockApres);
        mouvement.setCumpApres(dernierCump);

        return mouvementDao.save(mouvement);
    }

    private Mouvement getDernierMouvement(Article article) throws Exception {
        List<Mouvement> mouvements = mouvementDao.findAll();

        Mouvement dernier = null;

        for (Mouvement mouvement : mouvements) {
            if (mouvement.getArticleId().equals(article.getId())) {
                if (dernier == null || mouvement.getId() > dernier.getId()) {
                    dernier = mouvement;
                }
            }
        }

        return dernier;
    }

    private BigDecimal methode(Long articleId, BigDecimal qteSortie, boolean fifo) throws Exception {
        BigDecimal qteRestante = qteSortie;
        BigDecimal valeur = BigDecimal.ZERO;
        List<Long> idsExclus = new ArrayList<>();
        Map<Long, BigDecimal> restantParEntree = new HashMap<>();

        while (!verificationQte(qteRestante)) {
            Mouvement articleRef = fifo ? mouvementDao.getPlusAncienArticle(articleId, idsExclus)  : mouvementDao.getPlusRecentArticle(articleId, idsExclus);

            if (articleRef == null) {
                throw new IllegalArgumentException("Stock insuffisant.");
            }

            BigDecimal qteDisponible = restantParEntree.getOrDefault(articleRef.getId(), articleRef.getQte());
            if (qteDisponible.compareTo(BigDecimal.ZERO) <= 0) {
                idsExclus.add(articleRef.getId());
                continue;
            }
            
            BigDecimal qteConsommee = soustraireQte(articleRef.getId(), qteRestante, qteDisponible, restantParEntree);
            valeur = valeur.add(qteConsommee.multiply(articleRef.getPu()));
            qteRestante = qteRestante.subtract(qteConsommee);

            if (restantParEntree.get(articleRef.getId()).compareTo(BigDecimal.ZERO) <= 0) {
                idsExclus.add(articleRef.getId());
            }
        }

        return valeur;
    }

    private BigDecimal soustraireQte(
            Long entreeId,
            BigDecimal qteSortieRestante,
            BigDecimal qteDisponible,
            Map<Long, BigDecimal> restantParEntree
    ) {
        BigDecimal qteConsommee = qteDisponible.min(qteSortieRestante);
        restantParEntree.put(entreeId, qteDisponible.subtract(qteConsommee));
        return qteConsommee;
    }

    private boolean verificationQte(BigDecimal qteSortieRestante) {
        return qteSortieRestante.compareTo(BigDecimal.ZERO) <= 0;
    }

    private BigDecimal getStockApres(Mouvement mouvement) {
        if (mouvement == null) {
            return BigDecimal.ZERO;
        }

        return mouvement.getStockApres();
    }

    private BigDecimal getValeurStockApres(Mouvement mouvement) {
        if (mouvement == null) {
            return BigDecimal.ZERO;
        }

        return mouvement.getValeurStockApres();
    }
}
