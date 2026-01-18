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
        // On remplit la carte avec les données d'un service
        ServiceStatus service = serviceList.get(position);
        holder.tvName.setText(service.getNom());
        holder.imgLogo.setImageResource(service.getImageResId());

        // On pourra ici changer la couleur de la pastille plus tard
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