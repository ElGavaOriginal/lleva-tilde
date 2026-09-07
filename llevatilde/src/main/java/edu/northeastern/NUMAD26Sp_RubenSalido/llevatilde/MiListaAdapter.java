package edu.northeastern.NUMAD26Sp_RubenSalido.llevatilde;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

/*
 This class is the adapter for the Mi Lista RecyclerView. It handles the list of words and their details.
 The layout for each item is defined in item_palabra.xml, and the adapter is defined here.
 The output is a list of words with their details.
*/

public class MiListaAdapter extends RecyclerView.Adapter<MiListaAdapter.PalabraViewHolder> {

    // List of words to display in the RecyclerView
    private final List<EntradaLista> palabras;
    public MiListaAdapter(List<EntradaLista> palabras) {
        this.palabras = palabras;
    }

    // Inflates the layout for each item and returns a ViewHolder
    @NonNull
    @Override
    public PalabraViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_palabra, parent, false);
        return new PalabraViewHolder(view);
    }

    // Binds the data for each item in the RecyclerView
    @Override
    public void onBindViewHolder(@NonNull PalabraViewHolder holder, int position) {
        EntradaLista entrada = palabras.get(position);
        holder.textPalabraItem.setText(entrada.getPalabra());
        String tipo = entrada.getTipo();
        holder.textDetalleItem.setText(tipo.isEmpty() ? "" : tipo);
        holder.itemView.setTranslationX(0f);
    }

    // Returns the number of items in the list
    @Override
    public int getItemCount() {
        return palabras.size();
    }

    // ViewHolder for each item in the RecyclerView
    public static class PalabraViewHolder extends RecyclerView.ViewHolder {
        TextView textPalabraItem;
        TextView textDetalleItem;

        // Constructor for the ViewHolder
        public PalabraViewHolder(@NonNull View itemView) {
            super(itemView);
            textPalabraItem = itemView.findViewById(R.id.textPalabraItem);
            textDetalleItem = itemView.findViewById(R.id.textDetalleItem);
        }
    }
}
