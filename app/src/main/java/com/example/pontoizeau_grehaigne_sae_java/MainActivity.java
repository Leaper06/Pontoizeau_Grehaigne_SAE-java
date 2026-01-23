package com.example.pontoizeau_grehaigne_sae_java;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;
import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.view.View;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.widget.ImageView;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {
    private List<ServiceStatus> services;
    private ServiceAdapter adapter;
    private ImageView wifiStatusIcon;
    private ImageView refreshButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        wifiStatusIcon = findViewById(R.id.wifi_status);
        refreshButton = findViewById(R.id.btn_refresh);


        services = new ArrayList<>();
        View mainLayout = findViewById(R.id.main);
        if (mainLayout != null) {
            ViewCompat.setOnApplyWindowInsetsListener(mainLayout, (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            });
        }

        // 3. Trouver le RecyclerView
        RecyclerView recyclerView = findViewById(R.id.recycler_services);

        // 4. Définir la grille (2 colonnes)
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        // Test de l'accès Internet réel
        new Thread(() -> {
            boolean internetOk = NetworkUtils.isNetworkConnected(this) && NetworkUtils.isInternetAvailable();
            runOnUiThread(() -> {
                if (internetOk) {
                    // Internet fonctionne
                    Toast.makeText(this, "Internet OK", Toast.LENGTH_SHORT).show();
                    System.out.println("Internet OK");
                } else {
                    // Pas d'accès Internet
                    Toast.makeText(this, "Pas d'accès Internet", Toast.LENGTH_SHORT).show();
                    System.out.println("Pas d'accès Internet");
                }
            });
        }).start();
        // 5. Créer la liste de services de test (Feature 2 de ton document)
        services = new ArrayList<>();


        adapter = new ServiceAdapter(services);
        recyclerView.setAdapter(adapter);
        // APPELS API
        loadGithubStatus();
        loadDiscordStatus();
        loadCloudflareStatus();
        loadRedditStatus();

        // 7.bouton de rafraichissement
        refreshButton.setOnClickListener(v -> refreshAll());
    }

    // Méthode pour récupérer le statut GitHub via Volley
    private void loadGithubStatus() {
        String url = "https://www.githubstatus.com/api/v2/status.json";

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
                        if (indicator.equals("none")) {
                            etatText = "Opérationnel";
                        } else if (indicator.equals("minor")) {
                            etatText = "Perturbé";
                        } else {
                            etatText = "Panne";
                        }


                        // On utilise .add(0, ...) pour l'ajouter tout en haut de la liste
                        services.add(0, new ServiceStatus(
                                "GitHub API",  // Nom spécifique
                                etatText,
                                R.drawable.github,
                                description,
                                updatedAt,
                                "Voir site officiel"
                        ));


                        adapter.notifyDataSetChanged();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> {
                    error.printStackTrace();
                    // Optionnel : Gérer l'erreur visuellement
                }
        );

        Volley.newRequestQueue(this).add(request);
    }

    /**
     * Vérifie si Internet est disponible réellement
     * Combinaison : réseau actif + accès HTTP réel
     * @return true si Internet accessible, false sinon
     */
    private boolean isInternetConnected() {
        return NetworkUtils.isNetworkConnected(this) && NetworkUtils.isInternetAvailable();
    }

    /**
     * Met à jour l'icône Wi-Fi pour GitHub (index 0)
     * Doit être exécuté sur le thread UI
     */
    private void updateWifiIcon() {
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


    /**
     * BroadcastReceiver pour détecter les changements de connexion réseau
     * Met à jour l'icône Wi-Fi pour le service GitHub (index 0)
     */
    private final BroadcastReceiver wifiReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            updateWifiIcon();
        }
    };
    /**
     * Rafraîchit tous les éléments de l'écran
     * (connexion Internet + statuts des services)
     */
    private void refreshAll() {
        updateWifiIcon();
        loadGithubStatus();
        loadCloudflareStatus();
        loadRedditStatus();


        // loadCloudflareStatus();
        // loadGitlabStatus();
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Désenregistrement du BroadcastReceiver pour éviter les fuites mémoire
        unregisterReceiver(wifiReceiver);
    }

    private void loadDiscordStatus() {
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
                        if (indicator.equals("none")) {
                            etatText = "Opérationnel";
                        } else if (indicator.equals("minor")) {
                            etatText = "Perturbé";
                        } else {
                            etatText = "Panne";
                        }

                        // On ajoute Discord à la liste
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
    // Méthode pour récupérer le statut Cloudflare
    private void loadCloudflareStatus() {
        String url = "https://www.cloudflarestatus.com/api/v2/status.json ";

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

                        // 1. Choix du texte et de la couleur
                        String etatText;
                        if (indicator.equals("none")) etatText = "Opérationnel";
                        else if (indicator.equals("minor")) etatText = "Perturbé";
                        else etatText = "Panne";

                        // 2. Ajout à la liste
                        services.add(new ServiceStatus(
                                "Cloudflare",    // Nom
                                etatText,        // Statut
                                R.drawable.cloudflare, // Icône Cloudflare
                                description,
                                updatedAt,
                                "Voir cloudflarestatus.com"
                        ));

                        // 3. Rafraîchir
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
        String url = "https://api.npoint.io/6bb4de2dc75e03fe6880";

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

                        // Ajout à la liste
                        services.add(new ServiceStatus(
                                "Reddit",
                                etatText,
                                R.drawable.reddit, // Attention : Il te faut l'image reddit.png !
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