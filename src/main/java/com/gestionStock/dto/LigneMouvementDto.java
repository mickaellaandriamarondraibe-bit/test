package com.gestionStock.dto;

import com.gestionStock.model.Mouvement;

import java.time.LocalDate;

public class LigneMouvementDto {
    private final Long id;
    private final String produit;
    private final String methode;
    private final LocalDate dateMouvement;
    private final Object typeMouvement;
    private final Object qte;
    private final Object pu;
    private final Object valeur;
    private final Object stockApres;
    private final Object valeurStockApres;
    private final Object cumpApres;

    public LigneMouvementDto(Mouvement mouvement, String produit, String methode) {
        this.id = mouvement.getId();
        this.produit = produit;
        this.methode = methode;
        this.dateMouvement = mouvement.getDateMouvement();
        this.typeMouvement = mouvement.getTypeMouvement();
        this.qte = mouvement.getQte();
        this.pu = mouvement.getPu();
        this.valeur = mouvement.getValeur();
        this.stockApres = mouvement.getStockApres();
        this.valeurStockApres = mouvement.getValeurStockApres();
        this.cumpApres = mouvement.getCumpApres();
    }

    public Long getId() { return id; }
    public String getProduit() { return produit; }
    public String getMethode() { return methode; }
    public LocalDate getDateMouvement() { return dateMouvement; }
    public Object getTypeMouvement() { return typeMouvement; }
    public Object getQte() { return qte; }
    public Object getPu() { return pu; }
    public Object getValeur() { return valeur; }
    public Object getStockApres() { return stockApres; }
    public Object getValeurStockApres() { return valeurStockApres; }
    public Object getCumpApres() { return cumpApres; }
}
