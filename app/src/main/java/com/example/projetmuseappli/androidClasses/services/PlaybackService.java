package com.example.projetmuseappli.androidClasses.services;

import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.OptIn;
import androidx.media3.common.Player;
import androidx.media3.common.util.UnstableApi;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.session.CommandButton;
import androidx.media3.session.MediaSession;
import androidx.media3.session.MediaSessionService;
import androidx.media3.session.SessionCommand;
import androidx.media3.session.SessionCommands;
import androidx.media3.session.SessionResult;

import com.example.projetmuseappli.androidClasses.activities.MainActivity;
import com.google.common.collect.ImmutableList;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

/**
 * Service gérant la lecture audio en arrière-plan avec Jetpack Media3.
 */
public class PlaybackService extends MediaSessionService {

    public static final String ACTION_STOP_APP = "com.example.projetmuseappli.ACTION_STOP_APP";
    public static final String CUSTOM_COMMAND_STOP_APP = "ACTION_STOP_APP_AND_SERVICE";

    private MediaSession mediaSession;

    @OptIn(markerClass = UnstableApi.class)
    @Override
    public void onCreate() {
        super.onCreate();
        ExoPlayer player = new ExoPlayer.Builder(this).build();

        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                this,
                0,
                intent,
                PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT
        );

        SessionCommand stopAppCommand = new SessionCommand(CUSTOM_COMMAND_STOP_APP, Bundle.EMPTY);
        CommandButton stopAppButton = new CommandButton.Builder()
                .setDisplayName("Arrêt application")
                .setIconResId(android.R.drawable.ic_menu_close_clear_cancel)
                .setSessionCommand(stopAppCommand)
                .build();

        MediaSession.Callback callback = new MediaSession.Callback() {
            @NonNull
            @Override
            public ListenableFuture<SessionResult> onCustomCommand(
                    @NonNull MediaSession session,
                    @NonNull MediaSession.ControllerInfo controller,
                    @NonNull SessionCommand customCommand,
                    @NonNull Bundle args) {
                if (CUSTOM_COMMAND_STOP_APP.equals(customCommand.customAction)) {
                    stopPlaybackAndService();
                    return Futures.immediateFuture(new SessionResult(SessionResult.RESULT_SUCCESS));
                }
                return MediaSession.Callback.super.onCustomCommand(session, controller, customCommand, args);
            }

            @OptIn(markerClass = UnstableApi.class)
            @NonNull
            @Override
            public MediaSession.ConnectionResult onConnect(
                    @NonNull MediaSession session,
                    @NonNull MediaSession.ControllerInfo controller) {
                MediaSession.ConnectionResult defaultResult = MediaSession.Callback.super.onConnect(session, controller);
                SessionCommands sessionCommands = defaultResult.availableSessionCommands.buildUpon()
                        .add(stopAppCommand)
                        .build();
                return new MediaSession.ConnectionResult.AcceptedResultBuilder(session)
                        .setAvailablePlayerCommands(defaultResult.availablePlayerCommands)
                        .setAvailableSessionCommands(sessionCommands)
                        .setCustomLayout(ImmutableList.of(stopAppButton))
                        .build();
            }
        };

        mediaSession = new MediaSession.Builder(this, player)
                .setId("PlaybackServiceSession_" + java.util.UUID.randomUUID())
                .setSessionActivity(pendingIntent)
                .setCallback(callback)
                .setCustomLayout(ImmutableList.of(stopAppButton))
                .build();
    }

    @Nullable
    @Override
    public MediaSession onGetSession(@NonNull MediaSession.ControllerInfo controllerInfo) {
        return mediaSession;
    }

    @Override
    public void onTaskRemoved(@Nullable Intent rootIntent) {
        if (mediaSession != null) {
            Player player = mediaSession.getPlayer();
            if (player.isPlaying()) {
                player.pause();
            }
        }
        super.onTaskRemoved(rootIntent);
    }

    /**
     * Stoppe la lecture audio, vide les media items et arrête le service.
     */
    public void stopPlaybackAndService() {
        if (mediaSession != null) {
            Player player = mediaSession.getPlayer();
            player.stop();
            player.clearMediaItems();
        }
        stopSelf();
    }

    @Override
    public void onDestroy() {
        Intent intent = new Intent(ACTION_STOP_APP);
        intent.setPackage(getPackageName());
        sendBroadcast(intent);

        if (mediaSession != null) {
            mediaSession.getPlayer().release();
            mediaSession.release();
            mediaSession = null;
        }
        super.onDestroy();
    }
}
