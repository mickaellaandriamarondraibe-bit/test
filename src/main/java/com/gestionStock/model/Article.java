package com.gestionStock.model;

public class Article {
    private Long id;
    private String libelle;

    public Article() {}

    public Article(Long id, String libelle) {
        this.id = id;
        this.libelle = libelle;
    }

    public Article(String libelle) {
        this.libelle = libelle;
    }

    public Long getId() {
        return id;
    }

    public String getLibelle() {
        return libelle;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setLibelle(String libelle) {
        this.libelle = libelle;
    }

    @Override
    public String toString() {
        return libelle == null ? "Article #" + id : libelle;
    }
}
