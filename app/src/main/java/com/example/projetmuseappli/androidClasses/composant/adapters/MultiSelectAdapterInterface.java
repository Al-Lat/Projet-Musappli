package com.example.projetmuseappli.androidClasses.composant.adapters;

import android.view.View;

import java.util.Set;

/**
 * Interface regroupant la gestion de la sélection multiple pour les adaptateurs de l'application.
 *
 * @param <T> Le type d'élément affiché.
 */
public interface MultiSelectAdapterInterface<T> extends MediaRecyclerViewAdapterInterface<T> {

    /**
     * Indique si l'adaptateur est actuellement en mode sélection.
     *
     * @return {@code true} si au moins un élément est sélectionné, {@code false} sinon.
     */
    boolean isSelectionMode();

    /**
     * Retourne l'ensemble des éléments actuellement sélectionnés.
     *
     * @return Un {@link Set} contenant les éléments sélectionnés.
     */
    Set<T> getSelectedItems();

    /**
     * Alterne l'état de sélection d'un élément.
     *
     * @param item L'élément à (dé)sélectionner.
     */
    void toggleSelection(T item);

    /**
     * Désélectionne tous les éléments et quitte le mode sélection.
     */
    void clearSelection();

    /**
     * Associe la vue du bouton flottant pour le contrôle de sa visibilité et des clics.
     *
     * @param fab La vue du bouton flottant.
     */
    void setFloatingActionButton(View fab);

}
