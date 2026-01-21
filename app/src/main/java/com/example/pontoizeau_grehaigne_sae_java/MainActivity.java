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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        services = new ArrayList<>();
        adapter = new ServiceAdapter(services);
        View mainLayout = findViewById(R.id.main); // Assure-toi ue cet ID existe dans ton XML (voir étape 2)
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

        // 5. Créer ta liste de services de test (Feature 2 de ton document)

        // Note : utilise le nom exact de ton icône wifi (ex: wifi_ok)
        //services.add(new ServiceStatus("GitHub", "Opérationnel", R.drawable.github, "Aucun incident signalé", "18/01/2025", "N/A"));
        services.add(new ServiceStatus("Discord", "Indisponible", R.drawable.discord,
                "Panne majeure des serveurs vocaux", "18/01/2025", "En cours de résolution"));
        services.add(new ServiceStatus("Cloudflare", "Opérationnel", R.drawable.cloudflare,
                "Trafic normal", "17/01/2025", "Résolu"));

        services.add(new ServiceStatus("GitLab", "Dégradé", R.drawable.gitlab,
                "Lenteurs sur les CI/CD Pipelines", "16/01/2025", "Investigué"));

        // 6. Lier l'Adapter

        recyclerView.setAdapter(adapter);
        loadGithubStatus();
    }

    // Méthode pour récupérer le statut GitHub via Volley
    private void loadGithubStatus() {
        String url = "https://www.githubstatus.com/api/v2/status.json";
        // Request
        // On dit qu'on veut un "JsonObject"
        com.android.volley.toolbox.JsonObjectRequest request = new com.android.volley.toolbox.JsonObjectRequest(
                com.android.volley.Request.Method.GET,
                url,
                null, // Pas de paramètres à envoyer
                new com.android.volley.Response.Listener<org.json.JSONObject>() {
                    @Override
                    public void onResponse(org.json.JSONObject response) {
                        try {
                            // 3. On rentre dans l'objet "status" du JSON
                            org.json.JSONObject statusObj = response.getJSONObject("status");

                            // 4. On récupère les infos brutes
                            String indicator = statusObj.getString("indicator"); // ex: "none", "minor", "major"
                            String description = statusObj.getString("description"); // ex: "All Systems Operational"
                            String updatedAt = response.getJSONObject("page").getString("updated_at"); // La date

                            // 5. TRADUCTION : On transforme le code "indicator" en image Android
                            // 1. On décide que la GROSSE image sera toujours le logo GitHub
                            int iconRes = R.drawable.github;
                            String etatText;
                            // 2. On définit juste le texte (C'est lui qui pilotera la couleur de la pastille via l'Adapter !)
                            if (indicator.equals("none")) {
                                etatText = "Opérationnel"; // L'adapter verra ce mot -> Pastille Verte
                            } else if (indicator.equals("minor")) {
                                etatText = "Perturbé";     // L'adapter verra ce mot -> Pastille Orange
                            } else {
                                etatText = "Panne";        // Autre mot -> Pastille Rouge
                            }
                            // 3. On crée la carte
                            services.add(0, new ServiceStatus(
                                    "GitHub API",
                                    etatText,   // "Opérationnel"
                                    iconRes,    // R.drawable.github (Le logo)
                                    description,
                                    updatedAt,
                                    "Voir site officiel"
                            ));
                            // 4. On rafraîchit
                            adapter.notifyDataSetChanged();

                        } catch (org.json.JSONException e) {
                            e.printStackTrace(); // En cas d'erreur de lecture du JSON
                        }
                    }
                },
                new com.android.volley.Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(com.android.volley.VolleyError error) {
                        // En cas d'erreur internet (pas de wifi, serveur HS...)
                        // On pourrait ajouter un service "Erreur" ici
                        error.printStackTrace();
                    }
                }
        );

        // 8. Ajouter la demande à la file d'attente du facteur (Volley)
        com.android.volley.toolbox.Volley.newRequestQueue(this).add(request);
    }
}