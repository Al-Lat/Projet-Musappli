package com.example.projetmuseappli.androidClasses.activities;

import android.content.Context;
import android.content.Intent;

import com.example.projetmuseappli.elements.Playlist;

public class IntentManager {

    public static Intent getPlaylistIntent(Context context, Playlist playlist){
        Intent intent = new Intent(context, PlaylistActivity.class);
        intent.putExtra("playlist", playlist);
        return intent;
    }

}
