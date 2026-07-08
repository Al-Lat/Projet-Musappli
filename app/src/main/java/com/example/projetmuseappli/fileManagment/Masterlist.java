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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class Masterlist implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final String FILE_NAME = "masterlist.ser";

    private HashMap<UUID,Song> masterlistHashMap;
    private ArrayList<Song> masterlistArrayList;
    private ArrayList<Song> allPlaylistList;

    private static volatile Masterlist instance;

    private Masterlist(){
        masterlistHashMap = new HashMap<>();
        masterlistArrayList = new ArrayList<>();
        allPlaylistList = new ArrayList<>();
    }

    /**
     * Initialise l'instance de Masterlist en tentant de la désérialiser depuis le stockage interne.
     * Si le fichier n'existe pas ou est corrompu, une nouvelle instance est créée.
     * @param context Le contexte Android pour accéder aux fichiers
     */
    public static void init(Context context) {
        if (instance == null) {
            synchronized (Masterlist.class) {
                if (instance == null) {
                    try (FileInputStream fis = context.openFileInput(FILE_NAME);
                         ObjectInputStream ois = new ObjectInputStream(fis)) {
                        // readObject() appelle automatiquement readResolve() si elle est définie
                        instance = (Masterlist) ois.readObject();
                    } catch (Exception e) {
                        Log.i("Masterlist", "Aucune sauvegarde trouvée ou erreur de lecture, création d'une nouvelle liste.");
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
     * @param context Le contexte Android
     */
    public void save(Context context) {
        try (FileOutputStream fos = context.openFileOutput(FILE_NAME, Context.MODE_PRIVATE);
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

    public ArrayList<Song> getMasterlistArrayList() {
        return masterlistArrayList;
    }

    public void addSong(Song song){
        masterlistHashMap.put(song.getUUID(), song);
        masterlistArrayList.add(song);

    }

    public Song getSong(UUID uuid){
        return masterlistHashMap.get(uuid);
    }

    public Song getSong(int index){
        return masterlistArrayList.get(index);
    }
    
    public boolean removeSong(UUID uuid){
        for(Song song : masterlistArrayList){
            if(song.getUUID().equals(uuid)){
                masterlistArrayList.remove(song);
                masterlistHashMap.remove(uuid);
                return true;
            }
        }
        return false;
    }
}
