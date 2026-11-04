package com.example.projetmuseappli.androidClasses.overlays;

import android.content.Context;
import android.view.View;
import android.widget.PopupMenu;

import androidx.recyclerview.widget.RecyclerView;

import com.example.projetmuseappli.R;
import com.example.projetmuseappli.androidClasses.composant.adapters.SongAdapter;
import com.example.projetmuseappli.elements.Masterlist;
import com.example.projetmuseappli.elements.Song;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * Classe utilitaire pour l'affichage du menu contextuel (Popup Menu) associé à un morceau de musique.
 */
public class SongItemPopupMenu {

    /**
     * Affiche le menu contextuel d'options pour un morceau de musique sélectionné.
     *
     * @param view La vue ancre.
     * @param holder Le ViewHolder contenant le morceau.
     * @param currentSong Le morceau sélectionné.
     */
    public static void showSongItemPopup(View view, RecyclerView.ViewHolder holder, Song currentSong) {

        SongAdapter songAdapter = null;
        if (holder != null && holder.getBindingAdapter() instanceof SongAdapter) {
            songAdapter = (SongAdapter) holder.getBindingAdapter();
        }

        List<Song> songsToProcess = currentSong != null ? Collections.singletonList(currentSong) : Collections.emptyList();
        displayPopupMenu(view, songAdapter, songsToProcess);
    }

    /**
     * Affiche le menu contextuel d'options pour l'ensemble des morceaux sélectionnés en mode sélection multiple.
     *
     * @param view La vue ancre (ex: bouton flottant).
     * @param adapter L'adaptateur de sons.
     * @param selectedSongs L'ensemble des morceaux actuellement sélectionnés.
     */
    public static void showMultipleSongsItemPopup(View view, SongAdapter adapter, Set<Song> selectedSongs) {
        if (selectedSongs == null || selectedSongs.isEmpty()) {
            return;
        }
        displayPopupMenu(view, adapter, new ArrayList<>(selectedSongs));
    }

    private static void displayPopupMenu(View view, SongAdapter adapter, List<Song> songsToProcess) {
        if (songsToProcess == null || songsToProcess.isEmpty()) {
            return;
        }

        Context context = view.getContext();
        PopupMenu pop = new PopupMenu(context, view);
        pop.getMenuInflater().inflate(R.menu.context_menu_song, pop.getMenu());

        pop.setOnMenuItemClickListener((menuItem) -> {
            int itemId = menuItem.getItemId();

            if (itemId == R.id.action_delete_song) {
                ConfirmationDialogHelper.showDeleteConfirmationDialog(context, () -> {
                    for (Song song : songsToProcess) {
                        Masterlist.getInstance().removeSong(song);
                    }
                    if (adapter != null) {
                        adapter.clearSelection();
                        adapter.notifyDataSetChanged();
                    }
                });
                return true;
            } else if (itemId == R.id.action_add_to_playlist) {
                new PlaylistMenuChain(context).beginAddToPlaylistChain(songsToProcess);
                if (adapter != null) {
                    adapter.clearSelection();
                }
                return true;
            }
            return false;
        });
        pop.show();
    }

}
