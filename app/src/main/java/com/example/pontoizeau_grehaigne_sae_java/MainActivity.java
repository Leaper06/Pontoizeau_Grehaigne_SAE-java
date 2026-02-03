package com.example.pontoizeau_grehaigne_sae_java;
// Déclare le package de l'application

import android.os.Bundle;
// Permet de gérer l'état de l'activité

import androidx.appcompat.app.AppCompatActivity;
// Classe de base pour une activité Android

import androidx.recyclerview.widget.GridLayoutManager;
// Permet d'afficher un RecyclerView sous forme de grille

import androidx.recyclerview.widget.RecyclerView;
// Permet d'afficher une liste optimisée

import java.util.ArrayList;
// Permet d'utiliser une liste dynamique

import java.util.List;
// Interface List

import androidx.activity.EdgeToEdge;
// Permet l'affichage bord à bord

import androidx.core.graphics.Insets;
// Permet de gérer les marges système

import androidx.core.view.ViewCompat;
// Permet de gérer les insets sur une vue

import androidx.core.view.WindowInsetsCompat;
// Permet de récupérer les insets système

import android.view.View;
// Permet d'utiliser la classe View

import android.content.BroadcastReceiver;
// Permet d'écouter des événements système

import android.content.Context;
// Fournit le contexte de l'application

import android.content.Intent;
// Permet de recevoir des intents

import android.content.IntentFilter;
// Permet de filtrer les intents reçus

import android.widget.ImageView;
// Permet d'afficher des images

import android.widget.Toast;
// Permet d'afficher des messages temporaires

import com.android.volley.Request;
// Permet de définir le type de requête HTTP

import com.android.volley.RequestQueue;
// Permet de gérer une file de requêtes

import com.android.volley.Response;
// Interface de réponse Volley

import com.android.volley.VolleyError;
// Interface d'erreur Volley

import com.android.volley.toolbox.JsonObjectRequest;
// Permet de faire une requête JSON

import com.android.volley.toolbox.Volley;
// Permet de créer une file de requêtes Volley

import org.json.JSONException;
// Permet de gérer les erreurs JSON

import org.json.JSONObject;
// Permet de manipuler des objets JSON

public class MainActivity extends AppCompatActivity {
// Déclare l'activité principale

    private List<ServiceStatus> services;
    // Déclare la liste des services

    private ServiceAdapter adapter;
    // Déclare l'adaptateur du RecyclerView

    private ImageView wifiStatusIcon;
    // Déclare l'icône Wi-Fi

    private ImageView refreshButton;
    // Déclare le bouton de rafraîchissement

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Méthode appelée à la création de l'activité
        super.onCreate(savedInstanceState);
        // Appelle la méthode parente

        EdgeToEdge.enable(this);
        // Active l'affichage bord à bord

        setContentView(R.layout.activity_main);
        // Associe le layout à l'activité

        wifiStatusIcon = findViewById(R.id.wifi_status);
        // Initialise l'icône Wi-Fi

        refreshButton = findViewById(R.id.btn_refresh);
        // Initialise le bouton rafraîchir

        services = new ArrayList<>();
        // Instancie la liste des services

        View mainLayout = findViewById(R.id.main);
        // Récupère la vue principale

        if (mainLayout != null) {
            // Vérifie que la vue existe
            ViewCompat.setOnApplyWindowInsetsListener(mainLayout, (v, insets) -> {
                // Applique les marges système
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                // Récupère les marges
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                // Applique les marges à la vue
                return insets;
            });
        }

        RecyclerView recyclerView = findViewById(R.id.recycler_services);
        // Initialise le RecyclerView

        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        // Définit une grille de 2 colonnes

        new Thread(() -> {
            // Lance un thread secondaire
            boolean internetOk = NetworkUtils.isNetworkConnected(this) && NetworkUtils.isInternetAvailable();
            // Vérifie la connexion Internet
            runOnUiThread(() -> {
                // Retour sur le thread UI
                if (internetOk) {
                    Toast.makeText(this, "Internet OK", Toast.LENGTH_SHORT).show();
                    // Affiche un message
                    updateWifiIcon();
                    // Met à jour l'icône Wi-Fi
                } else {
                    Toast.makeText(this, "Pas d'accès Internet", Toast.LENGTH_SHORT).show();
                    // Affiche un message
                }
            });
        }).start();

        services = new ArrayList<>();
        // Réinitialise la liste des services

        adapter = new ServiceAdapter(services);
        // Instancie l'adaptateur

        recyclerView.setAdapter(adapter);
        // Associe l'adaptateur au RecyclerView

        loadGithubStatus();
        loadDiscordStatus();
        loadCloudflareStatus();
        loadRedditStatus();
        // Lance les appels API

