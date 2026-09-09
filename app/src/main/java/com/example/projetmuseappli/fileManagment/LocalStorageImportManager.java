/*
 * Classe chargé de l'import d'un fichier audio dans l'application
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
import android.net.Uri;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

public class LocalStorageImportManager {
    public static void importAudioFile(Context context,Uri uriFichier) {

        //Récupérer le nom du fichier
        String nomFichier = Utilitaries.getFileNameFromUri(context, uriFichier);
        String nomSon = nomFichier.substring(0, nomFichier.lastIndexOf('.'));

        //Récupérer l'extension du fichier
        String extension = nomFichier.substring(nomFichier.lastIndexOf('.') + 1);

        if(!Song.isExtentionValide(extension)){
            throw new IllegalArgumentException("L'extension du fichier est invalide");
        }

        //Copier le fichier dans le stockage externe privé de l'application avec un UUID comme nom
        UUID uuid = UUID.randomUUID();
        try {
            copyFileToLocalStorage(context, uriFichier, uuid.toString(), extension);
        } catch (IOException | SecurityException e) {
            throw new RuntimeException(e+" Erreur lors de la copie du fichier");
        } catch (Exception e){
            throw new RuntimeException(e+" Erreur inconnu lors de la copie du fichier");
        }

        //Créer l'objet Song liée au fichier
        Song song = new Song(uuid, extension, nomSon);

        //Ajouter l'objet Song à la Masterlist
        Masterlist.getInstance().addSong(song);
    }

    private static void copyFileToLocalStorage(Context context, Uri uriFichier , String nomFichier, String extension) throws IOException, SecurityException {
        String nomComplet = nomFichier+"."+extension;

        //Ouvrir le flux de lecture à partir de l'Uri source
        InputStream inputStream = context.getContentResolver().openInputStream(uriFichier);

        //Créer le fichier de destination dans le dossier privé de l'app
        File targetFile = new File(context.getFilesDir(), nomComplet);

        //Copier les données du flux dans le fichier de destination
        Files.copy(inputStream,targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

        //Fermeture des flux
        if (inputStream != null) {
            inputStream.close();
        }
    }



}
