package com.example.projetmuseappli.androidClasses.composant.adapters;

import android.content.Context;

import java.util.List;

/**
 * Definie les methodes que doivent implementer les adapter de recyclerView de l'application
 */
public interface MediaRecyclerViewAdapterInterface<T> {

    List<T> getListOfAdapter();

    /** Remplace les données affichées et rafraîchit la RecyclerView. */
    void setItems(List<T> newItems);

    Context getContext();

}