        refreshButton.setOnClickListener(v -> refreshAll());
        // Ajoute un écouteur de clic sur le bouton rafraîchir
    }

    private void loadGithubStatus() {
        // Méthode pour récupérer le statut GitHub
        String url = "https://www.githubstatus.com/api/v2/status.json";
        // Déclare l'URL GitHub

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                // Requête GET
                url,
                null,
                response -> {
                    try {
                        JSONObject statusObj = response.getJSONObject("status");
                        // Récupère l'objet status
                        String indicator = statusObj.getString("indicator");
                        // Récupère l'indicateur
                        String description = statusObj.getString("description");
                        // Récupère la description
                        String updatedAt = response.getJSONObject("page").getString("updated_at");
                        // Récupère la date de mise à jour

                        String etatText;
                        // Déclare le texte d'état
                        if (indicator.equals("none")) etatText = "Opérationnel";
                        else if (indicator.equals("minor")) etatText = "Perturbé";
                        else etatText = "Panne";

                        services.add(0, new ServiceStatus(
                                "GitHub API",
                                etatText,
                                R.drawable.github,
                                description,
                                updatedAt,
                                "Voir site officiel"
                        ));
                        // Ajoute GitHub à la liste

                        adapter.notifyDataSetChanged();
                        // Rafraîchit l'affichage
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> error.printStackTrace()
        );

        Volley.newRequestQueue(this).add(request);
        // Ajoute la requête à Volley
    }

    private boolean isInternetConnected() {
        // Vérifie la connexion Internet
        return NetworkUtils.isNetworkConnected(this) && NetworkUtils.isInternetAvailable();
    }

    private void updateWifiIcon() {
        // Met à jour l'icône Wi-Fi
        new Thread(() -> {
            boolean internetOk = isInternetConnected();
            runOnUiThread(() -> {
                if (wifiStatusIcon != null) {
                    wifiStatusIcon.setImageResource(
                            internetOk ? R.drawable.wifi_ok : R.drawable.wifi_nok
                    );
                }
            });
        }).start();
    }

    private final BroadcastReceiver wifiReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            updateWifiIcon();
            // Met à jour l'icône Wi-Fi
        }
    };

    private void refreshAll() {
        // Rafraîchit toutes les données
        services.clear();
        // Vide la liste
        adapter.notifyDataSetChanged();
        // Met à jour l'affichage
        updateWifiIcon();
        // Met à jour l'icône Wi-Fi
        loadGithubStatus();
        loadDiscordStatus();
        loadCloudflareStatus();
        loadRedditStatus();
        // Relance les appels API
        Toast.makeText(this, "Données actualisées", Toast.LENGTH_SHORT).show();
        // Affiche un message
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(wifiReceiver);
        // Désenregistre le BroadcastReceiver
    }

    private void loadDiscordStatus() {
        // Charge le statut Discord
        String url = "https://discordstatus.com/api/v2/status.json";

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                response -> {
                    try {
                        JSONObject statusObj = response.getJSONObject("status");
                        String indicator = statusObj.getString("indicator");
                        String description = statusObj.getString("description");
                        String updatedAt = response.getJSONObject("page").getString("updated_at");

                        String etatText;
                        if (indicator.equals("none")) etatText = "Opérationnel";
                        else if (indicator.equals("minor")) etatText = "Perturbé";
                        else etatText = "Panne";

                        services.add(new ServiceStatus(
                                "Discord",
                                etatText,
                                R.drawable.discord,
                                description,
                                updatedAt,
                                "Voir discordstatus.com"
                        ));

                        adapter.notifyDataSetChanged();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> error.printStackTrace()
        );

        Volley.newRequestQueue(this).add(request);
    }

    private void loadCloudflareStatus() {
        // Charge le statut Cloudflare
        String url = "https://www.cloudflarestatus.com/api/v2/status.json";

        com.android.volley.toolbox.JsonObjectRequest request = new com.android.volley.toolbox.JsonObjectRequest(
                com.android.volley.Request.Method.GET,
                url,
                null,
                response -> {
                    try {
                        org.json.JSONObject statusObj = response.getJSONObject("status");
                        String indicator = statusObj.getString("indicator");
                        String description = statusObj.getString("description");
                        String updatedAt = response.getJSONObject("page").getString("updated_at");

                        String etatText;
                        if (indicator.equals("none")) etatText = "Opérationnel";
                        else if (indicator.equals("minor")) etatText = "Perturbé";
                        else etatText = "Panne";

                        services.add(new ServiceStatus(
                                "Cloudflare",
                                etatText,
                                R.drawable.cloudflare,
                                description,
                                updatedAt,
                                "Voir cloudflarestatus.com"
                        ));

                        adapter.notifyDataSetChanged();
                    } catch (org.json.JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> error.printStackTrace()
        );

        com.android.volley.toolbox.Volley.newRequestQueue(this).add(request);
    }

    private void loadRedditStatus() {
        // Charge le statut Reddit
        String url = "https://www.redditstatus.com/api/v2/status.json";

        com.android.volley.toolbox.JsonObjectRequest request = new com.android.volley.toolbox.JsonObjectRequest(
                com.android.volley.Request.Method.GET,
                url,
                null,
                response -> {
                    try {
                        org.json.JSONObject statusObj = response.getJSONObject("status");
                        String indicator = statusObj.getString("indicator");
                        String description = statusObj.getString("description");
                        String updatedAt = response.getJSONObject("page").getString("updated_at");

                        String etatText;
                        if (indicator.equals("none")) etatText = "Opérationnel";
                        else if (indicator.equals("minor")) etatText = "Perturbé";
                        else etatText = "Panne";

                        services.add(new ServiceStatus(
                                "Reddit",
                                etatText,
                                R.drawable.reddit,
                                description,
                                updatedAt,
                                "Voir redditstatus.com"
                        ));

                        adapter.notifyDataSetChanged();
                    } catch (org.json.JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> error.printStackTrace()
        );

        com.android.volley.toolbox.Volley.newRequestQueue(this).add(request);
    }
}
