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

package com.example.projetmuseappli.fileManagment;

import android.content.Context;
import android.util.Log;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.UUID;

public class Masterlist implements Serializable {
    private static final long serialVersionUID = 165461L;
    private static final String FILE_NAME = "masterlist.ser";

    private static Context appContext;

    /*Collection des Song sous forme de hashmap pour la rapidité de recherche*/
    private HashMap<UUID,Song> masterlistHashMap;

    /*Collection des Song sous forme de LinkedList pour la rapidité d'ordonencement*/
    private LinkedList<Song> masterlistList;

    /*Collection des Playlist sous forme de LinkedList pour la rapidité d'ordonencement*/
    private LinkedList<Playlist> allPlaylistList;

    private static volatile Masterlist instance;

    /**
     * Tableau de Strings correspondant a chacun des formats de fichiers accepté par l'application.
     * les formats ont leurs designations systeme android, par exemple flac est audio/x-flac
     */
    final public static String[] ACCEPTED_FORMATS = {"audio/mpeg", "audio/x-flac", "audio/ogg", "application/ogg"};

    private Masterlist(){
        masterlistHashMap = new HashMap<>();
        masterlistList = new LinkedList<>();
        allPlaylistList = new LinkedList<>();
    }

    /**
     * Initialise l'instance de Masterlist en tentant de la désérialiser depuis le stockage interne.
     * Si le fichier n'existe pas ou est corrompu, une nouvelle instance est créée.
     * @param context Le contexte Android pour accéder aux fichiers
     */
    public static void init(Context context) {

        // On récupère le contexte global de l'application
        if (context != null) {
            appContext = context.getApplicationContext();
        }

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
    }

    /**
     * Retourne l'instance unique de Masterlist.
     * Note: init(Context) doit idéalement être appelé au moins une fois au démarrage.
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
     * Sauvegarde l'état actuel de la Masterlist dans le stockage interne.
     */
    public void save() {
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
     */
    protected Object readResolve() {
        synchronized (Masterlist.class) {
            if (instance == null) {
                instance = this;
            }
        }
        return instance;
    }
    /*Gestion de tout les sons*/
    public LinkedList<Song> getMasterlistList() {
        return masterlistList;
    }
    public Playlist getMasterPlaylist(String nomPlaylist){
        return new Playlist(nomPlaylist, masterlistList);
    }

    /*Gestion des sons*/
    public void addSong(Song song){
        masterlistHashMap.put(song.getUUID(), song);
        masterlistList.add(song);
        this.save();

    }

    public Song getSong(UUID uuid){
        return masterlistHashMap.get(uuid);
    }

    public Song getSong(int index){
        return masterlistList.get(index);
    }

    public boolean removeSong(UUID uuid){
        for(Song song : masterlistList){
            if(song.getUUID().equals(uuid)){
                masterlistList.remove(song);
                masterlistHashMap.remove(uuid);
                save();
                return true;
            }
        }
        return false;
    }

    public boolean removeSong(Song song){
        return removeSong(song.getUUID());
    }

    /*Gestion des playlist*/
    public void addPlaylist(Playlist playlist){
        allPlaylistList.add(playlist);
        save();
    }

    public void removePlaylist(Playlist playlist){
        allPlaylistList.remove(playlist);
        save();
    }

    public LinkedList<Playlist> getAllPlaylistList() {
        return allPlaylistList;
    }

    /**
     * Appelle "isEmpty" sur la hashmap interne de cette classe
     * @throws IllegalStateException si l'instance est null
     * @return  true si la liste est vide
     *          false sinon
     */
    public boolean isEmpty(){
        if (this.instance == null){
            throw new IllegalStateException("The Masterlist instance is null and cannot be tested");
        }

        return masterlistHashMap.isEmpty();
    }

}
