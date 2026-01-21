package com.example.pontoizeau_grehaigne_sae_java;

public class ServiceStatus {
    private String nom;
    private String statut;
    private int imageResId;

    // --- NOUVELLES VARIABLES ---
    private String description;
    private String date;
    private String resolution;

    // Constructeur
    public ServiceStatus(String nom, String statut, int imageResId, String description, String date, String resolution) {
        this.nom = nom;
        this.statut = statut;
        this.imageResId = imageResId;
        this.description = description;
        this.date = date;
        this.resolution = resolution;
    }

    // Getters existants
    public String getNom() { return nom; }
    public String getStatut() { return statut; }
    public int getImageResId() { return imageResId; }

    // --- NOUVEAUX GETTERS ---
    public String getDescription() { return description; }
    public String getDate() { return date; }
    public String getResolution() { return resolution; }

    // --- NOUVEAUX SETTERS ---
    public void setImageResId(int imageResId) { this.imageResId = imageResId; }
}
