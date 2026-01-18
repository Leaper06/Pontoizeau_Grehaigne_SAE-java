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
        // 1. Récupérer l'objet Service à cette position
        ServiceStatus service = serviceList.get(position);
        // 2. Afficher les infos (comme avant)
        holder.tvName.setText(service.getNom());
        holder.imgLogo.setImageResource(service.getImageResId());
        // "itemView" représente toute la carte (CardView)
        holder.itemView.setOnClickListener(v -> {
            // A. Créer l'Intent (De la carte actuelle -> Vers DetailActivity)
            // On doit utiliser v.getContext() car nous ne sommes pas dans une Activity
            android.content.Intent intent = new android.content.Intent(v.getContext(), DetailActivity.class);

            // B. Envoyer les données
            intent.putExtra("EXTRA_NOM", service.getNom());
            intent.putExtra("EXTRA_STATUT", service.getStatut());

            // C. Lancer l'activite
            v.getContext().startActivity(intent);
        });
        holder.itemView.setOnClickListener(v -> {
            android.content.Intent intent = new android.content.Intent(v.getContext(), DetailActivity.class);

            // On passe les infos de base
            intent.putExtra("EXTRA_NOM", service.getNom());
            intent.putExtra("EXTRA_STATUT", service.getStatut());

            // --- ON AJOUTE LES NOUVELLES INFOS ---
            intent.putExtra("EXTRA_DESC", service.getDescription());
            intent.putExtra("EXTRA_DATE", service.getDate());
            intent.putExtra("EXTRA_RES", service.getResolution());
            // On peut aussi passer l'image !
            intent.putExtra("EXTRA_IMAGE", service.getImageResId());

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