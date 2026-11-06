/*
 * Classe stockant l'ensemble des objets Song ou playlist de l'application
 * Copyright (C) 2026  Alexandre Laouat
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.example.projetmuseappli.elements;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Classe singleton gérant la sauvegarde, la persistance et l'accès centralisé aux données
 * de l'application (morceaux de musique et playlists).
 */
public class Masterlist implements Serializable {
    private static final long serialVersionUID = 165461L;
    private static final String FILE_NAME = "masterlist.ser";

    private static Context appContext;

    /*Collection des Song sous forme de hashmap pour la rapidité de recherche*/
    private HashMap<UUID,Song> masterlistSongHashMap;

    /*Collection des Playlist sous forme de hashmap pour la rapidité de recherche*/
    private HashMap<UUID,Playlist> masterlistPlaylistHashMap;

    private static volatile Masterlist instance;

    private transient List<MasterlistListener> listeners;

    private List<MasterlistListener> getListeners() {
        if (listeners == null) {
            listeners = new CopyOnWriteArrayList<>(); // safe si (dés)inscription pendant l'itération
        }
        return listeners;
    }

    /**
     * Tableau de Strings correspondant à chacun des formats de fichiers acceptés par l'application.
     * Les formats utilisent leurs désignations système Android, par exemple "audio/x-flac".
     */
    final public static String[] ACCEPTED_FORMATS = {"audio/mpeg", "audio/x-flac", "audio/ogg", "application/ogg"};

    private Masterlist(){
        masterlistSongHashMap = new LinkedHashMap<>();
        masterlistPlaylistHashMap = new LinkedHashMap<>();
    }

    /**
     * Initialise l'instance de Masterlist en tentant de la désérialiser depuis le stockage interne.
     * Si le fichier n'existe pas ou est corrompu, une nouvelle instance est créée.
     * Si une instance non null existe déjà, rien n'est fait.
     *
     * @param context Le contexte Android pour accéder au stockage interne.
     */
    public static void init(@NonNull Context context) {
        appContext = context.getApplicationContext();

        if (instance == null) {
            synchronized (Masterlist.class) {
                if (instance == null) {
                    try (FileInputStream fis = context.openFileInput(FILE_NAME);
                         ObjectInputStream ois = new ObjectInputStream(fis)) {
                        // readObject() appelle automatiquement readResolve() si elle est définie
                        instance = (Masterlist) ois.readObject();

                    } catch (Exception e) {
                        Log.i("Masterlist", "Aucune sauvegarde trouvée ou erreur " +
                                "de lecture, création d'une nouvelle liste.");
                        instance = new Masterlist();
                    }
                }
            }
        }
        /*Convertion des null en empty*/
        instance.fixNullLists();
    }

    /**
     * Retourne l'instance unique de Masterlist.
     * Note: {@link #init(Context)} doit idéalement être appelé au moins une fois au démarrage.
     *
     * @return L'instance unique de {@link Masterlist}.
     */
    public static Masterlist getInstance() {
        if (instance == null) {
            synchronized (Masterlist.class) {
                if (instance == null) {
                    instance = new Masterlist();
                }
            }
        }
        return instance;
    }

    /**
     * Sauvegarde l'état actuel de la Masterlist dans le stockage interne de l'application.
     */
    public synchronized void save() {
        if (appContext == null) {
            Log.e("Masterlist", "Erreur lors de la sauvegarde : appContext est null");
            return;
        }
        try (FileOutputStream fos = appContext.openFileOutput(FILE_NAME, Context.MODE_PRIVATE);
             ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            oos.writeObject(this);
        } catch (IOException e) {
            Log.e("Masterlist", "Erreur lors de la sauvegarde de la Masterlist", e);
        }
    }

    /**
     * Gère la désérialisation pour garantir l'unicité du Singleton.
     * Si une instance existe déjà, on retourne celle-ci.
     * Sinon, on définit l'instance actuelle comme étant celle désérialisée.
     *
     * @return L'instance unique de {@link Masterlist}.
     */
    protected Object readResolve() {
        synchronized (Masterlist.class) {
            if (instance == null) {
                instance = this;
            }
        }
        return instance;
    }

