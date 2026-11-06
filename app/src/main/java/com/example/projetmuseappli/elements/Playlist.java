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
import java.util.Collections;
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
    public Playlist(@NonNull String name, List<Song> list){
        setUUID();
        if(name.isEmpty()){
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
     * Retire le morceau situé à l'index spécifié de la playlist.
     *
     * @param index L'index du morceau à supprimer.
     * @return Le morceau {@link Song} supprimé.
     * @throws IllegalArgumentException si la playlist ne contient qu'un seul élément.
     * @throws IndexOutOfBoundsException si l'index est hors limites.
     */
    public Song removeSong(int index) {
        if (this.playlist.size() <= 1) {
            throw new IllegalArgumentException("La playlist ne peut pas être vide");
        }
        return this.playlist.remove(index);
    }

    /**
     * Déplace un morceau d'une position initiale à une nouvelle position dans la playlist.
     *
     * @param fromPosition La position initiale du morceau.
     * @param toPosition La nouvelle position du morceau.
     * @throws IndexOutOfBoundsException si fromPosition ou toPosition est invalide.
     */
    public void moveSong(int fromPosition, int toPosition) {
        if (fromPosition < 0 || fromPosition >= playlist.size()
                || toPosition < 0 || toPosition >= playlist.size()) {
            throw new IndexOutOfBoundsException("Position invalide lors du déplacement du morceau : "
                    + fromPosition + " -> " + toPosition);
        }
        if (fromPosition == toPosition) {
            return;
        }
        Song moved = playlist.remove(fromPosition);
        playlist.add(toPosition, moved);
    }

    /**
     * Échange la position de deux morceaux dans la playlist.
     *
     * @param index1 L'index du premier morceau.
     * @param index2 L'index du deuxième morceau.
     * @throws IndexOutOfBoundsException si l'un des index est invalide.
     */
    public void swapSongs(int index1, int index2) {
        if (index1 < 0 || index1 >= playlist.size()
                || index2 < 0 || index2 >= playlist.size()) {
            throw new IndexOutOfBoundsException("Index invalide lors de l'échange des morceaux : "
                    + index1 + ", " + index2);
        }
        if (index1 == index2) {
            return;
        }
        Collections.swap(playlist, index1, index2);
    }

    /**
     * Récupère le morceau situé à l'index spécifié dans la playlist.
     *
     * @param index L'index du morceau recherché.
     * @return Le morceau {@link Song} situé à cet index.
     * @throws IndexOutOfBoundsException si l'index est hors limites.
     */
    public Song getSong(int index) {
        return this.playlist.get(index);
    }

    /**
     * Vérifie si la playlist contient le morceau spécifié.
     *
     * @param song Le morceau {@link Song} à vérifier.
     * @return {@code true} si le morceau est présent, {@code false} sinon.
     */
    public boolean containsSong(Song song) {
        return song != null && this.playlist.contains(song);
    }

    /**
     * Renvoie l'index de la première occurrence du morceau spécifié dans la playlist,
     * ou -1 si la playlist ne contient pas ce morceau.
     *
     * @param song Le morceau à chercher.
     * @return L'index du morceau ou -1.
     */
    public int indexOf(Song song) {
        return this.playlist.indexOf(song);
    }

    /**
     * Retourne une copie défensive de la liste des morceaux contenus dans la playlist.
     * Les modifications de la liste retournée n'affectent pas la liste interne de la playlist.
     *
     * @return Une nouvelle liste (copie) des objets {@link Song}.
     */
    public List<Song> getSongList() {
        return new LinkedList<>(playlist);
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
     * @throws IllegalArgumentException si le nom spécifié est nul ou vide.
     */
    public void setName(@NonNull String name) {
        if (name.trim().isEmpty()) {
            throw new IllegalArgumentException("Le nom de la playlist ne peut pas être vide");
        }
        this.name = name;
    }

    /**
     * Trie la liste des morceaux de la playlist par titre (ordre alphabétique A-Z par défaut).
     */
    public void sortByName() {
        sortByName(true);
    }

    /**
     * Trie la liste des morceaux de la playlist par titre (ordre alphabétique).
     *
     * @param ascending {@code true} pour l'ordre croissant (A à Z), {@code false} pour décroissant (Z à A).
     */
    public void sortByName(boolean ascending) {
        if (ascending) {
            this.playlist.sort(new Song.SongNameComparator());
        } else {
            this.playlist.sort(new Song.SongNameComparator().reversed());
        }
    }

    /**
     * Trie la liste des morceaux de la playlist par date d'importation (ordre chronologique par défaut).
     */
    public void sortByDate() {
        sortByDate(true);
    }

    /**
     * Trie la liste des morceaux de la playlist par date d'importation.
     *
     * @param ascending {@code true} pour l'ordre croissant (plus ancien au plus récent),
     *                  {@code false} pour l'ordre décroissant (plus récent au plus ancien).
     */
    public void sortByDate(boolean ascending) {
        if (ascending) {
            this.playlist.sort(new Song.SongDateComparator());
        } else {
            this.playlist.sort(new Song.SongDateComparator().reversed());
        }
    }

    /**
     * Remplace la liste des morceaux contenus dans la playlist.
     *
     * @param list La nouvelle liste de morceaux.
     * @throws IllegalArgumentException si la liste spécifiée est nulle ou vide.
     */
    public void setSongList(@NonNull List<Song> list) {
        if (list.isEmpty()) {
            throw new IllegalArgumentException("La liste de morceaux ne peut pas être nulle ou vide");
        }
        this.playlist = new LinkedList<>(list);
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
