package com.example.projetmuseappli.androidClasses.activities;

import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;

import com.example.projetmuseappli.elements.Playlist;

/**
 * Classe utilitaire pour la création d'{@link Intent}s de navigation entre les activités.
 * Constructeur privé : cette classe ne doit pas être instanciée (SonarQube java:S1118).
 */
public class IntentManager {

    /**
     * Crée et retourne un {@link Intent} configuré pour ouvrir {@link PlaylistActivity}
     * avec la playlist donnée en paramètre.
     *
     * @param context Le contexte d'origine pour démarrer l'activité.
     * @param playlist La playlist à transmettre à l'activité cible.
     * @return L'intent initialisé.
     */
    public static Intent getPlaylistIntent(@NonNull Context context, @NonNull Playlist playlist){
        Intent intent = new Intent(context, PlaylistActivity.class);
        intent.putExtra("playlist", playlist);
        return intent;
    }

}
