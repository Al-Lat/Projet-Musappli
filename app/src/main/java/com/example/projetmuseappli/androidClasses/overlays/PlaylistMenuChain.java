package com.example.projetmuseappli.androidClasses.overlays;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projetmuseappli.R;
import com.example.projetmuseappli.elements.Masterlist;
import com.example.projetmuseappli.elements.Playlist;
import com.example.projetmuseappli.elements.Song;
import com.example.projetmuseappli.androidClasses.composant.adapters.PlaylistPreviewAdapter;

/**
 * Facade permettant d'enchainer plusieurs vues
 */
public class PlaylistMenuChain {

    private Context appContext;
    private Song songToAddToNewPlaylist;
    public PlaylistMenuChain(Context appContext){
        this.appContext = appContext;
    }

    public void beginAddToPlaylistChain(Song currentSong){
        this.songToAddToNewPlaylist = currentSong;
        showPlaylistPreviewList();
    }

    public void beginCreatePlaylistChain(){
        showPlaylistTitleInputDialog();
    }




    private void showPlaylistPreviewList(){
        View dialogView = LayoutInflater.from(appContext).inflate(R.layout.playlist_preview, null);

        /*instanciation du dialog*/
        android.app.AlertDialog dialog = new android.app.AlertDialog.Builder(appContext)
                .setView(dialogView)
                .create();

        /*gestion des actions sur item*/
        PlaylistPreviewAdapter.OnPlaylistPreviewClickListener listener = playlist -> {
            try {
                playlist.addSong(this.songToAddToNewPlaylist);
            } catch (IllegalArgumentException e) {
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
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent); /*fix des coins blancs*/
    }


    private void showPlaylistTitleInputDialog() {

        LayoutInflater inflater = LayoutInflater.from(appContext);
        View dialogView = inflater.inflate(R.layout.new_playlist_dialog, null);

        final EditText editText = dialogView.findViewById(R.id.playlistNameInput);

        Button btnCancel  = dialogView.findViewById(R.id.buttonCancel);
        Button btnConfirm = dialogView.findViewById(R.id.buttonConfirm);

        android.app.AlertDialog dialog = new android.app.AlertDialog.Builder(appContext)
                .setView(dialogView)
                .create();

        /*definition des actions*/
        btnConfirm.setOnClickListener((view) -> {
            String userInput = editText.getText().toString();
            if(isPlaylistTitleInputValid(userInput)){
                Masterlist.getInstance().addPlaylist(new Playlist(userInput,songToAddToNewPlaylist));
                dialog.dismiss();
            }

        });

        btnCancel.setOnClickListener((view) -> dialog.dismiss());

        dialog.show();
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent); /*fix des coins blancs*/
    }


    private boolean isPlaylistTitleInputValid(String input) {
        if (input.isEmpty() || input.length() > Playlist.NAME_MAX_LENGTH) {
            return false;
        }

        /*Titre Valide*/
        return true;

    }




}
