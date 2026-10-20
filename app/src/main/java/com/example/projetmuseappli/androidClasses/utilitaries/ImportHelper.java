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

public class ImportHelper {


    private ActivityResultLauncher<String[]> launcher;


    /**
     * Class encapsulant un filepicker de plusieurs documents.
     * Cette classe doit etre instancie a la creation d'une classe activite
     * @param caller classe Activity instanciant cette classe
     */
    public ImportHelper(@NonNull ActivityResultCaller caller) {
        this.launcher = caller.registerForActivityResult(
                new ActivityResultContracts.OpenMultipleDocuments(),
                uriList -> {
                    if (uriList != null && !uriList.isEmpty()) {
                        if (caller instanceof Activity && !((Activity) caller).isFinishing()) { //TODO comprendre ce if
                            importSong((Context) caller,uriList);
                        }
                    }
                }
        );
    }

    /**
     * Appel le lanceur pour l'ouverture de l'explorateur de fichiers.
     */
    public void launch() {
        launcher.launch(Masterlist.ACCEPTED_FORMATS);
    }


    /**
     * Gère l'importation d'un son à partir de son URI.
     * Extrait le nom du fichier pour définir le titre et vérifie l'extension.
     */
    public static void importSong(@NonNull Context context, List<Uri> uriCollection) throws RuntimeException {

        for (Uri uri : uriCollection) {
            // Appel a la methode d'import
            LocalStorageImportManager.importAudioFile(context, uri);
        }

        Toast.makeText(context, context.getString(R.string.import_succesful), Toast.LENGTH_SHORT).show();
    }



}
