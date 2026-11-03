package com.example.projetmuseappli.androidClasses.utilitaries;

import static org.junit.Assert.assertEquals;

import com.example.projetmuseappli.elements.Song;

import org.junit.Test;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.robolectric.RobolectricTestRunner;
import org.junit.runner.RunWith;

import java.util.List;
import java.util.UUID;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 33)
public class PlaybackHistoryManagerTest {

    @Test
    public void testHistoryLimitAndOrdering() {
        PlaybackHistoryManager manager = PlaybackHistoryManager.getInstance(RuntimeEnvironment.getApplication());
        manager.clearHistory(RuntimeEnvironment.getApplication());

        Song firstSong = new Song(UUID.randomUUID(), "mp3", "First Song");
        manager.addSongToHistory(RuntimeEnvironment.getApplication(), firstSong);

        // Add 55 songs
        for (int i = 0; i < 55; i++) {
            Song song = new Song(UUID.randomUUID(), "mp3", "Song " + i);
            manager.addSongToHistory(RuntimeEnvironment.getApplication(), song);
        }

        List<Song> history = manager.getHistoryList();
        assertEquals(50, history.size());
        assertEquals("Song 54", history.get(0).getTitre());
    }

    @Test
    public void testReplayingSongMovesToTop() {
        PlaybackHistoryManager manager = PlaybackHistoryManager.getInstance(RuntimeEnvironment.getApplication());
        manager.clearHistory(RuntimeEnvironment.getApplication());

        Song song1 = new Song(UUID.randomUUID(), "mp3", "Song 1");
        Song song2 = new Song(UUID.randomUUID(), "mp3", "Song 2");

        manager.addSongToHistory(RuntimeEnvironment.getApplication(), song1);
        manager.addSongToHistory(RuntimeEnvironment.getApplication(), song2);
        assertEquals("Song 2", manager.getHistoryList().get(0).getTitre());

        // Replay song1
        manager.addSongToHistory(RuntimeEnvironment.getApplication(), song1);
        assertEquals("Song 1", manager.getHistoryList().get(0).getTitre());
        assertEquals(2, manager.getHistoryList().size());
    }
}
