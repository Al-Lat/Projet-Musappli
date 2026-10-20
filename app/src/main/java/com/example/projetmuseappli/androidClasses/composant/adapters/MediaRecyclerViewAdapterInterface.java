package com.example.projetmuseappli.androidClasses.composant.adapters;

import android.content.Context;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

/**
 * Définit les méthodes que doivent implémenter les adaptateurs de {@link RecyclerView} de l'application.
 *
 * @param <T> Le type d'élément affiché par l'adaptateur.
 */
public interface MediaRecyclerViewAdapterInterface<T> {

    /**
     * Retourne la liste des éléments actuellement gérés par l'adaptateur.
     *
     * @return La liste d'éléments.
     */
    List<T> getListOfAdapter();

    /**
     * Remplace les données affichées et rafraîchit la {@link RecyclerView}.
     *
     * @param newItems La nouvelle liste d'éléments à afficher.
     */
    void setItems(List<T> newItems);

    /**
     * Retourne le contexte Android associé à cet adaptateur.
     *
     * @return Le {@link Context}.
     */
    Context getContext();

}
