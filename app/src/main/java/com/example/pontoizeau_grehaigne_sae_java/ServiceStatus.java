package com.example.pontoizeau_grehaigne_sae_java;

public class ServiceStatus {
    private String nom;
    private String statut;
    private int imageResId;

    public ServiceStatus(String nom, String statut, int imageResId) {
        this.nom = nom;
        this.statut = statut;
        this.imageResId = imageResId;
    }

    // Getters
    public String getNom() { return nom; }
    public String getStatut() { return statut; }
    public int getImageResId() { return imageResId; }
}