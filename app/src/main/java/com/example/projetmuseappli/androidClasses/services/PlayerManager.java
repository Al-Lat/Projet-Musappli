package com.example.projetmuseappli.androidClasses.services;

import android.content.ComponentName;
import android.content.Context;
import android.net.Uri;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.media3.common.MediaItem;
import androidx.media3.common.MediaMetadata;
import androidx.media3.common.Player;
import androidx.media3.session.MediaController;
import androidx.media3.session.SessionToken;

import com.example.projetmuseappli.androidClasses.utilitaries.PlaybackHistoryManager;
import com.example.projetmuseappli.elements.Masterlist;
import com.example.projetmuseappli.elements.Song;
import com.google.common.util.concurrent.ListenableFuture;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Gestionnaire responsable de la connexion au {@link PlaybackService} et du lancement de la lecture audio avec Jetpack Media3.
 */
public class PlayerManager {

    private static PlayerManager instance;
    private MediaController mediaController;
    private final ListenableFuture<MediaController> controllerFuture;

    private PlayerManager(Context context) {
        Context appContext = context.getApplicationContext();
        SessionToken sessionToken = new SessionToken(appContext, new ComponentName(appContext, PlaybackService.class));
        controllerFuture = new MediaController.Builder(appContext, sessionToken).buildAsync();
        controllerFuture.addListener(() -> {
            try {
                mediaController = controllerFuture.get();
                if (mediaController != null) {
                    mediaController.addListener(new Player.Listener() {
                        @Override
                        public void onMediaItemTransition(MediaItem mediaItem, int reason) {
                            if (mediaItem != null && mediaItem.mediaId != null) {
                                try {
                                    UUID uuid = UUID.fromString(mediaItem.mediaId);
                                    Song song = Masterlist.getInstance().getSong(uuid);
                                    if (song != null) {
                                        PlaybackHistoryManager
                                                .getInstance(appContext)
                                                .addSongToHistory(appContext, song);
                                    }
                                } catch (Exception ignored) {}
                            }
                        }
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, ContextCompat.getMainExecutor(appContext));
    }

    /**
     * Retourne l'instance unique de {@link PlayerManager}.
     *
     * @param context Le contexte d'exécution.
     * @return L'instance unique de PlayerManager.
     */
    public static synchronized PlayerManager getInstance(Context context) {
        if (instance == null) {
            instance = new PlayerManager(context);
        }
        return instance;
    }

    /**
     * Lance la lecture d'un morceau individuel unique (sans file de lecture).
     *
     * @param context Le contexte d'exécution.
     * @param song Le morceau à jouer.
     */
    public void playSong(Context context, Song song) {
        if (song == null) return;

        PlaybackHistoryManager
                .getInstance(context)
                .addSongToHistory(context, song);

        File targetFile = new File(context.getFilesDir(), song.getUUID().toString() + "." + song.getExtension());
        if (!targetFile.exists()) {
            Toast.makeText(context, "Fichier audio introuvable : " + song.getTitre(), Toast.LENGTH_SHORT).show();
            return;
        }

        Uri uri = Uri.fromFile(targetFile);
        MediaItem mediaItem = new MediaItem.Builder()
                .setMediaId(song.getUUID().toString())
                .setUri(uri)
                .setMediaMetadata(new MediaMetadata.Builder()
                        .setTitle(song.getTitre())
                        .build())
                .build();

        Runnable startPlayback = () -> {
            if (mediaController != null) {
                mediaController.setRepeatMode(Player.REPEAT_MODE_OFF);
                mediaController.setMediaItem(mediaItem);
                mediaController.prepare();
                mediaController.play();
                Toast.makeText(context, "Lecture : " + song.getTitre(), Toast.LENGTH_SHORT).show();
            }
        };

        if (mediaController != null) {
            startPlayback.run();
        } else if (controllerFuture != null) {
            controllerFuture.addListener(startPlayback, ContextCompat.getMainExecutor(context));
        }
    }

    /**
     * Lance la lecture d'un morceau à partir d'une liste de morceaux.
     * À la fin d'un morceau, le suivant est joué automatiquement.
     * La lecture s'arrête si le dernier morceau de la liste est atteint.
     *
     * @param context Le contexte d'exécution.
     * @param songList La liste des morceaux de référence (non filtrée).
     * @param songToPlay Le morceau sélectionné par lequel commencer la lecture.
     */
    public void playSongFromList(Context context, List<Song> songList, Song songToPlay) {
        if (songToPlay == null) return;

        PlaybackHistoryManager
                .getInstance(context)
                .addSongToHistory(context, songToPlay);

        File targetFile = new File(context.getFilesDir(), songToPlay.getUUID().toString() + "." + songToPlay.getExtension());
        if (!targetFile.exists()) {
            Toast.makeText(context, "Fichier audio introuvable : " + songToPlay.getTitre(), Toast.LENGTH_SHORT).show();
            return;
        }

        List<MediaItem> mediaItems = new ArrayList<>();
        int startIndex = 0;

        if (songList != null && !songList.isEmpty()) {
            for (Song s : songList) {
                File file = new File(context.getFilesDir(), s.getUUID().toString() + "." + s.getExtension());
                if (file.exists()) {
                    Uri uri = Uri.fromFile(file);
                    MediaItem mediaItem = new MediaItem.Builder()
                            .setMediaId(s.getUUID().toString())
                            .setUri(uri)
                            .setMediaMetadata(new MediaMetadata.Builder()
                                    .setTitle(s.getTitre())
                                    .build())
                            .build();
                    mediaItems.add(mediaItem);
                    if (s.getUUID().equals(songToPlay.getUUID())) {
                        startIndex = mediaItems.size() - 1;
                    }
                }
            }
        } else {
            Uri uri = Uri.fromFile(targetFile);
            MediaItem mediaItem = new MediaItem.Builder()
                    .setMediaId(songToPlay.getUUID().toString())
                    .setUri(uri)
                    .setMediaMetadata(new MediaMetadata.Builder()
                            .setTitle(songToPlay.getTitre())
                            .build())
                    .build();
            mediaItems.add(mediaItem);
        }

        if (mediaItems.isEmpty()) {
            Toast.makeText(context, "Fichier audio introuvable : " + songToPlay.getTitre(), Toast.LENGTH_SHORT).show();
            return;
        }

        final int finalStartIndex = startIndex;
        Runnable startPlayback = () -> {
            if (mediaController != null) {
                mediaController.setRepeatMode(Player.REPEAT_MODE_OFF);
                mediaController.setMediaItems(mediaItems, finalStartIndex, 0L);
                mediaController.prepare();
                mediaController.play();
                Toast.makeText(context, "Lecture : " + songToPlay.getTitre(), Toast.LENGTH_SHORT).show();
            }
        };

        if (mediaController != null) {
            startPlayback.run();
        } else if (controllerFuture != null) {
            controllerFuture.addListener(startPlayback, ContextCompat.getMainExecutor(context));
        }
    }

    /**
     * Alterner la lecture et la pause.
     */
    public void togglePlayPause() {
        if (mediaController != null) {
            if (mediaController.isPlaying()) {
                mediaController.pause();
            } else {
                mediaController.play();
            }
        }
    }

    /**
     * Déplace la position de lecture courante à la position spécifiée en millisecondes.
     *
     * @param positionMs Position en millisecondes.
     */
    public void seekTo(long positionMs) {
        if (mediaController != null) {
            mediaController.seekTo(positionMs);
        }
    }

    /**
     * Indique si un son est en cours de lecture.
     *
     * @return {@code true} si la lecture est en cours, {@code false} sinon.
     */
    public boolean isPlaying() {
        return mediaController != null && mediaController.isPlaying();
    }

    /**
     * Retourne la position courante du morceau en cours en millisecondes.
     *
     * @return La position en millisecondes.
     */
    public long getCurrentPosition() {
        return mediaController != null ? mediaController.getCurrentPosition() : 0L;
    }

    /**
     * Retourne la durée totale du morceau en cours en millisecondes.
     *
     * @return La durée en millisecondes.
     */
    public long getDuration() {
        return mediaController != null && mediaController.getDuration() > 0 ? mediaController.getDuration() : 0L;
    }

    /**
     * Passe au morceau précédent si disponible.
     */
    public void seekToPrevious() {
        if (mediaController != null && mediaController.hasPreviousMediaItem()) {
            mediaController.seekToPreviousMediaItem();
        }
    }

    /**
     * Passe au morceau suivant si disponible.
     */
    public void seekToNext() {
        if (mediaController != null && mediaController.hasNextMediaItem()) {
            mediaController.seekToNextMediaItem();
        }
    }

    /**
     * Indique si un morceau précédent est disponible dans la file de lecture.
     *
     * @return {@code true} si un morceau précédent existe, {@code false} sinon.
     */
    public boolean hasPrevious() {
        return mediaController != null && mediaController.hasPreviousMediaItem();
    }

    /**
     * Indique si un morceau suivant est disponible dans la file de lecture.
     *
     * @return {@code true} si un morceau suivant existe, {@code false} sinon.
     */
    public boolean hasNext() {
        return mediaController != null && mediaController.hasNextMediaItem();
    }

    /**
     * Ajoute un écouteur d'événements {@link Player.Listener} sur le controller.
     *
     * @param context Contexte d'exécution.
     * @param listener L'écouteur d'événements.
     */
    public void addListener(Context context, Player.Listener listener) {
        if (mediaController != null) {
            mediaController.addListener(listener);
        } else if (controllerFuture != null) {
            controllerFuture.addListener(() -> {
                if (mediaController != null) {
                    mediaController.addListener(listener);
                }
            }, ContextCompat.getMainExecutor(context));
        }
    }

    /**
     * Retire un écouteur d'événements {@link Player.Listener}.
     *
     * @param listener L'écouteur à retirer.
     */
    public void removeListener(Player.Listener listener) {
        if (mediaController != null) {
            mediaController.removeListener(listener);
        }
    }

    /**
     * Retourne le MediaController connecté s'il est prêt.
     *
     * @return Le {@link MediaController} ou {@code null}.
     */
    public MediaController getMediaController() {
        return mediaController;
    }
}
