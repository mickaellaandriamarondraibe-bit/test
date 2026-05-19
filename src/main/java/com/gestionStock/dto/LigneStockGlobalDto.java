package com.gestionStock.dto;

import java.time.LocalDate;

public class LigneStockGlobalDto {
    private final String produit;
    private final Object quantiteEnStock;
    private final Object valeurDuStock;
    private final Object cump;
    private final String methode;
    private final LocalDate dateDernierMouvement;
    private final Long articleId;

    public LigneStockGlobalDto(
            String produit,
            Object quantiteEnStock,
            Object valeurDuStock,
            Object cump,
            String methode,
            LocalDate dateDernierMouvement,
            Long articleId
    ) {
        this.produit = produit;
        this.quantiteEnStock = quantiteEnStock;
        this.valeurDuStock = valeurDuStock;
        this.cump = cump;
        this.methode = methode;
        this.dateDernierMouvement = dateDernierMouvement;
        this.articleId = articleId;
    }

    public String getProduit() { return produit; }
    public Object getQuantiteEnStock() { return quantiteEnStock; }
    public Object getValeurDuStock() { return valeurDuStock; }
    public Object getCump() { return cump; }
    public String getMethode() { return methode; }
    public LocalDate getDateDernierMouvement() { return dateDernierMouvement; }
    public Long getArticleId() { return articleId; }
}
