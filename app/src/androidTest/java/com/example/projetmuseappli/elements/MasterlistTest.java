package com.example.projetmuseappli.elements;

import android.content.Context;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class MasterlistTest {

    private Context context;

    @Before
    public void setUp() {
        context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        context.deleteFile("masterlist.ser");
        Masterlist.init(context);
    }

    @Test
    public void testSingletonInstance() {
        Masterlist instance1 = Masterlist.getInstance();
        Masterlist instance2 = Masterlist.getInstance();
        assertSame("Les deux instances doivent être identiques (Singleton)", instance1, instance2);
    }

    @Test
    public void testAddAndGetSong() {
        Masterlist masterlist = Masterlist.getInstance();
        UUID uuid = UUID.randomUUID();
        Song song = new Song(uuid, "mp3", "Test Song");
        
        masterlist.addSong(song);
        
        Song retrieved = masterlist.getSong(uuid);
        assertNotNull(retrieved);
        assertEquals("Test Song", retrieved.getTitre());
        assertTrue(masterlist.getMasterlistSongList().contains(song));
    }

    @Test
    public void testSaveAndLoad() {
        Masterlist masterlist = Masterlist.getInstance();
        UUID uuid = UUID.randomUUID();
        String title = "Persistent Song";
        masterlist.addSong(new Song(uuid, "flac", title));
        
        masterlist.save();
        
        File file = context.getFileStreamPath("masterlist.ser");
        assertTrue("Le fichier de sauvegarde devrait exister", file.exists());
    }

    @Test
    public void testRemoveSong() {
        Masterlist masterlist = Masterlist.getInstance();
        UUID uuid = UUID.randomUUID();
        Song song = new Song(uuid, "ogg", "To Remove");
        masterlist.addSong(song);

        masterlist.removeSong(uuid);
        assertNull(masterlist.getSong(uuid));

        UUID uuid2 = UUID.randomUUID();
        Song song2 = new Song(uuid2, "mp3", "To Remove 2");
        masterlist.addSong(song2);
        masterlist.removeSong(song2);
        assertNull(masterlist.getSong(uuid2));
    }

    @Test
    public void testAddAndGetPlaylist() {
        Masterlist masterlist = Masterlist.getInstance();
        Song song = new Song(UUID.randomUUID(), "mp3", "Song for Playlist");
        Playlist playlist = new Playlist("Playlist Test", song);

        masterlist.addPlaylist(playlist);

        Playlist retrieved = masterlist.getPlaylist(playlist.getUuid());
        assertNotNull(retrieved);
        assertEquals("Playlist Test", retrieved.getName());
        assertTrue(masterlist.getMasterlistPlaylistList().contains(playlist));
    }

    @Test
    public void testRemovePlaylist() {
        Masterlist masterlist = Masterlist.getInstance();
        Song song = new Song(UUID.randomUUID(), "mp3", "Song");
        Playlist playlist = new Playlist("To Delete", song);

        masterlist.addPlaylist(playlist);
        masterlist.removePlaylist(playlist);

        assertNull(masterlist.getPlaylist(playlist.getUuid()));
    }

    @Test
    public void testAddToPlaylistAndRemoveFromPlaylist() {
        Masterlist masterlist = Masterlist.getInstance();
        Song song1 = new Song(UUID.randomUUID(), "mp3", "Song 1");
        Song song2 = new Song(UUID.randomUUID(), "flac", "Song 2");
        Playlist playlist = new Playlist("Dynamic Playlist", song1);

        masterlist.addPlaylist(playlist);
        masterlist.addToPlaylist(playlist, song2);

        assertEquals(2, playlist.getSize());

        masterlist.removeFromPlaylist(song2, playlist);
        assertEquals(1, playlist.getSize());
    }

    @Test
    public void testAddToPlaylistMultipleSongs() {
        Masterlist masterlist = Masterlist.getInstance();
        Song song1 = new Song(UUID.randomUUID(), "mp3", "Song 1");
        Song song2 = new Song(UUID.randomUUID(), "flac", "Song 2");
        Song song3 = new Song(UUID.randomUUID(), "ogg", "Song 3");

        Playlist playlist = new Playlist("Batch Playlist", song1);
        masterlist.addPlaylist(playlist);

        List<Song> newSongs = new ArrayList<>();
        newSongs.add(song2);
        newSongs.add(song3);

        int added = masterlist.addToPlaylist(playlist, newSongs);
        assertEquals(2, added);
        assertEquals(3, playlist.getSize());
    }

    @Test
    public void testGetRawObject() {
        Masterlist masterlist = Masterlist.getInstance();
        Song song = new Song(UUID.randomUUID(), "mp3", "Raw Song");
        Playlist playlist = new Playlist("Raw Playlist", song);

        masterlist.addSong(song);
        masterlist.addPlaylist(playlist);

        Object retrievedSong = masterlist.getRawObject(song.getUUID());
        assertTrue(retrievedSong instanceof Song);

        Object retrievedPlaylist = masterlist.getRawObject(playlist.getUuid());
        assertTrue(retrievedPlaylist instanceof Playlist);

        assertNull(masterlist.getRawObject(UUID.randomUUID()));
    }

    @Test
    public void testMasterlistListeners() {
        Masterlist masterlist = Masterlist.getInstance();
        final boolean[] notified = {false};

        MasterlistListener listener = new MasterlistListener() {
            @Override
            public void onMasterlistChanged() {
                notified[0] = true;
            }
        };

        masterlist.addListener(listener);
        masterlist.addSong(new Song(UUID.randomUUID(), "mp3", "Notify Song"));

        assertTrue("Le listener doit être notifié lors de l'ajout d'une chanson", notified[0]);

        notified[0] = false;
        masterlist.removeListener(listener);
        masterlist.addSong(new Song(UUID.randomUUID(), "mp3", "No Notify Song"));

        assertFalse("Le listener ne doit plus être notifié après sa suppression", notified[0]);
    }

    @Test
    public void testIsEmpty() {
        Masterlist masterlist = Masterlist.getInstance();
        Song song = new Song(UUID.randomUUID(), "mp3", "Test Song");

        masterlist.addSong(song);
        assertFalse(masterlist.isEmpty());

        masterlist.removeSong(song);
        assertTrue(masterlist.isEmpty());
    }
}
