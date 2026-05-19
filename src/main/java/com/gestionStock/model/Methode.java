package com.gestionStock.model;

public class Methode {
    private Long id;
    private String libelle; // FIFO, LIFO, CUMP

    public Methode() {}

    public Methode(Long id, String libelle) {
        this.id = id;
        this.libelle = libelle;
    }

    public Methode(String libelle) {
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
        return libelle == null ? "Méthode #" + id : libelle;
    }
}
