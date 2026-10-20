package com.example.projetmuseappli.androidClasses.utilitaries;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCaller;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;

import com.example.projetmuseappli.R;
import com.example.projetmuseappli.elements.Masterlist;

import java.util.List;

/**
 * Classe utilitaire gérant la sélection et le lancement de l'importation de fichiers audio via le sélecteur de documents Android.
 */
public class ImportHelper {

    private ActivityResultLauncher<String[]> launcher;

    /**
     * Encapsule un sélecteur de documents multiples.
     * Doit être instanciée lors de la création de l'activité hôte.
     *
     * @param caller L'activité ou le composant initiant la demande.
     */
    public ImportHelper(@NonNull ActivityResultCaller caller) {
        this.launcher = caller.registerForActivityResult(
                new ActivityResultContracts.OpenMultipleDocuments(),
                uriList -> {
                    if (uriList != null && !uriList.isEmpty()) {
                        /*passe l'import que si caller est une activity encore en cours*/
                        if (caller instanceof Activity && !((Activity) caller).isFinishing()) {
                            importSong((Context) caller,uriList);
                        }
                    }
                }
        );
    }

    /**
     * Lance le sélecteur d'explorateur de fichiers filtré sur les formats audio supportés.
     */
    public void launch() {
        launcher.launch(Masterlist.ACCEPTED_FORMATS);
    }

    /**
     * Gère l'importation de morceaux à partir d'une liste d'URI.
     *
     * @param context Le contexte Android.
     * @param uriCollection La liste des URI de fichiers audio à importer.
     * @throws RuntimeException si une erreur survient pendant l'importation.
     */
    public static void importSong(@NonNull Context context, List<Uri> uriCollection) throws RuntimeException {

        for (Uri uri : uriCollection) {
            LocalStorageImportManager.importAudioFile(context, uri);
        }

        Toast.makeText(context, context.getString(R.string.import_succesful), Toast.LENGTH_SHORT).show();
    }

}
