package com.example.projetmuseappli.androidClasses.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import android.content.Context;

import androidx.media3.common.MediaItem;
import androidx.media3.common.MediaMetadata;
import androidx.media3.common.Player;
import androidx.media3.session.MediaController;

import com.example.projetmuseappli.elements.Song;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.util.Collections;
import java.util.UUID;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 33)
public class PlayerManagerTest {

    private PlayerManager playerManager;
    private MediaController mockController;
    private Context context;

    @Before
    public void setUp() {
        context = RuntimeEnvironment.getApplication();
        mockController = mock(MediaController.class);
        playerManager = new PlayerManager(mockController);
        PlayerManager.setInstance(playerManager);
    }

    @After
    public void tearDown() {
        playerManager.setMediaController(null);
        PlayerManager.setInstance(null);
    }

    @Test
    public void testIsPlayingSongNullOrNoController() {
        playerManager.setMediaController(null);
        Song song = new Song(UUID.randomUUID(), "mp3", "Song");
        assertFalse(playerManager.isPlayingSong(song));

        playerManager.setMediaController(mockController);
        assertFalse(playerManager.isPlayingSong(null));
    }

    @Test
    public void testIsPlayingSongWhenNotPlayingReturnsFalse() {
        Song song = new Song(UUID.randomUUID(), "mp3", "Song");
        when(mockController.isPlaying()).thenReturn(false);

        assertFalse(playerManager.isPlayingSong(song));
    }

    @Test
    public void testIsPlayingSongWhenDifferentSongPlayingReturnsFalse() {
        Song songA = new Song(UUID.randomUUID(), "mp3", "Song A");
        Song songB = new Song(UUID.randomUUID(), "mp3", "Song B");

        when(mockController.isPlaying()).thenReturn(true);
        MediaItem currentItem = new MediaItem.Builder()
                .setMediaId(songB.getUUID().toString())
                .build();
        when(mockController.getCurrentMediaItem()).thenReturn(currentItem);

        assertFalse(playerManager.isPlayingSong(songA));
    }

    @Test
    public void testIsPlayingSongWhenSameSongPlayingReturnsTrue() {
        Song song = new Song(UUID.randomUUID(), "mp3", "Song Playing");

        when(mockController.isPlaying()).thenReturn(true);
        MediaItem currentItem = new MediaItem.Builder()
                .setMediaId(song.getUUID().toString())
                .build();
        when(mockController.getCurrentMediaItem()).thenReturn(currentItem);

        assertTrue(playerManager.isPlayingSong(song));
    }

    @Test
    public void testPlaySongDoesNotRelaunchWhenAlreadyPlaying() {
        Song playingSong = new Song(UUID.randomUUID(), "mp3", "Playing Song");

        when(mockController.isPlaying()).thenReturn(true);
        MediaItem currentItem = new MediaItem.Builder()
                .setMediaId(playingSong.getUUID().toString())
                .build();
        when(mockController.getCurrentMediaItem()).thenReturn(currentItem);

        playerManager.playSong(context, playingSong);

        // Vérifie qu'aucun chargement de média ni lecture n'a été déclenché
        verify(mockController, never()).setMediaItem(any());
        verify(mockController, never()).play();
    }

    @Test
    public void testPlaySongFromListDoesNotRelaunchWhenAlreadyPlaying() {
        Song playingSong = new Song(UUID.randomUUID(), "mp3", "Playing Song");

        when(mockController.isPlaying()).thenReturn(true);
        MediaItem currentItem = new MediaItem.Builder()
                .setMediaId(playingSong.getUUID().toString())
                .build();
        when(mockController.getCurrentMediaItem()).thenReturn(currentItem);

        playerManager.playSongFromList(context, Collections.singletonList(playingSong), playingSong);

        // Vérifie qu'aucun chargement de liste ni lecture n'a été déclenché
        verify(mockController, never()).setMediaItems(any(), anyInt(), anyLong());
        verify(mockController, never()).play();
    }

    @Test
    public void testUpdateCurrentMediaItemTitleUpdatesMatchingItem() {
        Song song = new Song(UUID.randomUUID(), "mp3", "Ancien Nom");

        MediaItem originalItem = new MediaItem.Builder()
                .setMediaId(song.getUUID().toString())
                .setMediaMetadata(new MediaMetadata.Builder().setTitle("Ancien Nom").build())
                .build();

        when(mockController.getMediaItemCount()).thenReturn(1);
        when(mockController.getMediaItemAt(0)).thenReturn(originalItem);

        // Renommage du morceau
        song.setTitre("Nouveau Nom");
        playerManager.updateCurrentMediaItemTitle(song);

        // Vérifie que replaceMediaItem a bien été appelé avec le nouveau titre
        ArgumentCaptor<MediaItem> captor = ArgumentCaptor.forClass(MediaItem.class);
        verify(mockController).replaceMediaItem(eq(0), captor.capture());

        MediaItem updated = captor.getValue();
        assertNotNull(updated.mediaMetadata.title);
        assertEquals("Nouveau Nom", updated.mediaMetadata.title.toString());
    }

    @Test
    public void testReplayDisabledByDefault() {
        assertFalse(playerManager.isReplayEnabled());
    }

    @Test
    public void testSetReplayEnabledCallsSetRepeatMode() {
        playerManager.setReplayEnabled(true);
        assertTrue(playerManager.isReplayEnabled());
        verify(mockController).setRepeatMode(Player.REPEAT_MODE_ONE);

        playerManager.setReplayEnabled(false);
        assertFalse(playerManager.isReplayEnabled());
        verify(mockController).setRepeatMode(Player.REPEAT_MODE_OFF);
    }

    @Test
    public void testToggleReplay() {
        assertFalse(playerManager.isReplayEnabled());

        playerManager.toggleReplay();
        assertTrue(playerManager.isReplayEnabled());
        verify(mockController).setRepeatMode(Player.REPEAT_MODE_ONE);

        playerManager.toggleReplay();
        assertFalse(playerManager.isReplayEnabled());
        verify(mockController).setRepeatMode(Player.REPEAT_MODE_OFF);
    }

    @Test
    public void testOnMediaItemTransitionRepeatDoesNotAddToHistory() {
        Player.Listener listener = playerManager.createPlayerListener(context);
        Song song = new Song(UUID.randomUUID(), "mp3", "Test Song");
        com.example.projetmuseappli.elements.Masterlist.getInstance().addSong(song);

        int historyBefore = com.example.projetmuseappli.androidClasses.utilitaries.PlaybackHistoryManager.getInstance(context).getHistoryList().size();

        MediaItem item = new MediaItem.Builder()
                .setMediaId(song.getUUID().toString())
                .build();

        listener.onMediaItemTransition(item, Player.MEDIA_ITEM_TRANSITION_REASON_REPEAT);

        int historyAfter = com.example.projetmuseappli.androidClasses.utilitaries.PlaybackHistoryManager.getInstance(context).getHistoryList().size();
        assertEquals(historyBefore, historyAfter);
    }

    @Test
    public void testOnRepeatModeChangedUpdatesReplayState() {
        Player.Listener listener = playerManager.createPlayerListener(context);

        listener.onRepeatModeChanged(Player.REPEAT_MODE_ONE);
        assertTrue(playerManager.isReplayEnabled());

        listener.onRepeatModeChanged(Player.REPEAT_MODE_OFF);
        assertFalse(playerManager.isReplayEnabled());
    }
}
