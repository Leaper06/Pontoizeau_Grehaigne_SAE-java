package com.example.pontoizeau_grehaigne_sae_java;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
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
        // Appelle le onCreate de la classe mère
        setContentView(R.layout.activity_detail);
        // Associe le layout

        TextView tvName = findViewById(R.id.tv_detail_name);
        // Déclaration et initialisation du TextView

        TextView tvStatus = findViewById(R.id.tv_detail_status);
        // Déclaration et initialisation du TextView

        ImageButton imageButton2 = findViewById(R.id.imageButton2);
        // Déclaration et initialisation du bouton retour

        ImageView imgStatus = findViewById(R.id.iv_detail_status);

        String statut = getIntent().getStringExtra("EXTRA_STATUT");
        // Récupère le statut envoyé par l'activité précédente

        imageButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Méthode appelée lors du clic
                finish();
            }
        });

        if (statut != null) {
            // Vérifie que le statut n'est pas nul
            tvStatus.setText(statut);
            // Affiche le statut

            if (imgStatus != null) {
                if (statut.equals("Opérationnel")) {
                    imgStatus.setImageResource(R.drawable.status_ok);
                    // Affiche l'image OK
                } else if (statut.equals("Perturbé")) {
                    imgStatus.setImageResource(R.drawable.status_pok);
                    // Affiche l'image partiellement OK
                } else {
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
            tvName.setText(nomRecu);
            // Affiche le nom
        }
        if (statutRecu != null) {
            tvStatus.setText(statutRecu);
            // Affiche le statut
        }

        TextView tvDescription = findViewById(R.id.tv_detail_issue);
        // Déclare et initialise le TextView

        TextView tvDate = findViewById(R.id.tv_detail_date);
        // Déclare et initialise le TextView

        TextView tvRes = findViewById(R.id.tv_detail_res);
        // Déclare et initialise le TextView

        ImageView imgLogo = findViewById(R.id.detail_logo);
        // Déclare et initialise l'image

        String nom = getIntent().getStringExtra("EXTRA_NOM");
        // Récupère le nom du service

        String description = getIntent().getStringExtra("EXTRA_DESC");

        String date = getIntent().getStringExtra("EXTRA_DATE");

        String resolution = getIntent().getStringExtra("EXTRA_RES");

        int imageRes = getIntent().getIntExtra("EXTRA_IMAGE", 0);

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

        if (nom != null && nom.contains("GitHub")) {

            loadGithubIncidents();
            // Appelle l'API GitHub
        } else if (nom != null && nom.contains("Discord")) {
            loadDiscordIncidents();
        } else if (nom != null && nom.contains("Cloudflare")) {
            loadCloudflareIncidents();
        } else if (nom != null && nom.contains("Reddit")) {
            loadRedditIncidents();
        }
    }

    private void loadGithubIncidents() {
        // Méthode qui charge les incidents GitHub
        String url = "https://www.githubstatus.com/api/v2/incidents.json";

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                // Définit une requête GET
                url,
                null,
                response -> {
                    try {
                        JSONArray incidents = response.getJSONArray("incidents");
                        // Récupère le tableau des incidents

                        if (incidents.length() > 0) {
                            JSONObject latest = incidents.getJSONObject(0);
                            // Récupère le dernier incident

                            String issueName = latest.getString("name");
                            // Récupère le nom de l'incident
                            String issueDate = latest.getString("created_at");

                            String issueStatus = latest.getString("status");

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
                            TextView tvDate = findViewById(R.id.tv_detail_date);
                            TextView tvRes = findViewById(R.id.tv_detail_res);

                            tvIssue.setText("Dernier incident : " + issueName);
                            tvDate.setText("Date : " + datePropre);
                            tvRes.setText("État : " + etatEnFrancais);
                        } else {
                            TextView tvIssue = findViewById(R.id.tv_detail_issue);
                            // Récupère le TextView incident
                            tvIssue.setText("Aucun incident récent signalé.");
                            // Affiche le message
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        android.widget.Toast.makeText(this, "Erreur lecture JSON: " + e.getMessage(), android.widget.Toast.LENGTH_LONG).show();
                        // Affiche un message d'erreur
                    }
                },
                error -> {
                    error.printStackTrace();
                    // Affiche l'erreur réseau
                    android.widget.Toast.makeText(this, "Erreur Internet", android.widget.Toast.LENGTH_SHORT).show();
                }
        );

        Volley.newRequestQueue(this).add(request);
        // Ajoute la requête à la file d'attente
    }

    private void loadDiscordIncidents() {
        // Méthode qui charge les incidents Discord
        String url = "https://discordstatus.com/api/v2/incidents.json";

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
