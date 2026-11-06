package com.example.projetmuseappli.androidClasses.utilitaries;

import com.example.projetmuseappli.elements.Playlist;
import com.example.projetmuseappli.elements.Song;

import java.util.List;

/**
 * Classe utilitaire centralisant la logique de tri d'une liste de morceaux selon un {@link SortMode}.
 */
public class SongSortHelper {

    /**
     * Enumération des modes de tri disponibles pour la liste de morceaux.
     */
    public enum SortMode {
        CUSTOM,    // Personnalisé (ordre réel de la liste en mémoire)
        DATE_DESC, // Date d'ajout (plus récent au plus ancien)
        DATE_ASC,  // Date d'ajout (plus ancien au plus récent)
        NAME_ASC,  // Nom (A à Z)
        NAME_DESC  // Nom (Z à A)
    }

    /**
     * Applique le mode de tri spécifié sur la liste de morceaux fournie en s'appuyant sur la classe {@link Playlist}.
     *
     * @param songs La liste de morceaux à trier.
     * @param sortMode Le mode de tri {@link SortMode} à appliquer.
     * @return La liste de morceaux triée.
     */
    public static List<Song> sortSongs(List<Song> songs, SortMode sortMode) {
        if (songs == null || songs.isEmpty() || sortMode == null) {
            return songs;
        }

        Playlist tempPlaylist = new Playlist("temp", songs);
        switch (sortMode) {
            case CUSTOM:
                // Conservé dans l'ordre réel de la liste en mémoire (pas de tri)
                return songs;
            case DATE_ASC:
                tempPlaylist.sortByDate(true);
                return tempPlaylist.getSongList();
            case NAME_ASC:
                tempPlaylist.sortByName(true);
                return tempPlaylist.getSongList();
            case NAME_DESC:
                tempPlaylist.sortByName(false);
                return tempPlaylist.getSongList();
            case DATE_DESC:
            default:
                tempPlaylist.sortByDate(false);
                return tempPlaylist.getSongList();
        }
    }
}
