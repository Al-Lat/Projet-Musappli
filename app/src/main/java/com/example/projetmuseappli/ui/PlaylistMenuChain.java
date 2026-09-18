package com.example.projetmuseappli.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projetmuseappli.R;
import com.example.projetmuseappli.fileManagment.Masterlist;
import com.example.projetmuseappli.fileManagment.Playlist;
import com.example.projetmuseappli.fileManagment.Song;

/**
 * Facade permettant d'enchainer plusieurs vues
 */
public class PlaylistMenuChain {

    private Context appContext;
    public PlaylistMenuChain(Context appContext){
        this.appContext = appContext;
    }

    public void beginAddToPlaylistChain(Song currentSong){
        showPlaylistPreviewList(currentSong);
    }




    private void showPlaylistPreviewList(Song currentSong){
        View dialogView = LayoutInflater.from(appContext).inflate(R.layout.playlist_preview, null);
        //dialogView.setBackgroundColor(appContext.getColor(R.color.transparent));


        RecyclerView recyclerView = dialogView.findViewById(R.id.recyclerViewPlaylists);
        recyclerView.setLayoutManager(new LinearLayoutManager(appContext));
        recyclerView.setAdapter(new PlaylistPreviewAdapter(appContext
                , Masterlist.getInstance().getAllPlaylistList()));


        Button btnCancel = dialogView.findViewById(R.id.buttonCancelAddToPlaylist);
        Button btnCreate = dialogView.findViewById(R.id.buttonCreateNewPlaylist);


        android.app.AlertDialog dialog = new android.app.AlertDialog.Builder(appContext)
                .setView(dialogView)
                .create();

        /*definition des actions*/
        btnCancel.setOnClickListener(view -> {
            dialog.dismiss();
        });

        btnCreate.setOnClickListener(view -> {
            showPlaylistTitleInputDialog(currentSong);
            dialog.dismiss();
        });

        //TODO géré click sur une playlist

        dialog.show();
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent); /*fix des coins blancs*/
    }


    private void showPlaylistTitleInputDialog(Song songToAddToNewPlaylist) {

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
            } //TODO geré input invalide

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
