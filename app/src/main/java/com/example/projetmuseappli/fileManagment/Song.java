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

package com.example.projetmuseappli.fileManagment;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

public class Song implements Serializable {
    private static final long serialVersionUID = 2L;
    /*Identifiant unique du fichier son*/
    private UUID UUID;
    private String Extension;
    private String titre;
    private LocalDate dateImport;

    private String FORMAT_DATE_DEFAUT = "dd/MM/yyyy";


    public static final List<String> EXTENSIONS_VALIDES =
            Collections.unmodifiableList(Arrays.asList("mp3", "flac", "ogg"));


    public Song(UUID uuid, @NonNull String Extension, @NonNull String titre){
        this.UUID = uuid;

        if(EXTENSIONS_VALIDES.contains(Extension)){
            this.Extension = Extension;
        } else {
            String message = "L'extention du fichier : "+titre+"\nUUID : "+uuid+" est invalide. Extentions valide : "+getStringExtensionValides();
            throw new IllegalArgumentException(message);
        }

        if(titre.isEmpty()){
            throw new IllegalArgumentException("le titre ne peut pas etre vide");
            //TODO : vérification des doublons
        }
        this.titre = titre;

        this.dateImport = LocalDate.now();

    }

    public UUID getUUID() {
        return UUID;
    }

    public String getExtension() {
        return Extension;
    }

    public String getTitre() {
        return titre;
    }

    public LocalDate getDateImport() {
        return dateImport;
    }

    /**
     * Renvoie la date d'import sous forme de string formatee
     * @param format format de la date (cf DateTimeFormater), "dd/MM/yyyy" est le format par defaut
     * @return la string de la date formatee
     */
    public String getDateImportString(String format){
        if(format.isEmpty()){
            format=FORMAT_DATE_DEFAUT;
        }
        return this.dateImport.format(DateTimeFormatter.ofPattern(format));
    }

    /**
     * indique si l'extention est valide
     * @param extension String representant l'extention
     * @return boolean : true si l'extention est valide, false sinon
     */
    public static boolean isExtentionValide(String extension){
        return EXTENSIONS_VALIDES.contains(extension.toLowerCase());
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
     * Classe permetant de trier les objets Song par leur nom via Collection.sort()
     */
    public static class SongNameComparator implements Comparator<Song> {
        @Override
        public int compare(Song s1, Song s2) {
            return s1.getTitre().compareTo(s2.getTitre());
        }
    }

    /**
     * Classe permetant de trier les objets Song par leur date d'ajout sur l'application via Collection.sort()
     */
    public static class SongDateComparator implements Comparator<Song> {
        @Override
        public int compare(Song s1, Song s2) {
            return s1.getDateImport().compareTo(s2.getDateImport());
        }
    }















}
