/*
 * Classe regroupant des méthodes utiles
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
package com.example.projetmuseappli.androidClasses.utilitaries;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.OpenableColumns;

public class Utilitaries {

    public static String getFileNameFromUri(Context context, Uri uri) {
        String result = null;

        // Si l'URI commence par "content://", c'est le cas général sur Android moderne
        if ("content".equals(uri.getScheme())) {
            // On demande au système de nous retourner une "ligne" de données contenant le nom
            try (Cursor cursor = context.getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    // On récupère l'index de la colonne qui contient le nom affichable
                    int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    if (nameIndex != -1) {
                        result = cursor.getString(nameIndex);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // Cas de secours (Fallback) : Si l'URI est un ancien schéma "file://" (rare aujourd'hui)
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }

        return result;
    }



}
