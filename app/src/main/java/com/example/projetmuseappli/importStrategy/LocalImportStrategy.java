package com.example.projetmuseappli.importStrategy;

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
import com.example.projetmuseappli.androidClasses.utilitaries.Utilitaries;
import com.example.projetmuseappli.elements.Masterlist;
import com.example.projetmuseappli.elements.Song;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

/**
 * Stratégie concrète d'importation de morceaux depuis le stockage local de l'appareil.
 * <p>
 * Implémente {@link ImportStrategyInterface} en déléguant la sélection de fichiers audio
 * au sélecteur de documents système Android via {@link ActivityResultContracts.OpenMultipleDocuments},
 * puis en copiant les fichiers choisis dans le stockage interne privé de l'application
 * avant de les enregistrer dans la {@link Masterlist}.
 */
public class LocalImportStrategy implements ImportStrategyInterface {

    private final ActivityResultLauncher<String[]> launcher;
    private final ActivityResultCaller caller;

    /**
     * Initialise la stratégie d'importation locale et enregistre le sélecteur de documents auprès du composant hôte.
     * <p>
     * Doit impérativement être instanciée lors de l'initialisation du composant hôte (avant son état démarré).
     *
     * @param caller L'activité ou le fragment hôte initiant la demande d'importation.
     */
    public LocalImportStrategy(@NonNull ActivityResultCaller caller) {
        this.caller = caller;
        this.launcher = caller.registerForActivityResult(
                new ActivityResultContracts.OpenMultipleDocuments(),
                this::onDocumentsSelected
        );
    }

    /**
     * Déclenche le processus d'importation locale en ouvrant l'explorateur de fichiers Android,
     * filtré selon les formats audio pris en charge par l'application ({@link Masterlist#ACCEPTED_FORMATS}).
     */
    @Override
    public void importSong() {
        try {
            launcher.launch(Masterlist.ACCEPTED_FORMATS);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Traite les fichiers sélectionnés par l'utilisateur via le sélecteur de documents.
     *
     * @param uriList Liste des {@link Uri} des fichiers audio sélectionnés par l'utilisateur.
     */
    private void onDocumentsSelected(@Nullable List<Uri> uriList) {
        Context context = getContextFromCaller(caller);
        if (uriList != null && !uriList.isEmpty() && context != null) {
            for (Uri uri : uriList) {
                importAudioFile(context, uri);
            }

            Toast.makeText(context, context.getString(R.string.import_succesful), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Importe un fichier audio individuel à partir de son {@link Uri}, le copie dans le stockage
     * interne privé de l'application sous un identifiant unique (UUID) et l'ajoute à la {@link Masterlist}.
     *
     * @param context    Le contexte d'exécution de l'application.
     * @param fileUri L'{@link Uri} du fichier audio source à importer.
     * @throws IllegalArgumentException Si l'extension du fichier n'est pas supportée.
     * @throws RuntimeException         En cas d'erreur lors de la copie ou de l'accès au fichier.
     */
    private void importAudioFile(@NonNull Context context, @NonNull Uri fileUri)
            throws IllegalArgumentException,RuntimeException {
        // Récupérer le nom du fichier
        String fileName = Utilitaries.getFileNameFromUri(context, fileUri);
        String songName = fileName.substring(0, fileName.lastIndexOf('.'));

        //Verification de l'extention
        String extension = fileName.substring(fileName.lastIndexOf('.') + 1);
        if (!Song.isExtentionValide(extension)) {
            throw new IllegalArgumentException("L'extension du fichier est invalide");
        }


        if(Masterlist.getInstance().containsSongFile(fileUri)){
            Toast.makeText(context,
                    String.format(
                            context.getString(R.string.ERROR_MESSAGE_SONG_FILE_ALREADY_EXISTS_FORMAT)
                            ,Masterlist.getInstance().getSong(fileUri).getTitre()
                            ,songName
                    ),
                    Toast.LENGTH_LONG).show();
        } else {

            /*Doublon 'surfacique' tout de meme importe*/
            if(Masterlist.getInstance().containsSongName(songName)){
                songName = "(1) - " + songName; //TODO rendre plus robuste via une incrémentation du chiffre
            }

            // Copier le fichier dans le stockage interne privé de l'application avec un UUID comme nom
            UUID uuid = UUID.randomUUID();
            try {
                copyFileToLocalStorage(context, fileUri, uuid.toString(), extension);
            } catch (IOException | SecurityException e) {
                throw new RuntimeException(e + " Erreur lors de la copie du fichier");
            } catch (Exception e) {
                throw new RuntimeException(e + " Erreur inconnue lors de la copie du fichier");
            }

            // Créer l'objet Song lié au fichier
            Song song = new Song(uuid, extension, songName);

            // Ajouter l'objet Song à la Masterlist
            Masterlist.getInstance().addSong(song);

        }

    }

    /**
     * Copie physiquement le contenu d'un fichier audio depuis son flux source vers l'espace de stockage
     * interne privé de l'application ({@link Context#getFilesDir()}).
     *
     * @param context    Le contexte d'exécution pour accéder au ContentResolver et au répertoire privé.
     * @param uriFichier L'{@link Uri} du fichier source.
     * @param nomFichier Le nom unique de destination attribué au fichier (UUID).
     * @param extension  L'extension du format audio.
     * @throws IOException       En cas d'échec d'ouverture du flux ou de copie du fichier.
     * @throws SecurityException En cas de refus de permission d'accès à l'URI.
     */
    private void copyFileToLocalStorage(@NonNull Context context, @NonNull Uri uriFichier, @NonNull String nomFichier, @NonNull String extension) throws IOException, SecurityException {
        String nomComplet = nomFichier + "." + extension;

        // Créer le fichier de destination dans le dossier privé de l'app
        File targetFile = new File(context.getFilesDir(), nomComplet);

        try (InputStream inputStream = context.getContentResolver().openInputStream(uriFichier)) {
            if (inputStream == null) {
                throw new IOException("Impossible d'ouvrir le flux pour l'URI : " + uriFichier);
            }
            // Copier les données du flux dans le fichier de destination
            Files.copy(inputStream, targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        }
    }

    /**
     * Récupère un {@link Context} valide et actif à partir de l'hôte (Activity ou Fragment).
     *
     * @param caller Le composant hôte ayant initié la stratégie.
     * @return Le contexte Android actif, ou {@code null} si l'activité ou le fragment est terminé/détaché.
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


}
