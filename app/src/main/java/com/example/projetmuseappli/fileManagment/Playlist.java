/*
 * Classe rrpresentant une playlist
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

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Playlist implements Serializable {

    private LinkedList<Song> playlist;
    private String nom;
    private static final long serialVersionUID = 1L;

    public Playlist(String nom, Song song){
        if(nom.isEmpty()){
            throw new IllegalArgumentException("Le nom de la playlist ne peut pas etre vide");
        }
        this.nom = nom;
        this.playlist = new LinkedList<>();
        this.playlist.add(song);

    }

    public Playlist(String nom, List<Song> list){
        if(nom.isEmpty()){
            throw new IllegalArgumentException("Le nom de la playlist ne peut pas etre vide");
        }
        this.nom = nom;
        this.playlist = new LinkedList<>(list);

    }

    public void addSong(@NonNull Song song){

        if(this.playlist.contains(song)){
            throw new IllegalArgumentException("La playlist contient déjà ce son");
        }

        this.playlist.add(song);
    }

    public void removeSong(@NonNull Song song){
        if(!this.playlist.contains(song)){
            throw new IllegalArgumentException("La playlist ne contient pas ce son");
        }

        if(this.playlist.size()==1){
            throw new IllegalArgumentException("La playlist ne peut pas être vide");
        }

        this.playlist.remove(song);
    }

    public LinkedList<Song> getPlaylist() {
        return playlist;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(@NonNull String nom) {
        this.nom = nom;
    }
}
