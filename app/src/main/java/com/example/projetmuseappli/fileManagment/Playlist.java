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
import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;

public class Playlist implements Serializable {

    private LinkedList<Song> playlist;
    private String name;
    public final static int NAME_MAX_LENGTH = 40;

    private LocalDate date;
    private static final long serialVersionUID = 185449732L;

    public Playlist(@NonNull String name, Song song){
        if(name.isEmpty()){
            throw new IllegalArgumentException("Le nom de la playlist ne peut pas etre vide");
        }
        this.name = name;
        this.playlist = new LinkedList<>();
        this.playlist.add(song);
        this.date = LocalDate.now();

    }

    public Playlist(String name, List<Song> list){
        if(name.isEmpty()){
            throw new IllegalArgumentException("Le nom de la playlist ne peut pas etre vide");
        }
        this.name = name;
        this.playlist = new LinkedList<>(list);
        this.date = LocalDate.now();

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

    public LinkedList<Song> getSongList() {
        return playlist;
    }

    public String getName() {
        return name;
    }

    public int getSize(){return this.playlist.size();}

    public void setName(@NonNull String name) {
        this.name = name;
    }

    public LocalDate getDate() {
        return date;
    }
}
