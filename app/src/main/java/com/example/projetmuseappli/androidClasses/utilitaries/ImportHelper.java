package com.example.projetmuseappli.androidClasses.utilitaries;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCaller;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.projetmuseappli.R;
import com.example.projetmuseappli.elements.Masterlist;

import java.util.List;

/**
 * Classe utilitaire gérant la sélection et le lancement de l'importation de fichiers audio via le sélecteur de documents Android.
 */
public class ImportHelper {

    private final ActivityResultLauncher<String[]> launcher;

    /**
     * Encapsule un sélecteur de documents multiples.
     * Doit être instanciée lors de la création de l'activité ou du fragment hôte.
     *
     * @param caller L'activité ou le fragment initiant la demande.
     */
    public ImportHelper(@NonNull ActivityResultCaller caller) {
        this.launcher = caller.registerForActivityResult(
                new ActivityResultContracts.OpenMultipleDocuments(),
                uriList -> {
                    if (uriList != null && !uriList.isEmpty()) {
                        Context context = getContextFromCaller(caller);
                        if (context != null) {
                            importSong(context, uriList);
                        }
                    }
                }
        );
    }

    /**
     * Récupère le contexte valide à partir du caller (Activity ou Fragment) si celui-ci est encore actif.
     *
     * @param caller Le composant ayant initié le sélecteur.
     * @return Le contexte Android, ou null si le composant est terminé/détaché.
     */
    @Nullable
    public static Context getContextFromCaller(@NonNull ActivityResultCaller caller) {
        if (caller instanceof Activity) {
            Activity activity = (Activity) caller;
            if (!activity.isFinishing()) {
                return activity;
            }
        } else if (caller instanceof Fragment) {
            Fragment fragment = (Fragment) caller;
            if (fragment.isAdded() && fragment.getActivity() != null && !fragment.getActivity().isFinishing()) {
                return fragment.getContext();
            }
        }
        return null;
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
