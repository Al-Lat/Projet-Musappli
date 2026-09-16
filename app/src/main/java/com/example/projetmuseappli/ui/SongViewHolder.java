package com.example.projetmuseappli.ui;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projetmuseappli.R;

/**
 * ViewHolder : Conteneur pour les vues d'un élément de la liste.
 * Le pattern ViewHolder permet d'éviter de faire des findViewById() à répétition
 * pendant le défilement de la liste, améliorant ainsi les performances.
 */
class SongViewHolder extends RecyclerView.ViewHolder {
    TextView textViewTitle;
    TextView textViewExtension;
    View buttonBurger;

    /**
     * Constructeur du ViewHolder effectuant la liaison avec les vues du layout.
     * @param itemView La vue racine de l'item.
     */
    public SongViewHolder(@NonNull View itemView) {
        super(itemView);
        // Liaison des éléments graphiques du layout item_song.xml par leur ID
        textViewTitle = itemView.findViewById(R.id.textViewTitle);
        textViewExtension = itemView.findViewById(R.id.textViewExtension);
        buttonBurger = itemView.findViewById(R.id.buttonBurger);
    }



}