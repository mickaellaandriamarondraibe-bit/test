package com.gestionStock.model;

import java.math.BigDecimal;

public class MouvementSource {

    private Long id;

    private Long mouvementSortieId;
    private Long mouvementEntreeId;

    private BigDecimal qteUtilisee;
    private BigDecimal puUtilise;
    private BigDecimal valeurUtilisee;

    public MouvementSource() {
    }

    public Long getId() {
        return id;
    }

    public Long getMouvementSortieId() {
        return mouvementSortieId;
    }

    public Long getMouvementEntreeId() {
        return mouvementEntreeId;
    }

    public BigDecimal getQteUtilisee() {
        return qteUtilisee;
    }

    public BigDecimal getPuUtilise() {
        return puUtilise;
    }

    public BigDecimal getValeurUtilisee() {
        return valeurUtilisee;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setMouvementSortieId(Long mouvementSortieId) {
        this.mouvementSortieId = mouvementSortieId;
    }

    public void setMouvementEntreeId(Long mouvementEntreeId) {
        this.mouvementEntreeId = mouvementEntreeId;
    }

    public void setQteUtilisee(BigDecimal qteUtilisee) {
        this.qteUtilisee = qteUtilisee;
    }

    public void setPuUtilise(BigDecimal puUtilise) {
        this.puUtilise = puUtilise;
    }

    public void setValeurUtilisee(BigDecimal valeurUtilisee) {
        this.valeurUtilisee = valeurUtilisee;
    }
}
