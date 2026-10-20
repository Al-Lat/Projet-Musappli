/*
 * Classe representant une playlist
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

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

/**
 * Représente une liste de lecture (playlist) regroupant un ensemble de morceaux {@link Song}.
 */
public class Playlist implements Serializable {

    /*Identifiant unique du fichier playlist*/
    private UUID uuid;

    private List<Song> playlist;
    private String name;

    /** Longueur maximale autorisée pour le nom d'une playlist. */
    public final static int NAME_MAX_LENGTH = 40;

    private LocalDate dateCreation;

    private static final long serialVersionUID = 185449732L;

    private static final String FORMAT_DATE_DEFAUT = "dd/MM/yyyy";

    /**
     * Construit une nouvelle playlist contenant un morceau initial.
     *
     * @param name Nom de la playlist.
     * @param song Morceau initial à inclure.
     * @throws IllegalArgumentException si le nom spécifié est vide.
     */
    public Playlist(@NonNull String name, Song song){
        setUUID();
        if(name.isEmpty()){
            throw new IllegalArgumentException("Le nom de la playlist ne peut pas etre vide");
        }
        this.name = name;
        this.playlist = new LinkedList<>();
        if(song != null){
            this.playlist.add(song);
        }
        this.dateCreation = LocalDate.now();

    }

    /**
     * Construit une nouvelle playlist à partir d'une liste de morceaux.
     *
     * @param name Nom de la playlist.
     * @param list Liste initiale de morceaux {@link Song}.
     * @throws IllegalArgumentException si le nom spécifié est vide.
     */
    public Playlist(String name, List<Song> list){
        setUUID();
        if(name == null || name.isEmpty()){
            throw new IllegalArgumentException("Le nom de la playlist ne peut pas etre vide");
        }
        this.name = name;
        this.playlist = new LinkedList<>(list != null ? list : new LinkedList<>());
        this.dateCreation = LocalDate.now();

    }

    /**
     * Ajoute un morceau de musique à la playlist.
     *
     * @param song Le morceau {@link Song} à ajouter.
     * @throws IllegalArgumentException si le morceau est déjà présent dans la playlist.
     */
    public void addSong(@NonNull Song song) throws IllegalArgumentException{

        if(this.playlist.contains(song)){
            throw new IllegalArgumentException("La playlist contient déjà ce son");
        }

        this.playlist.add(song);
    }

    /**
     * Retire un morceau de la playlist.
     *
     * @param song Le morceau {@link Song} à supprimer.
     * @throws IllegalArgumentException si le morceau n'appartient pas à la playlist ou si la playlist ne contient qu'un seul élément.
     */
    public void removeSong(@NonNull Song song){
        if(!this.playlist.contains(song)){
            throw new IllegalArgumentException("La playlist ne contient pas ce son");
        }

        if(this.playlist.size()==1){
            throw new IllegalArgumentException("La playlist ne peut pas être vide");
        }

        this.playlist.remove(song);
    }

    /**
     * Retourne la liste des morceaux contenus dans la playlist.
     *
     * @return La liste des objets {@link Song}.
     */
    public List<Song> getSongList() {
        return playlist;
    }

    /**
     * Retourne le nom de la playlist.
     *
     * @return Le nom de la playlist.
     */
    public String getName() {
        return name;
    }

    /**
     * Retourne le nombre de morceaux dans la playlist.
     *
     * @return Le nombre de morceaux.
     */
    public int getSize(){return this.playlist.size();}

    /**
     * Modifie le nom de la playlist.
     *
     * @param name Le nouveau nom.
     */
    public void setName(@NonNull String name) {
        this.name = name;
    }

    /**
     * Retourne l'identifiant unique UUID de la playlist.
     *
     * @return L'identifiant {@link UUID}.
     */
    public UUID getUuid() {return this.uuid;}

    /**
     * Retourne la date de création de la playlist.
     *
     * @return La date {@link LocalDate} de création.
     */
    public LocalDate getDate() {
        return dateCreation;
    }

    /**
     * Renvoie la date de création sous forme de chaîne formatée selon le format par défaut ("dd/MM/yyyy").
     *
     * @return La chaîne représentant la date formatée.
     */
    public String getDateString(){
        return getDateString(FORMAT_DATE_DEFAUT);
    }

    /**
     * Renvoie la date de création sous forme de chaîne formatée.
     *
     * @param format Le motif de formatage de la date (ex: "dd/MM/yyyy").
     * @return La chaîne représentant la date formatée.
     */
    public String getDateString(String format){
        if(format == null || format.isEmpty()){
            format=FORMAT_DATE_DEFAUT;
        }
        return this.dateCreation.format(DateTimeFormatter.ofPattern(format));
    }

    private void setUUID(){
        this.uuid = UUID.randomUUID();
    }

}
