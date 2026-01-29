package com.example.pontoizeau_grehaigne_sae_java;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;          // <-- IMPORTANT
import android.widget.TextView;
import android.widget.ImageButton;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class DetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        // 1. Récupération des éléments visuels (Vérifiez bien vos IDs dans activity_detail.xml)
        TextView tvName = findViewById(R.id.tv_detail_name);
        TextView tvStatus = findViewById(R.id.tv_detail_status);


        ImageButton imageButton2 = findViewById(R.id.imageButton2);
        ImageView imgStatus = findViewById(R.id.iv_detail_status);
        String statut = getIntent().getStringExtra("EXTRA_STATUT");
        imageButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // revient à l'activité précédente
            }
        });
        if (statut != null) {
            tvStatus.setText(statut);

            // 2. AJOUTE CE BLOC POUR CHANGER LA COULEUR
            if (imgStatus != null) {
                if (statut.equals("Opérationnel")) {
                    imgStatus.setImageResource(R.drawable.status_ok);
                } else if (statut.equals("Perturbé")) {
                    imgStatus.setImageResource(R.drawable.status_pok);
                } else {
                    imgStatus.setImageResource(R.drawable.status_nok);
                }
            }
        }


        // 3. On récupère les données envoyées par la page précédente
        String nomRecu = getIntent().getStringExtra("EXTRA_NOM");
        String statutRecu = getIntent().getStringExtra("EXTRA_STATUT");

        // 4. On affiche les données
        if (nomRecu != null) {
            tvName.setText(nomRecu);
        }
        if (statutRecu != null) {
            tvStatus.setText(statutRecu);
        }
        TextView tvDescription = findViewById(R.id.tv_detail_issue); // L'ID que tu as mis pour "Dernière mise hors service..."
        TextView tvDate = findViewById(R.id.tv_detail_date);
        TextView tvRes = findViewById(R.id.tv_detail_res);
        ImageView imgLogo = findViewById(R.id.detail_logo);

        // 2. Récupération des données envoyées par la MainActivity
        String nom = getIntent().getStringExtra("EXTRA_NOM");

        String description = getIntent().getStringExtra("EXTRA_DESC");
        String date = getIntent().getStringExtra("EXTRA_DATE");
        String resolution = getIntent().getStringExtra("EXTRA_RES");
        int imageRes = getIntent().getIntExtra("EXTRA_IMAGE", 0);

        // 3. Affichage des données de base (celles qui viennent de la liste)
        if (nom != null) tvName.setText(nom);
        if (statut != null) tvStatus.setText(statut);
        if (description != null) tvDescription.setText("Problème : " + description);
        if (date != null) tvDate.setText("Date : " + date);
        if (resolution != null) tvRes.setText("Résolution : " + resolution);
        if (imageRes != 0) imgLogo.setImageResource(imageRes);


        if (nom != null && nom.contains("GitHub")) {
            loadGithubIncidents();
        } else if (nom != null && nom.contains("Discord")) {
            loadDiscordIncidents();
        }
        else if (nom != null && nom.contains("Cloudflare")) {
            loadCloudflareIncidents();
        }        else if (nom != null && nom.contains("Reddit")) {
            loadRedditIncidents();
        }
    }

    private void loadGithubIncidents() {
        // J'ai mis ton URL npoint pour le test.
        // Quand tu auras fini les tests, remets : "https://www.githubstatus.com/api/v2/incidents.json"
        String url = "https://www.githubstatus.com/api/v2/incidents.json";

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                response -> {
                    try {
                        JSONArray incidents = response.getJSONArray("incidents");

                        if (incidents.length() > 0) {
                            JSONObject latest = incidents.getJSONObject(0);

                            // --- 1. Récupération des données brutes ---
                            String issueName = latest.getString("name");
                            String issueDate = latest.getString("created_at");
                            String issueStatus = latest.getString("status");

                            // --- 2. Traduction du statut (C'est ça qui corrige "Voir site officiel") ---
                            String etatEnFrancais = issueStatus; // Valeur par défaut

                            if (issueStatus.equals("resolved")) {
                                etatEnFrancais = "Résolu";
                            } else if (issueStatus.equals("investigating")) {
                                etatEnFrancais = "Enquête en cours";
                            } else if (issueStatus.equals("identified")) {
                                etatEnFrancais = "Identifié";
                            } else if (issueStatus.equals("monitoring")) {
                                etatEnFrancais = "Sous surveillance";
                            }

                            // --- 3. Formatage de la date (ex: 2026-01-22 -> 22/01/2026) ---
                            String datePropre = issueDate;
                            if (issueDate.length() >= 10) {
                                String annee = issueDate.substring(0, 4);
                                String mois = issueDate.substring(5, 7);
                                String jour = issueDate.substring(8, 10);
                                datePropre = jour + "/" + mois + "/" + annee;
                            }

                            // --- 4. Mise à jour de l'affichage ---
                            TextView tvIssue = findViewById(R.id.tv_detail_issue);
                            TextView tvDate = findViewById(R.id.tv_detail_date);
                            TextView tvRes = findViewById(R.id.tv_detail_res);

                            tvIssue.setText("Dernier incident : " + issueName);
                            tvDate.setText("Date : " + datePropre);

                            // C'est cette ligne qui force le remplacement du texte par défaut !
                            tvRes.setText("État : " + etatEnFrancais);

                        } else {
                            // Si la liste "incidents" est vide dans le JSON
                            TextView tvIssue = findViewById(R.id.tv_detail_issue);
                            tvIssue.setText("Aucun incident récent signalé.");
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        android.widget.Toast.makeText(this, "Erreur lecture JSON: " + e.getMessage(), android.widget.Toast.LENGTH_LONG).show();
                    }
                },
                error -> {
                    error.printStackTrace();
                    android.widget.Toast.makeText(this, "Erreur Internet", android.widget.Toast.LENGTH_SHORT).show();
                }
        );

        Volley.newRequestQueue(this).add(request);
    }
    private void loadDiscordIncidents() {
        String url = "https://discordstatus.com/api/v2/incidents.json";

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                response -> {
                    try {
                        JSONArray incidents = response.getJSONArray("incidents");

                        if (incidents.length() > 0) {
                            JSONObject latest = incidents.getJSONObject(0);

                            // Récupération
                            String issueName = latest.getString("name");
                            String issueDate = latest.getString("created_at");
                            String issueStatus = latest.getString("status");

                            // Traduction
                            String etatEnFrancais = issueStatus;
                            if (issueStatus.equals("resolved")) etatEnFrancais = "Résolu";
                            else if (issueStatus.equals("investigating")) etatEnFrancais = "Enquête en cours";
                            else if (issueStatus.equals("monitoring")) etatEnFrancais = "Sous surveillance";

                            // Date
                            String datePropre = issueDate;
                            if (issueDate.length() >= 10) {
                                datePropre = issueDate.substring(8, 10) + "/" + issueDate.substring(5, 7) + "/" + issueDate.substring(0, 4);
                            }

                            // Affichage
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
                    } catch (JSONException e) { e.printStackTrace(); }
                },
                error -> error.printStackTrace()
        );
        Volley.newRequestQueue(this).add(request);
    }
    private void loadCloudflareIncidents() {
        String url = "https://www.cloudflarestatus.com/api/v2/incidents.json";

        com.android.volley.toolbox.JsonObjectRequest request = new com.android.volley.toolbox.JsonObjectRequest(
                com.android.volley.Request.Method.GET,
                url,
                null,
                response -> {
                    try {
                        org.json.JSONArray incidents = response.getJSONArray("incidents");

                        if (incidents.length() > 0) {
                            org.json.JSONObject latest = incidents.getJSONObject(0);

                            // --- Récupération ---
                            String issueName = latest.getString("name");
                            String issueDate = latest.getString("created_at");
                            String issueStatus = latest.getString("status");

                            // --- Traduction ---
                            String etatEnFrancais = issueStatus;
                            if (issueStatus.equals("resolved")) etatEnFrancais = "Résolu";
                            else if (issueStatus.equals("investigating")) etatEnFrancais = "Enquête en cours";
                            else if (issueStatus.equals("monitoring")) etatEnFrancais = "Sous surveillance";
                            else if (issueStatus.equals("identified")) etatEnFrancais = "Identifié";

                            // --- Date ---
                            String datePropre = issueDate;
                            if (issueDate.length() >= 10) {
                                datePropre = issueDate.substring(8, 10) + "/" + issueDate.substring(5, 7) + "/" + issueDate.substring(0, 4);
                            }

                            // --- Affichage ---
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
    }
    private void loadRedditIncidents() {
        String url = "https://www.redditstatus.com/api/v2/incidents.json";

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

                            // Traduction
                            String etatEnFrancais = issueStatus;
                            if (issueStatus.equals("resolved")) etatEnFrancais = "Résolu";
                            else if (issueStatus.equals("investigating")) etatEnFrancais = "Enquête";
                            else if (issueStatus.equals("identified")) etatEnFrancais = "Identifié";

                            // Date
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
    }
    }
