package com.gestionStock.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Mouvement {
    private Long id;
    private Long articleId;
    private Long methodeId;

    private LocalDate dateMouvement;
    private TypeMouvement typeMouvement;

    private BigDecimal qte;
    private BigDecimal pu;
    private BigDecimal valeur;

    private BigDecimal stockApres;
    private BigDecimal valeurStockApres;
    private BigDecimal cumpApres;

    public Mouvement() {}

    public Mouvement(
            Long id,
            Long articleId,
            Long methodeId,
            LocalDate dateMouvement,
            TypeMouvement type,
            BigDecimal qte,
            BigDecimal pu,
            BigDecimal valeur,
            BigDecimal stockApres,
            BigDecimal valeurStockApres,
            BigDecimal cumpApres
    ) {
        this.id = id;
        this.articleId = articleId;
        this.methodeId = methodeId;
        this.dateMouvement = dateMouvement;
        this.typeMouvement = type;
        this.qte = qte;
        this.pu = pu;
        this.valeur = valeur;
        this.stockApres = stockApres;
        this.valeurStockApres = valeurStockApres;
        this.cumpApres = cumpApres;
    }

    public Long getId() {
        return id;
    }

    public Long getArticleId() {
        return articleId;
    }

    public Long getMethodeId() {
        return methodeId;
    }

    public LocalDate getDateMouvement() {
        return dateMouvement;
    }

    public TypeMouvement getTypeMouvement() {
        return typeMouvement;
    }

    public BigDecimal getQte() {
        return qte;
    }

    public BigDecimal getPu() {
        return pu;
    }

    public BigDecimal getValeur() {
        return valeur;
    }

    public BigDecimal getStockApres() {
        return stockApres;
    }

    public BigDecimal getValeurStockApres() {
        return valeurStockApres;
    }

    public BigDecimal getCumpApres() {
        return cumpApres;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setArticleId(Long articleId) {
        this.articleId = articleId;
    }

    public void setMethodeId(Long methodeId) {
        this.methodeId = methodeId;
    }

    public void setDateMouvement(LocalDate dateMouvement) {
        this.dateMouvement = dateMouvement;
    }

    public void setTypeMouvement(TypeMouvement typeMouvement) {
        this.typeMouvement = typeMouvement;
    }

    public void setQte(BigDecimal qte) {
        this.qte = qte;
    }

    public void setPu(BigDecimal pu) {
        this.pu = pu;
    }

    public void setValeur(BigDecimal valeur) {
        this.valeur = valeur;
    }

    public void setStockApres(BigDecimal stockApres) {
        this.stockApres = stockApres;
    }

    public void setValeurStockApres(BigDecimal valeurStockApres) {
        this.valeurStockApres = valeurStockApres;
    }

    public void setCumpApres(BigDecimal cumpApres) {
        this.cumpApres = cumpApres;
    }
}
