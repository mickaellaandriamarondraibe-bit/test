package com.gestionStock.service;

import com.gestionStock.dao.GenericDao;
import com.gestionStock.dto.LigneStockGlobalDto;
import com.gestionStock.dto.LigneStockResteDto;
import com.gestionStock.model.Article;
import com.gestionStock.model.Methode;
import com.gestionStock.model.Mouvement;
import com.gestionStock.model.MouvementSource;
import com.gestionStock.model.TypeMouvement;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EtatStockService {

    private final GenericDao<Mouvement> mouvementDao;
    private final GenericDao<MouvementSource> mouvementSourceDao;
    private final GenericDao<Article> articleDao;
    private final GenericDao<Methode> methodeDao;

    public EtatStockService() {
        this.mouvementDao = new GenericDao<>(Mouvement.class);
        this.mouvementSourceDao = new GenericDao<>(MouvementSource.class);
        this.articleDao = new GenericDao<>(Article.class);
        this.methodeDao = new GenericDao<>(Methode.class);
    }

    public List<Mouvement> getStockGlobalByDate(LocalDate dateLimite) throws Exception {
        List<Mouvement> mouvements = mouvementDao.findAll();
        List<Mouvement> result = new ArrayList<>();

        for (Mouvement mouvement : mouvements) {
            if (mouvement.getDateMouvement().isAfter(dateLimite)) {
                continue;
            }

            Mouvement dernier = findByArticleId(result, mouvement.getArticleId());

            if (dernier == null) {
                result.add(mouvement);
            } else if (mouvement.getId() > dernier.getId()) {
                result.remove(dernier);
                result.add(mouvement);
            }
        }

        return result;
    }

    private Mouvement findByArticleId(List<Mouvement> mouvements, Long articleId) {
        for (Mouvement mouvement : mouvements) {
            if (mouvement.getArticleId().equals(articleId)) {
                return mouvement;
            }
        }

        return null;
    }

    private List<Mouvement> getStockDetailByDate(Long articleId, LocalDate dateLimite) throws Exception {
        List<Mouvement> mouvements = mouvementDao.findAll();
        List<Mouvement> result = new ArrayList<>();

        for (Mouvement mouvement : mouvements) {
            if (mouvement.getArticleId().equals(articleId) && !mouvement.getDateMouvement().isAfter(dateLimite)) {
                result.add(mouvement);
            }
        }

        return result;
    }

    public List<LigneStockResteDto> getStockDetailLignesByDate(Long articleId, LocalDate dateLimite) throws Exception {
        List<Mouvement> mouvements = getStockDetailByDate(articleId, dateLimite);
        mouvements.sort(Comparator.comparing(Mouvement::getDateMouvement).thenComparing(Mouvement::getId));

        Article article = articleDao.findById(articleId);
        String libelleMethode = "";
        if (article != null && article.getMethodeId() != null) {
            Methode methode = methodeDao.findById(article.getMethodeId());
            if (methode != null && methode.getLibelle() != null) {
                libelleMethode = methode.getLibelle().trim().toUpperCase();
            }
        }

        List<Mouvement> entrees = new ArrayList<>();
        List<Mouvement> sorties = new ArrayList<>();
        for (Mouvement mouvement : mouvements) {
            if (mouvement.getTypeMouvement() == TypeMouvement.ENTREE) {
                entrees.add(mouvement);
            } else {
                sorties.add(mouvement);
            }
        }

        Map<Long, BigDecimal> restantParEntree = new HashMap<>();
        for (Mouvement entree : entrees) {
            restantParEntree.put(entree.getId(), entree.getQte());
        }

        Map<Long, List<MouvementSource>> sourcesParSortieId = new HashMap<>();
        for (MouvementSource source : mouvementSourceDao.findAll()) {
            Long sortieId = source.getMouvementSortieId();
            sourcesParSortieId.computeIfAbsent(sortieId, k -> new ArrayList<>()).add(source);
        }

        for (Mouvement sortie : sorties) {
            List<MouvementSource> sources = sourcesParSortieId.get(sortie.getId());
            if (sources != null && !sources.isEmpty()) {
                for (MouvementSource source : sources) {
                    BigDecimal restant = restantParEntree.getOrDefault(source.getMouvementEntreeId(), BigDecimal.ZERO);
                    restantParEntree.put(source.getMouvementEntreeId(), restant.subtract(source.getQteUtilisee()));
                }
                continue;
            }
            consommerSansSources(sortie.getQte(), entrees, restantParEntree, libelleMethode);
        }

        List<LigneStockResteDto> lignes = new ArrayList<>();
        for (Mouvement entree : entrees) {
            BigDecimal restant = restantParEntree.getOrDefault(entree.getId(), BigDecimal.ZERO);
            if (restant.compareTo(BigDecimal.ZERO) > 0) {
                lignes.add(new LigneStockResteDto(entree.getDateMouvement(), restant, entree.getPu()));
            }
        }
        lignes.sort(Comparator.comparing(LigneStockResteDto::getDateEntree));
        return lignes;
    }

    public List<LigneStockGlobalDto> getStockGlobalLignesByDate(LocalDate dateLimite) throws Exception {
        List<Mouvement> mouvements = getStockGlobalByDate(dateLimite);

        Map<Long, String> libelleArticleParId = new HashMap<>();
        for (Article article : articleDao.findAll()) {
            libelleArticleParId.put(article.getId(), article.getLibelle());
        }

        Map<Long, String> libelleMethodeParId = new HashMap<>();
        for (Methode methode : methodeDao.findAll()) {
            libelleMethodeParId.put(methode.getId(), methode.getLibelle());
        }

        List<LigneStockGlobalDto> lignes = new ArrayList<>();
        for (Mouvement mouvement : mouvements) {
            lignes.add(new LigneStockGlobalDto(
                    libelleArticleParId.getOrDefault(mouvement.getArticleId(), "#" + mouvement.getArticleId()),
                    mouvement.getStockApres(),
                    mouvement.getValeurStockApres(),
                    mouvement.getCumpApres(),
                    libelleMethodeParId.getOrDefault(mouvement.getMethodeId(), "#" + mouvement.getMethodeId()),
                    mouvement.getDateMouvement(),
                    mouvement.getArticleId()
            ));
        }
        return lignes;
    }

    private void consommerSansSources(
            BigDecimal qteSortie,
            List<Mouvement> entrees,
            Map<Long, BigDecimal> restantParEntree,
            String libelleMethode
    ) {
        List<Mouvement> ordre = new ArrayList<>(entrees);
        if ("LIFO".equals(libelleMethode)) {
            ordre.sort(Comparator.comparing(Mouvement::getDateMouvement).thenComparing(Mouvement::getId).reversed());
        } else {
            ordre.sort(Comparator.comparing(Mouvement::getDateMouvement).thenComparing(Mouvement::getId));
        }

        BigDecimal resteASoustraire = qteSortie;
        for (Mouvement entree : ordre) {
            if (resteASoustraire.compareTo(BigDecimal.ZERO) <= 0) {
                break;
            }
            BigDecimal restant = restantParEntree.getOrDefault(entree.getId(), BigDecimal.ZERO);
            if (restant.compareTo(BigDecimal.ZERO) <= 0) {
                continue;
            }

            BigDecimal consommee = restant.min(resteASoustraire);
            restantParEntree.put(entree.getId(), restant.subtract(consommee));
            resteASoustraire = resteASoustraire.subtract(consommee);
        }
    }
}
