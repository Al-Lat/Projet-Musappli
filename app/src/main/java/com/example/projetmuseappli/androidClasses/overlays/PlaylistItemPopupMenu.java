package com.example.projetmuseappli.androidClasses.overlays;

import android.content.Context;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projetmuseappli.R;
import com.example.projetmuseappli.androidClasses.composant.adapters.PlaylistAdapter;
import com.example.projetmuseappli.elements.Masterlist;
import com.example.projetmuseappli.elements.Playlist;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * Classe utilitaire pour l'affichage du menu contextuel (Popup Menu) d'un item de playlist.
 */
public class PlaylistItemPopupMenu {

    /**
     * Affiche un menu contextuel pour un élément de playlist.
     *
     * @param view La vue ancre pour l'affichage du menu.
     * @param holder Le ViewHolder auquel est rattachée la playlist.
     * @param currentPlaylist La playlist sélectionnée.
     */
    public static void showPlaylistItemPopup(@NonNull View view, RecyclerView.ViewHolder holder, Playlist currentPlaylist) {

        PlaylistAdapter playlistAdapter = null;
        if (holder != null && holder.getBindingAdapter() instanceof PlaylistAdapter) {
            playlistAdapter = (PlaylistAdapter) holder.getBindingAdapter();
        }

        List<Playlist> playlistsToProcess = currentPlaylist != null ? Collections.singletonList(currentPlaylist) : Collections.emptyList();
        displayPopupMenu(view, playlistAdapter, playlistsToProcess);
    }

    /**
     * Affiche le menu contextuel d'options pour l'ensemble des playlists sélectionnées.
     *
     * @param view La vue ancre (ex: bouton flottant).
     * @param adapter L'adaptateur de playlists.
     * @param selectedPlaylists L'ensemble des playlists sélectionnées.
     */
    public static void showMultiplePlaylistsItemPopup(@NonNull View view, PlaylistAdapter adapter, @NonNull Set<Playlist> selectedPlaylists) {
        if (selectedPlaylists.isEmpty()) {
            return;
        }
        displayPopupMenu(view, adapter, new ArrayList<>(selectedPlaylists));
    }

    private static void displayPopupMenu(@NonNull View view, PlaylistAdapter adapter, @NonNull List<Playlist> playlistsToProcess) {
        if (playlistsToProcess.isEmpty()) {
            return;
        }

        Context context = view.getContext();
        PopupMenu pop = new PopupMenu(context, view);
        pop.getMenuInflater().inflate(R.menu.context_menu_playlist, pop.getMenu());

        if (playlistsToProcess.size() > 1) {
            pop.getMenu().findItem(R.id.action_rename_playlist).setVisible(false);
        }

        pop.setOnMenuItemClickListener((menuItem) -> {
            int itemId = menuItem.getItemId();

            if (itemId == R.id.action_delete_playlist) {
                ConfirmationDialogHelper.showDeleteConfirmationDialog(context, () -> {
                    for (Playlist playlist : playlistsToProcess) {
                        Masterlist.getInstance().removePlaylist(playlist);
                    }
                    if (adapter != null) {
                        adapter.clearSelection();
                        adapter.notifyDataSetChanged();
                    }
                });
                return true;
            } else if (itemId == R.id.action_rename_playlist) {
                if (playlistsToProcess.size() == 1) {
                    Playlist target = playlistsToProcess.get(0);
                    new PlaylistMenuChain(context).beginRenamePlaylistChain(target, () -> {
                        if (adapter != null) {
                            adapter.clearSelection();
                            adapter.notifyDataSetChanged();
                        }
                    });
                }
                return true;
            } else if (itemId == R.id.action_play_playlist) {
                Toast.makeText(context, context.getString(R.string.FEATURE_TODO), Toast.LENGTH_SHORT).show();
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
