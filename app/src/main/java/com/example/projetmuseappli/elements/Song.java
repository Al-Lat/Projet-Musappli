/*
 * Classe representant les informations sur un fichier son, cette classe n'est pas le fichier en lui
 * meme
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
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

/**
 * Représente les métadonnées et informations d'un morceau de musique importé dans l'application.
 */
public class Song implements Serializable {
    private static final long serialVersionUID = 2414591651L;

    /*Identifiant unique du fichier son*/
    private UUID UUID;
    private String Extension;
    private String titre;
    private LocalDate dateImport;

    private String FORMAT_DATE_DEFAUT = "dd/MM/yyyy";

    /** Liste immuable des extensions de fichiers audio supportées par l'application. */
    public static final List<String> EXTENSIONS_VALIDES =
            Collections.unmodifiableList(Arrays.asList("mp3", "flac", "ogg"));

    /**
     * Construit un objet Song avec ses attributs principaux.
     *
     * @param uuid L'identifiant unique du morceau.
     * @param Extension L'extension du fichier (ex: mp3, flac, ogg).
     * @param titre Le titre du morceau.
     * @throws IllegalArgumentException si l'extension ou le titre est invalide.
     */
    public Song(UUID uuid, @NonNull String Extension, @NonNull String titre){
        this.UUID = uuid;

        if(EXTENSIONS_VALIDES.contains(Extension)){
            this.Extension = Extension;
        } else {
            String message = "L'extention du fichier : "+titre+"\nUUID : "+uuid+" est invalide. " +
                    "Extentions valide : "+getStringExtensionValides();
            throw new IllegalArgumentException(message);
        }

        if(titre.isEmpty()){
            throw new IllegalArgumentException("le titre ne peut pas etre vide");
        }
        this.titre = titre;

        this.dateImport = LocalDate.now();

    }

    /**
     * Retourne l'identifiant unique UUID du morceau.
     *
     * @return L'identifiant {@link UUID}.
     */
    public UUID getUUID() {
        return UUID;
    }

    /**
     * Retourne l'extension du fichier audio.
     *
     * @return L'extension sous forme de chaîne.
     */
    public String getExtension() {
        return Extension;
    }

    /**
     * Retourne le titre du morceau.
     *
     * @return Le titre du morceau.
     */
    public String getTitre() {
        return titre;
    }

    /**
     * Retourne la date d'importation du morceau dans l'application.
     *
     * @return La date {@link LocalDate} d'importation.
     */
    public LocalDate getDate() {
        return dateImport;
    }

    /**
     * Renvoie la date d'import sous forme de chaîne formatée selon le format par défaut ("dd/MM/yyyy").
     *
     * @return La chaîne de la date formatée.
     */
    public String getDateString(){
        return getDateString(FORMAT_DATE_DEFAUT);
    }

    /**
     * Renvoie la date d'import sous forme de chaîne formatée.
     *
     * @param format Motif de formatage de la date (ex: "dd/MM/yyyy").
     * @return La chaîne de la date formatée.
     */
    public String getDateString(String format){
        if(format == null || format.isEmpty()){
            format=FORMAT_DATE_DEFAUT;
        }
        return this.dateImport.format(DateTimeFormatter.ofPattern(format));
    }

    /**
     * Indique si l'extension spécifiée est reconnue comme valide.
     *
     * @param extension La chaîne représentant l'extension du fichier.
     * @return {@code true} si l'extension est supportée, {@code false} sinon.
     */
    public static boolean isExtentionValide(String extension){
        return extension != null && EXTENSIONS_VALIDES.contains(extension.toLowerCase());
    }

    @Override
    public String toString() {
        return "Song{" +
                "UUID=" + UUID +
                ", Extension='" + Extension + '\'' +
                ", titre='" + titre + '\'' +
                ", dateImport=" + dateImport +
                '}';
    }

    /*
     * Renvoie la liste des extensions valides sous la forme "ext1, ext2, ext3"
     */
    private String getStringExtensionValides(){
        StringBuilder sb = new StringBuilder(EXTENSIONS_VALIDES.get(0));
        for(int index = 1;index<EXTENSIONS_VALIDES.size()-1;index++){
            sb.append(", ");
            sb.append(EXTENSIONS_VALIDES.get(index));
        }
        return sb.toString();
    }

    /**
     * Comparator permettant de trier les morceaux par titre.
     */
    public static class SongNameComparator implements Comparator<Song> {
        @Override
        public int compare(Song s1, Song s2) {
            return s1.getTitre().compareTo(s2.getTitre());
        }
    }

    /**
     * Comparator permettant de trier les morceaux par leur date d'ajout.
     */
    public static class SongDateComparator implements Comparator<Song> {
        @Override
        public int compare(Song s1, Song s2) {
            return s1.getDate().compareTo(s2.getDate());
        }
    }

}
