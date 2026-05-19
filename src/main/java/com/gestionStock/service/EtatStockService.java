package com.gestionStock.service;

import com.gestionStock.dao.GenericDao;
import com.gestionStock.dto.LigneStockDetailDto;
import com.gestionStock.dto.LigneStockGlobalDto;
import com.gestionStock.model.Article;
import com.gestionStock.model.Methode;
import com.gestionStock.model.Mouvement;
import com.gestionStock.model.MouvementSource;

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

    public List<LigneStockDetailDto> getStockDetailLignesByDate(Long articleId, LocalDate dateLimite) throws Exception {
        List<Mouvement> mouvements = getStockDetailByDate(articleId, dateLimite);
        mouvements.sort(Comparator.comparing(Mouvement::getDateMouvement).thenComparing(Mouvement::getId));

        Map<Long, List<MouvementSource>> sourcesParSortieId = new HashMap<>();
        for (MouvementSource source : mouvementSourceDao.findAll()) {
            Long sortieId = source.getMouvementSortieId();
            sourcesParSortieId.computeIfAbsent(sortieId, k -> new ArrayList<>()).add(source);
        }

        List<LigneStockDetailDto> lignes = new ArrayList<>();
        for (Mouvement mouvement : mouvements) {
            lignes.add(new LigneStockDetailDto(mouvement, formaterSources(mouvement, sourcesParSortieId)));
        }
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

    private String formaterSources(Mouvement mouvement, Map<Long, List<MouvementSource>> sourcesParSortieId) {
        List<MouvementSource> sources = sourcesParSortieId.get(mouvement.getId());
        if (sources == null || sources.isEmpty()) {
            return "-";
        }
        StringBuilder texte = new StringBuilder();
        for (int i = 0; i < sources.size(); i++) {
            MouvementSource source = sources.get(i);
            if (i > 0) {
                texte.append(" | ");
            }
            texte.append("entree ")
                    .append(source.getMouvementEntreeId())
                    .append(": qte ")
                    .append(source.getQteUtilisee());
        }
        return texte.toString();
    }
}
