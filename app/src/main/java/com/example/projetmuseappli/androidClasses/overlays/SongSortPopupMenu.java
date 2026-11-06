package com.example.projetmuseappli.androidClasses.overlays;

import android.content.Context;
import android.view.View;
import android.widget.PopupMenu;

import androidx.annotation.NonNull;

import com.example.projetmuseappli.R;
import com.example.projetmuseappli.androidClasses.utilitaries.SongSortHelper.SortMode;

/**
 * Classe utilitaire pour l'affichage du menu contextuel (Popup Menu) permettant de trier les morceaux.
 */
public class SongSortPopupMenu {

    /**
     * Listener recevant le mode de tri sélectionné.
     */
    public interface OnSortOptionSelectedListener {
        void onSortOptionSelected(SortMode sortMode);
    }

    /**
     * Affiche le menu contextuel de tri ancré sur la vue spécifiée (sans l'option "Personalisé" par défaut).
     *
     * @param anchorView La vue sur laquelle ancrer le menu.
     * @param listener Le callback notifié de l'option choisie.
     */
    public static void showSortPopup(@NonNull View anchorView, @NonNull OnSortOptionSelectedListener listener) {
        showSortPopup(anchorView, false, listener);
    }

    /**
     * Affiche le menu contextuel de tri ancré sur la vue spécifiée.
     *
     * @param anchorView La vue sur laquelle ancrer le menu.
     * @param showCustomOption Indique s'il faut afficher l'option de filtre "Personalisé".
     * @param listener Le callback notifié de l'option choisie.
     */
    public static void showSortPopup(@NonNull View anchorView, boolean showCustomOption, @NonNull OnSortOptionSelectedListener listener) {
        Context context = anchorView.getContext();
        PopupMenu popup = new PopupMenu(context, anchorView);
        popup.getMenuInflater().inflate(R.menu.menu_sort_options, popup.getMenu());

        if (!showCustomOption) {
            popup.getMenu().removeItem(R.id.sort_custom);
        }

        popup.setOnMenuItemClickListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.sort_custom) {
                listener.onSortOptionSelected(SortMode.CUSTOM);
                return true;
            } else if (itemId == R.id.sort_date_desc) {
                listener.onSortOptionSelected(SortMode.DATE_DESC);
                return true;
            } else if (itemId == R.id.sort_date_asc) {
                listener.onSortOptionSelected(SortMode.DATE_ASC);
                return true;
            } else if (itemId == R.id.sort_name_asc) {
                listener.onSortOptionSelected(SortMode.NAME_ASC);
                return true;
            } else if (itemId == R.id.sort_name_desc) {
                listener.onSortOptionSelected(SortMode.NAME_DESC);
                return true;
            }
            return false;
        });

        popup.show();
    }
}
