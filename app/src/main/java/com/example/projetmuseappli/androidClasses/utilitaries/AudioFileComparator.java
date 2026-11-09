package com.example.projetmuseappli.androidClasses.utilitaries;

import android.content.Context;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Arrays;

/**
 * Classe utilitaire dédiée à la comparaison de fichiers audio.
 * <p>
 * Combine une comparaison au niveau binaire (I/O streaming) et une comparaison
 * acoustique/métadonnées audio via {@link MediaMetadataRetriever} du SDK Android.
 */
public class AudioFileComparator {

    private static final String TAG = "AudioFileComparator";
    private static final int BUFFER_SIZE = 8192;
    private static final long DURATION_TOLERANCE_MS = 250; // Tolérance de 250ms pour variations d'encodage

    private AudioFileComparator() {
        // Constructeur privé pour classe utilitaire
    }

    /**
     * Compare le fichier désigné par une {@link Uri} à un fichier cible {@link File}.
     * <p>
     * La comparaison vérifie d'abord l'égalité binaire stricte byte-à-byte. Si celle-ci est différente
     * (par exemple métadonnées ou conteneurs légèrement modifiés), elle compare les propriétés audio
     * fondamentales (durée, bitrate) extraites via le {@link MediaMetadataRetriever}.
     *
     * @param context    Le contexte Android permettant d'accéder au ContentResolver.
     * @param sourceUri  L'{@link Uri} du fichier audio à comparer.
     * @param targetFile Le {@link File} de comparaison stocké en local.
     * @return {@code true} si les fichiers représentent le même contenu audio, {@code false} sinon.
     */
    public static boolean areEqual(@NonNull Context context, @NonNull Uri sourceUri, @NonNull File targetFile) {
        if (!targetFile.isFile()) {
            return false;
        }

        // 1. Comparaison binaire byte-à-byte
        if (areContentsBinaryEqual(context, sourceUri, targetFile)) {
            return true;
        }

        // 2. Comparaison des caractéristiques audio via MediaMetadataRetriever
        return areAudioPropertiesEqual(context, sourceUri, targetFile);
    }

    /**
     * Effectue une comparaison binaire séquentielle byte-à-byte entre le flux issu de l'URI et le fichier cible.
     */
    public static boolean areContentsBinaryEqual(@NonNull Context context, @NonNull Uri sourceUri, @NonNull File targetFile) {
        try (InputStream sourceStream = new BufferedInputStream(context.getContentResolver().openInputStream(sourceUri));
             InputStream targetStream = new BufferedInputStream(Files.newInputStream(targetFile.toPath()))) {

            byte[] sourceBuffer = new byte[BUFFER_SIZE];
            byte[] targetBuffer = new byte[BUFFER_SIZE];

            while (true) {
                int sourceRead = readFully(sourceStream, sourceBuffer);
                if (sourceRead <= 0) {
                    break;
                }
                int targetRead = readFully(targetStream, targetBuffer);
                if (sourceRead != targetRead) {
                    return false;
                }
                if (!Arrays.equals(Arrays.copyOf(sourceBuffer, sourceRead), Arrays.copyOf(targetBuffer, targetRead))) {
                    return false;
                }
            }

            // S'assurer que le fichier cible n'a plus d'octets restants
            return targetStream.read() == -1;

        } catch (Exception e) {
            Log.d(TAG, "Erreur lors de la comparaison binaire: " + e.getMessage());
            return false;
        }
    }

    /**
     * Compare les propriétés audio (durée, bitrate) via le MediaMetadataRetriever Android.
     */
    public static boolean areAudioPropertiesEqual(@NonNull Context context, @NonNull Uri sourceUri, @NonNull File targetFile) {
        try (MediaMetadataRetriever mmrSource = new MediaMetadataRetriever();
             MediaMetadataRetriever mmrTarget = new MediaMetadataRetriever()) {

            mmrSource.setDataSource(context, sourceUri);
            mmrTarget.setDataSource(targetFile.getAbsolutePath());

            String sourceDurationStr = mmrSource.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
            String targetDurationStr = mmrTarget.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);

            if (sourceDurationStr == null || targetDurationStr == null) {
                return false;
            }

            long sourceDuration = Long.parseLong(sourceDurationStr);
            long targetDuration = Long.parseLong(targetDurationStr);

            if (Math.abs(sourceDuration - targetDuration) > DURATION_TOLERANCE_MS) {
                return false;
            }

            String sourceBitrate = mmrSource.extractMetadata(MediaMetadataRetriever.METADATA_KEY_BITRATE);
            String targetBitrate = mmrTarget.extractMetadata(MediaMetadataRetriever.METADATA_KEY_BITRATE);
            if (sourceBitrate != null && targetBitrate != null) {
                long srcBitrateVal = Long.parseLong(sourceBitrate);
                long tgtBitrateVal = Long.parseLong(targetBitrate);
                return srcBitrateVal == tgtBitrateVal;
            }

            return true;

        } catch (Exception e) {
            Log.d(TAG, "Erreur lors de l'extraction des métadonnées audio: " + e.getMessage());
            return false;
        }
    }

    private static int readFully(InputStream in, byte[] buffer) throws Exception {
        int totalRead = 0;
        int read;
        while (totalRead < buffer.length && (read = in.read(buffer, totalRead, buffer.length - totalRead)) != -1) {
            totalRead += read;
        }
        return totalRead;
    }
}
