package com.ph03nix.functionalproject;

import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ph03nix.functionalproject.Security.UniqueCode;

import java.util.List;

public class GestorVentasAdapter extends RecyclerView.Adapter<GestorVentasAdapter.GestorVentaViewHolder> {

    private List<GestorVentas> gestores;
    private OnGestorClickListener listener;

    public interface OnGestorClickListener {
        void onGestorClick(GestorVentas gestor);
    }

    public GestorVentasAdapter(List<GestorVentas> gestores, OnGestorClickListener listener) {
        this.gestores = gestores;
        this.listener = listener;
    }

    @NonNull
    @Override
    public GestorVentaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.gestor_ventas_item, parent, false);
        return new GestorVentaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GestorVentaViewHolder holder, int position) {
        GestorVentas gestor = gestores.get(position);
        holder.tvNombre.setText(gestor.getName());
        holder.tvCodigo.setText("Código: " + gestor.getUniqueCode());


        holder.itemView.setOnClickListener(v -> listener.onGestorClick(gestor));
    }

    @Override
    public int getItemCount() {
        return gestores.size();
    }

    public void updateData(List<GestorVentas> newGestores) {
        gestores = newGestores;
        notifyDataSetChanged();
    }

    static class GestorVentaViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombre, tvCodigo;

        public GestorVentaViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombre = itemView.findViewById(R.id.tvNombre);
            tvCodigo = itemView.findViewById(R.id.tvCodigo);
        }
    }
}
