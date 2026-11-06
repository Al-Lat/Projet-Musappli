package com.example.projetmuseappli.androidClasses.overlays;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projetmuseappli.R;
import com.example.projetmuseappli.androidClasses.composant.adapters.SongAdapter;
import com.example.projetmuseappli.androidClasses.services.PlayerManager;
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
     * @param view        La vue ancre.
     * @param holder      Le ViewHolder contenant le morceau.
     * @param currentSong Le morceau sélectionné.
     */
    public static void showSongItemPopup(@NonNull View view, RecyclerView.ViewHolder holder, Song currentSong) {

        SongAdapter songAdapter = null;
        if (holder != null && holder.getBindingAdapter() instanceof SongAdapter) {
            songAdapter = (SongAdapter) holder.getBindingAdapter();
        }

        List<Song> songsToProcess = currentSong != null ? Collections.singletonList(currentSong) : Collections.emptyList();
        displayPopupMenu(view, songAdapter, songsToProcess, false);
    }

    /**
     * Affiche le menu contextuel d'options pour l'ensemble des morceaux sélectionnés en mode sélection multiple.
     *
     * @param view          La vue ancre (ex: bouton flottant).
     * @param adapter       L'adaptateur de sons.
     * @param selectedSongs L'ensemble des morceaux actuellement sélectionnés.
     */
    public static void showMultipleSongsItemPopup(@NonNull View view, SongAdapter adapter, @NonNull Set<Song> selectedSongs) {
        if (selectedSongs.isEmpty()) {
            return;
        }
        displayPopupMenu(view, adapter, new ArrayList<>(selectedSongs), true);
    }

    private static void displayPopupMenu(@NonNull View view, SongAdapter adapter,
                                         @NonNull List<Song> songsToProcess, boolean isMultiSelect) {
        if (songsToProcess.isEmpty()) {
            return;
        }

        Context context = view.getContext();
        PopupMenu pop = new PopupMenu(context, view);
        pop.getMenuInflater().inflate(R.menu.context_menu_song, pop.getMenu());

        // Le renommage n'est disponible que pour un seul morceau à la fois
        pop.getMenu().findItem(R.id.action_rename_song).setVisible(!isMultiSelect && songsToProcess.size() == 1);

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

            } else if (itemId == R.id.action_rename_song) {
                showRenameSongDialog(context, songsToProcess.get(0), adapter);
                return true;
            }
            return false;
        });
        pop.show();
    }

    /**
     * Affiche un dialogue permettant à l'utilisateur de saisir un nouveau nom pour le morceau.
     *
     * @param context Le contexte de l'application.
     * @param song    Le morceau à renommer.
     * @param adapter L'adaptateur à notifier après le renommage.
     */
    private static void showRenameSongDialog(@NonNull Context context, @NonNull Song song, SongAdapter adapter) {

        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.new_playlist_dialog, null);

        TextView dialogTitle = dialogView.findViewById(R.id.dialogTitleTextView);
        if (dialogTitle != null) {
            dialogTitle.setText(R.string.ask_rename_song_name);
        }

        final EditText editText = dialogView.findViewById(R.id.playlistNameInput);
        if (editText != null) {
            editText.setText(song.getTitre());
            editText.selectAll();
        }

        Button btnCancel  = dialogView.findViewById(R.id.buttonCancel);
        Button btnConfirm = dialogView.findViewById(R.id.buttonConfirm);

        AlertDialog dialog = new AlertDialog.Builder(context)
                .setView(dialogView)
                .create();

        btnConfirm.setOnClickListener((v) -> {
            String userInput = editText != null ? editText.getText().toString().trim() : "";
            if (!userInput.isEmpty()) {
                song.setTitre(userInput);
                Masterlist.getInstance().save();
                Masterlist.getInstance().notifyListeners();
                PlayerManager.getInstance(context).updateCurrentMediaItemTitle(song);
                if (adapter != null) {
                    adapter.notifyDataSetChanged();
                }
                dialog.dismiss();
            }
        });

        btnCancel.setOnClickListener((v) -> dialog.dismiss());

        dialog.show();
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }
    }

}
