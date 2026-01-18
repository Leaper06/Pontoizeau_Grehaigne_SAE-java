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

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);


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
        List<ServiceStatus> services = new ArrayList<>();
        // Note : utilise le nom exact de ton icône wifi (ex: wifi_ok)
        services.add(new ServiceStatus("GitHub", "Opérationnel", R.drawable.github));
        services.add(new ServiceStatus("Discord", "Indisponible", R.drawable.discord));
        services.add(new ServiceStatus("Cloudflare", "Opérationnel", R.drawable.cloudflare));
        services.add(new ServiceStatus("GitLab", "Dégradé", R.drawable.gitlab));

        // 6. Lier l'Adapter
        ServiceAdapter adapter = new ServiceAdapter(services);
        recyclerView.setAdapter(adapter);
    }
}