    /**
     * Enregistre un écouteur de modifications sur la Masterlist.
     *
     * @param listener L'écouteur à ajouter.
     */
    public void addListener(@NonNull MasterlistListener listener) {
        getListeners().add(listener);
    }

    /**
     * Supprime un écouteur de modifications de la Masterlist.
     *
     * @param listener L'écouteur à retirer.
     */
    public void removeListener(@NonNull MasterlistListener listener) {
        getListeners().remove(listener);
    }

    public void notifyListeners() {
        for (MasterlistListener listener : getListeners()) {
            listener.onMasterlistChanged();
        }
    }

    /**
     * Retourne l'ensemble des morceaux de musique enregistrés, triés par date d'import du plus récent au plus ancien.
     *
     * @return Une {@link LinkedList} contenant tous les objets {@link Song}.
     */
    public LinkedList<Song> getMasterlistSongList() {
        LinkedList<Song> list = new LinkedList<>(masterlistSongHashMap.values());
        Collections.reverse(list);
        list.sort((s1, s2) -> s2.getDate().compareTo(s1.getDate()));
        return list;
    }

    /**
     * Ajoute un nouveau morceau de musique à la Masterlist et enregistre les modifications.
     *
     * @param song Le morceau {@link Song} à ajouter.
     */
    public void addSong(@NonNull Song song){
        masterlistSongHashMap.put(song.getUUID(), song);
        this.save();
        this.notifyListeners();
    }

    /**
     * Récupère un morceau par son identifiant unique UUID.
     *
     * @param uuid L'identifiant {@link UUID} du morceau recherché.
     * @return Le morceau {@link Song} correspondant, ou {@code null} si non trouvé.
     */
    public Song getSong(@NonNull UUID uuid){
        return masterlistSongHashMap.get(uuid);
    }

    /**
     * Supprime un morceau de la Masterlist en utilisant son identifiant UUID.
     *
     * @param uuid L'identifiant {@link UUID} du morceau à supprimer.
     */
    public void removeSong(@NonNull UUID uuid){
        Song song = getSong(uuid);
        if (song != null) {
            removeSongFromAllPlaylists(song);
            masterlistSongHashMap.remove(uuid);
            this.save();
            this.notifyListeners();
        }
    }

    /**
     * Supprime un morceau de la Masterlist.
     *
     * @param song Le morceau {@link Song} à supprimer.
     */
    public void removeSong(@NonNull Song song){
        removeSong(song.getUUID());
    }

    /**
     * Ajoute une nouvelle playlist à la Masterlist et sauvegarde l'état.
     *
     * @param playlist La {@link Playlist} à ajouter.
     */
    public void addPlaylist(@NonNull Playlist playlist){
        masterlistPlaylistHashMap.put(playlist.getUuid(),playlist);
        save();
        this.notifyListeners();
    }

    /**
     * Récupère une playlist par son identifiant unique UUID.
     *
     * @param uuid L'identifiant {@link UUID} de la playlist.
     * @return La {@link Playlist} correspondante, ou {@code null} si non trouvée.
     */
    public Playlist getPlaylist(@NonNull UUID uuid){
        return masterlistPlaylistHashMap.get(uuid);
    }

    /**
     * Supprime une playlist de la Masterlist.
     *
     * @param playlist La {@link Playlist} à supprimer.
     */
    public void removePlaylist(@NonNull Playlist playlist){
        masterlistPlaylistHashMap.remove(playlist.getUuid());
        save();
        this.notifyListeners();
    }

    /**
     * Déplace un morceau au sein de la liste d'une playlist et sauvegarde la Masterlist.
     *
     * @param playlistUuid L'identifiant UUID de la playlist.
     * @param fromPosition La position d'origine du morceau.
     * @param toPosition La nouvelle position du morceau.
     */
    public void moveSongInPlaylist(@NonNull UUID playlistUuid, int fromPosition, int toPosition) {
        Playlist playlist = getPlaylist(playlistUuid);
        if (playlist != null) {
            playlist.moveSong(fromPosition, toPosition);
            save();
            this.notifyListeners();
        }
    }

