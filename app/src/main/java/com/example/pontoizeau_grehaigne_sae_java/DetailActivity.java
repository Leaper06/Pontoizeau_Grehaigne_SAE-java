package com.example.pontoizeau_grehaigne_sae_java;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;          // <-- IMPORTANT
import android.widget.TextView;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;

public class DetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
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
        TextView tvResolution = findViewById(R.id.tv_detail_res);
        android.widget.ImageView imgLogo = findViewById(R.id.detail_logo); // L'image en haut

// 2. Récupérer les variables
        String description = getIntent().getStringExtra("EXTRA_DESC");
        String date = getIntent().getStringExtra("EXTRA_DATE");
        String resolution = getIntent().getStringExtra("EXTRA_RES");
        int imageId = getIntent().getIntExtra("EXTRA_IMAGE", 0);

// 3. Remplacer le texte "en dur" du XML par nos variables Java
        if (description != null) tvDescription.setText("Problème : " + description);
        if (date != null) tvDate.setText("Date : " + date);
        if (resolution != null) tvResolution.setText("État : " + resolution);
        if (imageId != 0) imgLogo.setImageResource(imageId);
    }
}