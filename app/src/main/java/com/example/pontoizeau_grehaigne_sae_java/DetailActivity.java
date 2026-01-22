package com.example.pontoizeau_grehaigne_sae_java;

import android.os.Bundle;
import android.widget.TextView;
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
        TextView tvDescription = findViewById(R.id.tv_detail_issue);
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
                    try {
                        JSONArray incidents = response.getJSONArray("incidents");

                        if (incidents.length() > 0) {
                            JSONObject latest = incidents.getJSONObject(0);

                            String issueName = latest.getString("name");
                            String issueDate = latest.getString("created_at");
                            String issueStatus = latest.getString("status");

                            // Mise à jour de l'affichage avec les infos fraiches
                            TextView tvIssue = findViewById(R.id.tv_detail_issue);
                            TextView tvDate = findViewById(R.id.tv_detail_date);
                            TextView tvRes = findViewById(R.id.tv_detail_res);

                            tvIssue.setText("Dernier incident : " + issueName);
                            if (issueDate.length() >= 10) {
                                tvDate.setText("Date : " + issueDate.substring(0, 10));
                            }
                            tvRes.setText("État de résolution : " + issueStatus);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> error.printStackTrace()
        );

        Volley.newRequestQueue(this).add(request);
    }
}