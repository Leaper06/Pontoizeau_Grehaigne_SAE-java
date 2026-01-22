package com.example.pontoizeau_grehaigne_sae_java;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class ServiceAdapter extends RecyclerView.Adapter<ServiceAdapter.ServiceViewHolder> {

    private List<ServiceStatus> serviceList;

    public ServiceAdapter(List<ServiceStatus> serviceList) {
        this.serviceList = serviceList;
    }

    @NonNull
    @Override
    public ServiceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // On lie le fichier XML item_service
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_service, parent, false);
        return new ServiceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ServiceViewHolder holder, int position) {
        // 1. Récupérer l'objet Service
        ServiceStatus service = serviceList.get(position);
        // 2. Afficher les infos de base (Nom, Logo)
        holder.tvName.setText(service.getNom());
        holder.imgLogo.setImageResource(service.getImageResId());
        // .mutate() est très important : il dit "modifie CE rond-là uniquement, pas ceux des autres lignes"
        android.graphics.drawable.GradientDrawable pastille =
                (android.graphics.drawable.GradientDrawable) holder.dotStatus.getBackground().mutate();
        // B. On choisit la couleur selon le texte du statut
        int couleurAUtiliser;
        String s = service.getStatut(); // Raccourci

        if (s.contains("Opérationnel")) {
            couleurAUtiliser = R.color.status_green; // Vert
        } else if (s.contains("Perturbé") || s.contains("Dégradé")) {
            couleurAUtiliser = R.color.status_orange; // Orange
        } else {
            couleurAUtiliser = R.color.status_red; // Rouge (pour "Panne", "Indisponible"...)
        }

        // C. On applique la couleur
        pastille.setColor(androidx.core.content.ContextCompat.getColor(holder.itemView.getContext(), couleurAUtiliser));

        // (Ton code de clic pour ouvrir les détails reste ici si tu l'avais ajouté avant)
        holder.itemView.setOnClickListener(v -> {
            android.content.Intent intent = new android.content.Intent(v.getContext(), DetailActivity.class);

            // --- ON AJOUTE LES DONNÉES ICI (C'est ce qui manquait) ---
            intent.putExtra("EXTRA_NOM", service.getNom());
            intent.putExtra("EXTRA_STATUT", service.getStatut());
            intent.putExtra("EXTRA_IMAGE", service.getImageResId());

            // Assure-toi que ces méthodes (Getters) existent dans ta classe ServiceStatus
            intent.putExtra("EXTRA_DESC", service.getDescription());
            intent.putExtra("EXTRA_DATE", service.getDate());
            intent.putExtra("EXTRA_RES", service.getResolution());

            v.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return serviceList.size();
    }

    // Le ViewHolder fait le lien avec les IDs du XML item_service
    public static class ServiceViewHolder extends RecyclerView.ViewHolder {
        TextView tvName;
        ImageView imgLogo;
        View dotStatus;

        public ServiceViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_name);
            imgLogo = itemView.findViewById(R.id.img_logo);
            dotStatus = itemView.findViewById(R.id.dot_status);
        }
    }
}