package com.example.pontoizeau_grehaigne_sae_java;
// Déclare le package de l'application

import android.content.Intent;
// Permet d'utiliser les intents

import android.os.Bundle;
// Permet de gérer l'état de l'activité

import android.view.View;
// Permet d'utiliser la classe View

import android.widget.TextView;
// Permet d'utiliser des TextView

import android.widget.ImageButton;
// Permet d'utiliser des ImageButton

import android.widget.ImageView;
// Permet d'utiliser des ImageView

import androidx.appcompat.app.AppCompatActivity;
// Classe de base pour une activité

import com.android.volley.Request;
// Permet de définir le type de requête HTTP

import com.android.volley.toolbox.JsonObjectRequest;
// Permet de faire une requête JSON

import com.android.volley.toolbox.Volley;
// Permet de gérer les requêtes réseau

import org.json.JSONArray;
// Permet de manipuler des tableaux JSON

import org.json.JSONException;
// Permet de gérer les erreurs JSON

import org.json.JSONObject;
// Permet de manipuler des objets JSON

public class DetailActivity extends AppCompatActivity {
// Déclare la classe DetailActivity

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Méthode appelée à la création de l'activité
        super.onCreate(savedInstanceState);
        // Appelle le onCreate de la classe parente
        setContentView(R.layout.activity_detail);
        // Associe le layout à l'activité

        TextView tvName = findViewById(R.id.tv_detail_name);
        // Déclare et initialise le TextView du nom

        TextView tvStatus = findViewById(R.id.tv_detail_status);
        // Déclare et initialise le TextView du statut

        ImageButton imageButton2 = findViewById(R.id.imageButton2);
        // Déclare et initialise le bouton retour

        ImageView imgStatus = findViewById(R.id.iv_detail_status);
        // Déclare et initialise l'image du statut

        String statut = getIntent().getStringExtra("EXTRA_STATUT");
        // Récupère le statut envoyé par l'activité précédente

        imageButton2.setOnClickListener(new View.OnClickListener() {
            // Ajoute un écouteur de clic
            @Override
            public void onClick(View v) {
                // Méthode appelée lors du clic
                finish();
                // Ferme l'activité actuelle
            }
        });

        if (statut != null) {
            // Vérifie que le statut n'est pas nul
            tvStatus.setText(statut);
            // Affiche le statut

            if (imgStatus != null) {
                // Vérifie que l'image existe
                if (statut.equals("Opérationnel")) {
                    // Si le statut est opérationnel
                    imgStatus.setImageResource(R.drawable.status_ok);
                    // Affiche l'image OK
                } else if (statut.equals("Perturbé")) {
                    // Si le statut est perturbé
                    imgStatus.setImageResource(R.drawable.status_pok);
                    // Affiche l'image partiellement OK
                } else {
                    // Dans les autres cas
                    imgStatus.setImageResource(R.drawable.status_nok);
                    // Affiche l'image NOK
                }
            }
        }

        String nomRecu = getIntent().getStringExtra("EXTRA_NOM");
        // Récupère le nom envoyé

        String statutRecu = getIntent().getStringExtra("EXTRA_STATUT");
        // Récupère le statut envoyé

        if (nomRecu != null) {
            // Vérifie que le nom n'est pas nul
            tvName.setText(nomRecu);
            // Affiche le nom
        }
        if (statutRecu != null) {
            // Vérifie que le statut n'est pas nul
            tvStatus.setText(statutRecu);
            // Affiche le statut
        }

        TextView tvDescription = findViewById(R.id.tv_detail_issue);
        // Déclare et initialise le TextView description

        TextView tvDate = findViewById(R.id.tv_detail_date);
        // Déclare et initialise le TextView date

        TextView tvRes = findViewById(R.id.tv_detail_res);
        // Déclare et initialise le TextView résolution

        ImageView imgLogo = findViewById(R.id.detail_logo);
        // Déclare et initialise l'image du logo

        String nom = getIntent().getStringExtra("EXTRA_NOM");
        // Récupère le nom du service

        String description = getIntent().getStringExtra("EXTRA_DESC");
        // Récupère la description

        String date = getIntent().getStringExtra("EXTRA_DATE");
        // Récupère la date

        String resolution = getIntent().getStringExtra("EXTRA_RES");
        // Récupère la résolution

        int imageRes = getIntent().getIntExtra("EXTRA_IMAGE", 0);
        // Récupère l'identifiant de l'image

        if (nom != null) tvName.setText(nom);
        // Affiche le nom

        if (statut != null) tvStatus.setText(statut);
        // Affiche le statut

        if (description != null) tvDescription.setText("Problème : " + description);
        // Affiche la description

        if (date != null) tvDate.setText("Date : " + date);
        // Affiche la date

        if (resolution != null) tvRes.setText("Résolution : " + resolution);
        // Affiche la résolution

