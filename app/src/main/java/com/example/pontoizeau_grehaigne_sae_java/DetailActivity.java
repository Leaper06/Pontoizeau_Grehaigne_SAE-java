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
        imageButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // revient à l'activité précédente
            }
        });


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
        String statut = getIntent().getStringExtra("EXTRA_STATUT");
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

        // Si on est sur la page de GitHub, on va chercher les détails supplémentaires (Incidents)
        if (nom != null && nom.contains("GitHub")) {
            loadGithubIncidents();
        }
    }

    private void loadGithubIncidents() {
        String url = "https://www.githubstatus.com/api/v2/incidents.json";

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                response -> {
                    // Mouchard 1 : La réponse est arrivée
                    // android.widget.Toast.makeText(this, "Réponse reçue !", android.widget.Toast.LENGTH_SHORT).show();

                    try {
                        JSONArray incidents = response.getJSONArray("incidents");

                        if (incidents.length() > 0) {
                            JSONObject latest = incidents.getJSONObject(0);
                            String issueName = latest.getString("name");
                            // ... (ton code de récupération) ...

                            TextView tvIssue = findViewById(R.id.tv_detail_issue);
                            // ... (tes setText) ...

                            tvIssue.setText("Dernier incident : " + issueName);

                            // Mouchard 2 : Succès !
                            android.widget.Toast.makeText(this, "Mise à jour réussie !", android.widget.Toast.LENGTH_SHORT).show();
                        } else {
                            android.widget.Toast.makeText(this, "Aucun incident dans le JSON", android.widget.Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        // Mouchard 3 : Le JSON est mal écrit
                        android.widget.Toast.makeText(this, "Erreur lecture JSON: " + e.getMessage(), android.widget.Toast.LENGTH_LONG).show();
                    }
                },
                error -> {
                    error.printStackTrace();
                    // Mouchard 4 : Problème internet ou lien faux
                    android.widget.Toast.makeText(this, "Erreur Internet: " + error.getMessage(), android.widget.Toast.LENGTH_LONG).show();
                }
        );

        Volley.newRequestQueue(this).add(request);
    }
}