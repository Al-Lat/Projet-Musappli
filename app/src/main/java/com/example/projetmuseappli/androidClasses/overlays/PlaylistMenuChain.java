package com.example.projetmuseappli.androidClasses.overlays;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projetmuseappli.R;
import com.example.projetmuseappli.androidClasses.composant.adapters.PlaylistPreviewAdapter;
import com.example.projetmuseappli.elements.Masterlist;
import com.example.projetmuseappli.elements.Playlist;
import com.example.projetmuseappli.elements.Song;

import java.util.Collections;
import java.util.List;

/**
 * Façade permettant d'enchaîner l'affichage des boîtes de dialogue pour la sélection, la création et le renommage de playlists.
 */
public class PlaylistMenuChain {

    private Context appContext;
    private List<Song> songsToAddToNewPlaylist;

    /**
     * Initialise la chaîne de menus avec le contexte applicatif.
     *
     * @param appContext Le contexte de l'application.
     */
    public PlaylistMenuChain(Context appContext){
        this.appContext = appContext;
    }

    /**
     * Démarre la séquence permettant d'ajouter un morceau à une playlist existante ou d'en créer une nouvelle.
     *
     * @param currentSong Le morceau à ajouter.
     */
    public void beginAddToPlaylistChain(Song currentSong){
        this.songsToAddToNewPlaylist = currentSong != null ? Collections.singletonList(currentSong) : Collections.emptyList();
        showPlaylistPreviewList();
    }

    /**
     * Démarre la séquence permettant d'ajouter plusieurs morceaux à une playlist existante ou d'en créer une nouvelle.
     *
     * @param songs La liste de morceaux à ajouter.
     */
    public void beginAddToPlaylistChain(List<Song> songs){
        this.songsToAddToNewPlaylist = songs != null ? songs : Collections.emptyList();
        showPlaylistPreviewList();
    }

    /**
     * Démarre la séquence de création d'une nouvelle playlist.
     */
    public void beginCreatePlaylistChain(){
        showPlaylistTitleInputDialog();
    }

    /**
     * Démarre la séquence de renommage d'une playlist existante.
     *
     * @param playlist La playlist à renommer.
     * @param onRenamed Callback optionnel exécuté après la modification réussie du nom.
     */
    public void beginRenamePlaylistChain(Playlist playlist, Runnable onRenamed) {
        if (playlist == null) return;
        showRenamePlaylistDialog(playlist, onRenamed);
    }

    private void showPlaylistPreviewList(){
        View dialogView = LayoutInflater.from(appContext).inflate(R.layout.playlist_preview, null);

        /*instanciation du dialog*/
        AlertDialog dialog = new AlertDialog.Builder(appContext)
                .setView(dialogView)
                .create();

        /*gestion des actions sur item*/
        PlaylistPreviewAdapter.OnPlaylistPreviewClickListener listener = playlist -> {
            int addedCount = Masterlist.getInstance().addToPlaylist(playlist, songsToAddToNewPlaylist);
            if (addedCount < songsToAddToNewPlaylist.size()) {
                Toast.makeText(appContext, appContext.getString(R.string.ERROR_MESSAGE_SONG_ALREADY_IN_PLAYLIST)
                        , Toast.LENGTH_SHORT).show();
            }
            dialog.dismiss();
        };

        /*preparation de la vue*/
        RecyclerView recyclerView = dialogView.findViewById(R.id.recyclerViewPlaylists);
        recyclerView.setLayoutManager(new LinearLayoutManager(appContext));
        recyclerView.setAdapter(new PlaylistPreviewAdapter(appContext
                , Masterlist.getInstance().getMasterlistPlaylistList(),listener));

        /*definition des actions sur boutons*/
        Button btnCancel = dialogView.findViewById(R.id.buttonCancelAddToPlaylist);
        Button btnCreate = dialogView.findViewById(R.id.buttonCreateNewPlaylist);

        btnCancel.setOnClickListener(view -> {
            dialog.dismiss();
        });

        btnCreate.setOnClickListener(view -> {
            showPlaylistTitleInputDialog();
            dialog.dismiss();
        });

        dialog.show();
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }
    }

    private void showPlaylistTitleInputDialog() {

        LayoutInflater inflater = LayoutInflater.from(appContext);
        View dialogView = inflater.inflate(R.layout.new_playlist_dialog, null);

        final EditText editText = dialogView.findViewById(R.id.playlistNameInput);

        Button btnCancel  = dialogView.findViewById(R.id.buttonCancel);
        Button btnConfirm = dialogView.findViewById(R.id.buttonConfirm);

        AlertDialog dialog = new AlertDialog.Builder(appContext)
                .setView(dialogView)
                .create();

        /*definition des actions*/
        btnConfirm.setOnClickListener((view) -> {
            String userInput = editText.getText().toString();
            if(isPlaylistTitleInputValid(userInput)){
                Masterlist.getInstance().addPlaylist(new Playlist(userInput, songsToAddToNewPlaylist));
                dialog.dismiss();
            }

        });

        btnCancel.setOnClickListener((view) -> dialog.dismiss());

        dialog.show();
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }
    }

    private void showRenamePlaylistDialog(Playlist playlist, Runnable onRenamed) {

        LayoutInflater inflater = LayoutInflater.from(appContext);
        View dialogView = inflater.inflate(R.layout.new_playlist_dialog, null);

        TextView dialogTitle = dialogView.findViewById(R.id.dialogTitleTextView);
        if (dialogTitle != null) {
            dialogTitle.setText(R.string.ask_rename_playlist_name);
        }

        final EditText editText = dialogView.findViewById(R.id.playlistNameInput);
        if (editText != null) {
            editText.setText(playlist.getName());
            editText.selectAll();
        }

        Button btnCancel  = dialogView.findViewById(R.id.buttonCancel);
        Button btnConfirm = dialogView.findViewById(R.id.buttonConfirm);

        AlertDialog dialog = new AlertDialog.Builder(appContext)
                .setView(dialogView)
                .create();

        btnConfirm.setOnClickListener((view) -> {
            String userInput = editText != null ? editText.getText().toString().trim() : "";
            if (isPlaylistTitleInputValid(userInput)) {
                playlist.setName(userInput);
                Masterlist.getInstance().save();
                Masterlist.getInstance().notifyListeners();
                if (onRenamed != null) {
                    onRenamed.run();
                }
                dialog.dismiss();
            }
        });

        btnCancel.setOnClickListener((view) -> dialog.dismiss());

        dialog.show();
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }
    }

    private boolean isPlaylistTitleInputValid(String input) {
        if (input == null || input.isEmpty() || input.length() > Playlist.NAME_MAX_LENGTH) {
            return false;
        }

        /*Titre Valide*/
        return true;

    }

}