        if (imageRes != 0) imgLogo.setImageResource(imageRes);
        // Affiche le logo

        if (nom != null && nom.contains("GitHub")) {
            // Vérifie si le service est GitHub
            loadGithubIncidents();
            // Appelle l'API GitHub
        } else if (nom != null && nom.contains("Discord")) {
            // Vérifie si le service est Discord
            loadDiscordIncidents();
        } else if (nom != null && nom.contains("Cloudflare")) {
            // Vérifie si le service est Cloudflare
            loadCloudflareIncidents();
        } else if (nom != null && nom.contains("Reddit")) {
            // Vérifie si le service est Reddit
            loadRedditIncidents();
        }
    }

    private void loadGithubIncidents() {
        // Méthode qui charge les incidents GitHub
        String url = "https://www.githubstatus.com/api/v2/incidents.json";
        // Déclare l'URL de l'API GitHub

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                // Définit une requête GET
                url,
                // Passe l'URL
                null,
                // Pas de corps de requête
                response -> {
                    // Réponse reçue
                    try {
                        JSONArray incidents = response.getJSONArray("incidents");
                        // Récupère le tableau des incidents

                        if (incidents.length() > 0) {
                            // Vérifie s'il y a des incidents
                            JSONObject latest = incidents.getJSONObject(0);
                            // Récupère le dernier incident

                            String issueName = latest.getString("name");
                            // Récupère le nom de l'incident
                            String issueDate = latest.getString("created_at");
                            // Récupère la date
                            String issueStatus = latest.getString("status");
                            // Récupère le statut

                            String etatEnFrancais = issueStatus;
                            // Initialise le statut en français

                            if (issueStatus.equals("resolved")) {
                                etatEnFrancais = "Résolu";
                            } else if (issueStatus.equals("investigating")) {
                                etatEnFrancais = "Enquête en cours";
                            } else if (issueStatus.equals("identified")) {
                                etatEnFrancais = "Identifié";
                            } else if (issueStatus.equals("monitoring")) {
                                etatEnFrancais = "Sous surveillance";
                            }

                            String datePropre = issueDate;
                            // Initialise la date affichée
                            if (issueDate.length() >= 10) {
                                String annee = issueDate.substring(0, 4);
                                String mois = issueDate.substring(5, 7);
                                String jour = issueDate.substring(8, 10);
                                datePropre = jour + "/" + mois + "/" + annee;
                            }

                            TextView tvIssue = findViewById(R.id.tv_detail_issue);
                            // Récupère le TextView incident
                            TextView tvDate = findViewById(R.id.tv_detail_date);
                            // Récupère le TextView date
                            TextView tvRes = findViewById(R.id.tv_detail_res);
                            // Récupère le TextView état

                            tvIssue.setText("Dernier incident : " + issueName);
                            // Affiche l'incident
                            tvDate.setText("Date : " + datePropre);
                            // Affiche la date
                            tvRes.setText("État : " + etatEnFrancais);
                            // Affiche l'état
                        } else {
                            TextView tvIssue = findViewById(R.id.tv_detail_issue);
                            // Récupère le TextView incident
                            tvIssue.setText("Aucun incident récent signalé.");
                            // Affiche le message
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        // Affiche l'erreur
                        android.widget.Toast.makeText(this, "Erreur lecture JSON: " + e.getMessage(), android.widget.Toast.LENGTH_LONG).show();
                        // Affiche un message d'erreur
                    }
                },
                error -> {
                    error.printStackTrace();
                    // Affiche l'erreur réseau
                    android.widget.Toast.makeText(this, "Erreur Internet", android.widget.Toast.LENGTH_SHORT).show();
                    // Affiche un message d'erreur
                }
        );

        Volley.newRequestQueue(this).add(request);
        // Ajoute la requête à la file d'attente
    }

    private void loadDiscordIncidents() {
        // Méthode qui charge les incidents Discord
        String url = "https://discordstatus.com/api/v2/incidents.json";
        // Déclare l'URL de l'API Discord

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                response -> {
                    try {
                        JSONArray incidents = response.getJSONArray("incidents");
                        // Récupère les incidents
                        if (incidents.length() > 0) {
                            JSONObject latest = incidents.getJSONObject(0);
                            // Récupère le dernier incident

                            String issueName = latest.getString("name");
                            String issueDate = latest.getString("created_at");
                            String issueStatus = latest.getString("status");

                            String etatEnFrancais = issueStatus;
                            if (issueStatus.equals("resolved")) etatEnFrancais = "Résolu";
                            else if (issueStatus.equals("investigating")) etatEnFrancais = "Enquête en cours";
                            else if (issueStatus.equals("monitoring")) etatEnFrancais = "Sous surveillance";

                            String datePropre = issueDate;
                            if (issueDate.length() >= 10) {
                                datePropre = issueDate.substring(8, 10) + "/" + issueDate.substring(5, 7) + "/" + issueDate.substring(0, 4);
                            }

                            TextView tvIssue = findViewById(R.id.tv_detail_issue);
                            TextView tvDate = findViewById(R.id.tv_detail_date);
                            TextView tvRes = findViewById(R.id.tv_detail_res);

                            tvIssue.setText("Dernier incident : " + issueName);
                            tvDate.setText("Date : " + datePropre);
                            tvRes.setText("État : " + etatEnFrancais);
                        } else {
                            TextView tvIssue = findViewById(R.id.tv_detail_issue);
                            tvIssue.setText("Aucun incident récent.");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> error.printStackTrace()
        );
        Volley.newRequestQueue(this).add(request);
        // Ajoute la requête
    }

    private void loadCloudflareIncidents() {
        // Méthode qui charge les incidents Cloudflare
        String url = "https://www.cloudflarestatus.com/api/v2/incidents.json";
        // Déclare l'URL Cloudflare

        com.android.volley.toolbox.JsonObjectRequest request = new com.android.volley.toolbox.JsonObjectRequest(
                com.android.volley.Request.Method.GET,
                url,
                null,
                response -> {
                    try {
                        org.json.JSONArray incidents = response.getJSONArray("incidents");
                        if (incidents.length() > 0) {
                            org.json.JSONObject latest = incidents.getJSONObject(0);

                            String issueName = latest.getString("name");
                            String issueDate = latest.getString("created_at");
                            String issueStatus = latest.getString("status");

                            String etatEnFrancais = issueStatus;
                            if (issueStatus.equals("resolved")) etatEnFrancais = "Résolu";
                            else if (issueStatus.equals("investigating")) etatEnFrancais = "Enquête en cours";
                            else if (issueStatus.equals("monitoring")) etatEnFrancais = "Sous surveillance";
                            else if (issueStatus.equals("identified")) etatEnFrancais = "Identifié";

                            String datePropre = issueDate;
                            if (issueDate.length() >= 10) {
                                datePropre = issueDate.substring(8, 10) + "/" + issueDate.substring(5, 7) + "/" + issueDate.substring(0, 4);
                            }

                            TextView tvIssue = findViewById(R.id.tv_detail_issue);
                            TextView tvDate = findViewById(R.id.tv_detail_date);
                            TextView tvRes = findViewById(R.id.tv_detail_res);

                            tvIssue.setText("Dernier incident : " + issueName);
                            tvDate.setText("Date : " + datePropre);
                            tvRes.setText("État : " + etatEnFrancais);
                        } else {
                            TextView tvIssue = findViewById(R.id.tv_detail_issue);
                            tvIssue.setText("Aucun incident récent.");
                        }
                    } catch (org.json.JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> error.printStackTrace()
        );

        com.android.volley.toolbox.Volley.newRequestQueue(this).add(request);
        // Ajoute la requête
    }

    private void loadRedditIncidents() {
        // Méthode qui charge les incidents Reddit
        String url = "https://www.redditstatus.com/api/v2/incidents.json";
        // Déclare l'URL Reddit

        com.android.volley.toolbox.JsonObjectRequest request = new com.android.volley.toolbox.JsonObjectRequest(
                com.android.volley.Request.Method.GET,
                url,
                null,
                response -> {
                    try {
                        org.json.JSONArray incidents = response.getJSONArray("incidents");
                        if (incidents.length() > 0) {
                            org.json.JSONObject latest = incidents.getJSONObject(0);

                            String issueName = latest.getString("name");
                            String issueDate = latest.getString("created_at");
                            String issueStatus = latest.getString("status");

                            String etatEnFrancais = issueStatus;
                            if (issueStatus.equals("resolved")) etatEnFrancais = "Résolu";
                            else if (issueStatus.equals("investigating")) etatEnFrancais = "Enquête";
                            else if (issueStatus.equals("identified")) etatEnFrancais = "Identifié";

                            String datePropre = issueDate;
                            if (issueDate.length() >= 10) {
                                datePropre = issueDate.substring(8, 10) + "/" + issueDate.substring(5, 7) + "/" + issueDate.substring(0, 4);
                            }

                            TextView tvIssue = findViewById(R.id.tv_detail_issue);
                            TextView tvDate = findViewById(R.id.tv_detail_date);
                            TextView tvRes = findViewById(R.id.tv_detail_res);

                            tvIssue.setText("Dernier incident : " + issueName);
                            tvDate.setText("Date : " + datePropre);
                            tvRes.setText("État : " + etatEnFrancais);
                        } else {
                            TextView tvIssue = findViewById(R.id.tv_detail_issue);
                            tvIssue.setText("Aucun incident récent.");
                        }
                    } catch (org.json.JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> error.printStackTrace()
        );

        com.android.volley.toolbox.Volley.newRequestQueue(this).add(request);
        // Ajoute la requête
    }
}