    /**
     * Ajoute un morceau de musique à une playlist donnée et sauvegarde les modifications.
     *
     * @param playlist La {@link Playlist} cible.
     * @param song Le morceau {@link Song} à ajouter.
     */
    public void addToPlaylist(@NonNull Playlist playlist, @NonNull Song song) {
        playlist.addSong(song);
        save();
        this.notifyListeners();
    }

    /**
     * Ajoute plusieurs morceaux de musique à une playlist donnée et sauvegarde les modifications.
     *
     * @param playlist La {@link Playlist} cible.
     * @param songs La liste des morceaux {@link Song} à ajouter.
     * @return Le nombre de morceaux effectivement ajoutés.
     */
    public int addToPlaylist(@NonNull Playlist playlist, @NonNull List<Song> songs) {
        int addedCount = 0;
        for (Song song : songs) {
            if (song != null && !playlist.containsSong(song)) {
                playlist.addSong(song);
                addedCount++;
            }
        }
        if (addedCount > 0) {
            save();
            this.notifyListeners();
        }
        return addedCount;
    }

    /**
     * Retire un morceau d'une playlist donnée et sauvegarde les modifications.
     *
     * @param song Le morceau {@link Song} à retirer.
     * @param playlist La {@link Playlist} dont le morceau doit être supprimé.
     */
    public void removeFromPlaylist(@NonNull Song song, @NonNull Playlist playlist){
        playlist.removeSong(song);
        save();
        this.notifyListeners();
    }

    /**
     * Retourne l'ensemble des playlists enregistrées.
     *
     * @return Une {@link LinkedList} contenant toutes les instances de {@link Playlist}.
     */
    public LinkedList<Playlist> getMasterlistPlaylistList() {
        return new LinkedList<>(masterlistPlaylistHashMap.values());
    }

    /**
     * Récupère l'élément (Song ou Playlist) correspondant à l'identifiant UUID fourni.
     *
     * @param uuid L'identifiant {@link UUID} de l'élément recherché.
     * @return L'objet {@link Playlist} ou {@link Song} correspondant, ou {@code null} si introuvable.
     */
    public Object getRawObject(@NonNull UUID uuid){
        if (masterlistPlaylistHashMap.containsKey(uuid)) {
            return masterlistPlaylistHashMap.get(uuid);
        } else if (masterlistSongHashMap.containsKey(uuid)) {
            return masterlistSongHashMap.get(uuid);
        }
        return null;
    }

    /**
     * Vérifie si la liste de morceaux est vide.
     *
     * @return {@code true} si la liste des morceaux est vide, {@code false} sinon.
     * @throws IllegalStateException si l'instance de la Masterlist est null.
     */
    public boolean isEmpty(){
        if (Masterlist.instance == null){
            throw new IllegalStateException("The Masterlist instance is null and cannot be tested");
        }

        return masterlistSongHashMap.isEmpty();
    }

    /**
     * Réinitialise la Masterlist en supprimant tous les morceaux et toutes les playlists.
     */
    public void clear() {
        masterlistSongHashMap.clear();
        masterlistPlaylistHashMap.clear();
        this.save();
        this.notifyListeners();
    }

    /*convertit les listes/hashmaps attributs de la classe null en listes vides*/
    private void fixNullLists(){
        if (this.masterlistSongHashMap == null ){
            this.masterlistSongHashMap = new LinkedHashMap<>();
        }
        if (this.masterlistPlaylistHashMap == null ){
            this.masterlistPlaylistHashMap = new LinkedHashMap<>();
        }
    }

    /*Supprime un son de toute les playlists de l'application*/
    private void removeSongFromAllPlaylists(@NonNull Song song) {
        List<Playlist> playlistsToRemove = new LinkedList<>();
        for (Playlist playlist : masterlistPlaylistHashMap.values()) {
            if (playlist.containsSong(song)) {
                if (playlist.getSize() <= 1) {
                    playlistsToRemove.add(playlist);
                } else {
                    playlist.removeSong(song);
                }
            }
        }
        for (Playlist playlist : playlistsToRemove) {
            removePlaylist(playlist);
        }
    }

}
