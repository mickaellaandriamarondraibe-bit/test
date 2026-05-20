package com.gestionStock.model;

public class Article {
    private Long id;
    private String libelle;
    private Long methodeId;

    public Article() {}

    public Article(Long id, String libelle, Long methodeId) {
        this.id = id;
        this.libelle = libelle;
        this.methodeId = methodeId;
    }

    public Article(String libelle, Long methodeId) {
        this.libelle = libelle;
        this.methodeId = methodeId;
    }

    public Long getId() {
        return id;
    }

    public String getLibelle() {
        return libelle;
    }

    public Long getMethodeId() {
        return methodeId;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setLibelle(String libelle) {
        this.libelle = libelle;
    }

    public void setMethodeId(Long methodeId) {
        this.methodeId = methodeId;
    }

    @Override
    public String toString() {
        return libelle == null ? "Article #" + id : libelle;
    }
}
