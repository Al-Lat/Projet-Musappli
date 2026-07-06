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

import java.io.Serializable;
import java.util.ArrayList;

public class Playlist implements Serializable {

    private ArrayList<Song> playlist;
    private String nom;
    private static final long serialVersionUID = 1L;

    public Playlist(String nom, Song song){
        if(nom.isEmpty()){
            throw new IllegalArgumentException("Le nom de la playlist ne peut pas etre vide");
        }
        this.nom = nom;
        this.playlist = new ArrayList<>();
        this.playlist.add(song);

    }


}
