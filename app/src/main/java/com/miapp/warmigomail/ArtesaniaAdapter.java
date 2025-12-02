package com.miapp.warmigomail;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Locale;

public class ArtesaniaAdapter extends RecyclerView.Adapter<ArtesaniaAdapter.ViewHolder> {

    public interface OnItemEliminarListener {
        void onEliminar(int posicion);
    }

    public interface OnItemEditarListener {
        void onEditar(int posicion);
    }

    private final List<ArtesaniaItem> listaHamburguesas;
    private final OnItemEliminarListener eliminarListener;
    private final OnItemEditarListener editarListener;

    public ArtesaniaAdapter(List<ArtesaniaItem> lista,
                            OnItemEliminarListener eliminarListener,
                            OnItemEditarListener editarListener) {
        this.listaHamburguesas = lista;
        this.eliminarListener = eliminarListener;
        this.editarListener = editarListener;
    }

    @Override
    public int getItemCount() {
        return listaHamburguesas.size();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View vista = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_hamburguesa, parent, false);
        return new ViewHolder(vista);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ArtesaniaItem item = listaHamburguesas.get(position);

        holder.tvNumero.setText(String.valueOf(position + 1));
        holder.tvDetalle.setText(item.getDetalle());
        holder.tvPrecio.setText(String.format(Locale.getDefault(), "S/%.2f", item.getPrecio()));

        boolean estaCompleto = !item.getDetalle().equals("Sin detalles");

        // Mostrar solo uno de los íconos si el item está completo
        holder.ivCheck.setVisibility(estaCompleto ? View.VISIBLE : View.GONE);
        holder.ivEliminar.setVisibility(estaCompleto ? View.GONE : View.VISIBLE);
        
        holder.ivEliminar.setOnClickListener(v -> {
            if (eliminarListener != null) {
                eliminarListener.onEliminar(holder.getAdapterPosition());
            }
        });

        holder.itemView.setOnClickListener(v -> {
            if (editarListener != null) {
                editarListener.onEditar(holder.getAdapterPosition());
            }
        });
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvNumero, tvDetalle, tvPrecio;
        ImageView ivEliminar, ivCheck;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNumero = itemView.findViewById(R.id.tv_numero);
            tvDetalle = itemView.findViewById(R.id.tv_detalle);
            tvPrecio = itemView.findViewById(R.id.tv_precio);
            ivEliminar = itemView.findViewById(R.id.iv_eliminar);
            ivCheck = itemView.findViewById(R.id.iv_check);
        }
    }
}
