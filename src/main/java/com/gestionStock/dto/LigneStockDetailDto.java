package com.gestionStock.dto;

import com.gestionStock.model.Mouvement;

import java.time.LocalDate;

public class LigneStockDetailDto {
    private final LocalDate dateMouvement;
    private final Object typeMouvement;
    private final Object qte;
    private final Object pu;
    private final Object valeur;
    private final Object stockApres;
    private final Object valeurStockApres;
    private final Object cumpApres;
    private final String sourcesUtilisees;

    public LigneStockDetailDto(Mouvement mouvement, String sourcesUtilisees) {
        this.dateMouvement = mouvement.getDateMouvement();
        this.typeMouvement = mouvement.getTypeMouvement();
        this.qte = mouvement.getQte();
        this.pu = mouvement.getPu();
        this.valeur = mouvement.getValeur();
        this.stockApres = mouvement.getStockApres();
        this.valeurStockApres = mouvement.getValeurStockApres();
        this.cumpApres = mouvement.getCumpApres();
        this.sourcesUtilisees = sourcesUtilisees;
    }

    public LocalDate getDateMouvement() { return dateMouvement; }
    public Object getTypeMouvement() { return typeMouvement; }
    public Object getQte() { return qte; }
    public Object getPu() { return pu; }
    public Object getValeur() { return valeur; }
    public Object getStockApres() { return stockApres; }
    public Object getValeurStockApres() { return valeurStockApres; }
    public Object getCumpApres() { return cumpApres; }
    public String getSourcesUtilisees() { return sourcesUtilisees; }
}
