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
        Masterlist.getInstance().clear();
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

    @Test
    public void testRemoveSongFromAllPlaylists() {
        Masterlist masterlist = Masterlist.getInstance();
        Song song1 = new Song(UUID.randomUUID(), "mp3", "Song To Remove");
        Song song2 = new Song(UUID.randomUUID(), "flac", "Song To Keep");
        Song song3 = new Song(UUID.randomUUID(), "ogg", "Solo Song");

        masterlist.addSong(song1);
        masterlist.addSong(song2);
        masterlist.addSong(song3);

        // Playlist 1 avec song1 et song2 (taille 2)
        Playlist playlist1 = new Playlist("Playlist Multi", song1);
        masterlist.addPlaylist(playlist1);
        masterlist.addToPlaylist(playlist1, song2);

        // Playlist 2 avec uniquement song3 (taille 1)
        Playlist playlist2 = new Playlist("Playlist Solo", song3);
        masterlist.addPlaylist(playlist2);

        // Playlist 3 avec uniquement song1 (taille 1)
        Playlist playlist3 = new Playlist("Playlist Song1 Solo", song1);
        masterlist.addPlaylist(playlist3);

        assertEquals(2, playlist1.getSize());
        assertTrue(playlist1.getSongList().contains(song1));

        // Suppression de song1 de la Masterlist
        masterlist.removeSong(song1);

        // Vérifications
        assertNull(masterlist.getSong(song1.getUUID()));
        assertFalse(playlist1.getSongList().contains(song1));
        assertEquals(1, playlist1.getSize());
        assertTrue(playlist1.getSongList().contains(song2));

        // playlist3 ne contenait que song1 -> elle doit être supprimée car elle deviendrait vide
        assertNull(masterlist.getPlaylist(playlist3.getUuid()));

        // playlist2 ne contenait pas song1 -> inchangée
        assertNotNull(masterlist.getPlaylist(playlist2.getUuid()));
        assertEquals(1, playlist2.getSize());
    }

    @Test
    public void testMoveSongInPlaylist() {
        Masterlist masterlist = Masterlist.getInstance();
        Song song1 = new Song(UUID.randomUUID(), "mp3", "Song 1");
        Song song2 = new Song(UUID.randomUUID(), "flac", "Song 2");
        Song song3 = new Song(UUID.randomUUID(), "ogg", "Song 3");

        Playlist playlist = new Playlist("Reorder Playlist", song1);
        masterlist.addPlaylist(playlist);
        masterlist.addToPlaylist(playlist, song2);
        masterlist.addToPlaylist(playlist, song3);

        assertEquals(song1, playlist.getSongList().get(0));
        assertEquals(song2, playlist.getSongList().get(1));
        assertEquals(song3, playlist.getSongList().get(2));

        // Déplacer song1 de la position 0 à la position 2
        masterlist.moveSongInPlaylist(playlist.getUuid(), 0, 2);

        assertEquals(song2, playlist.getSongList().get(0));
        assertEquals(song3, playlist.getSongList().get(1));
        assertEquals(song1, playlist.getSongList().get(2));
    }

    @Test
    public void testRenameSongInMasterlist() {
        Masterlist masterlist = Masterlist.getInstance();
        Song song = new Song(UUID.randomUUID(), "mp3", "Ancien Titre");
        masterlist.addSong(song);

        song.setTitre("Nouveau Titre");
        masterlist.save();

        Song retrieved = masterlist.getSong(song.getUUID());
        assertNotNull(retrieved);
        assertEquals("Nouveau Titre", retrieved.getTitre());
    }

    @Test
    public void testRenameSongNotifiesListener() {
        Masterlist masterlist = Masterlist.getInstance();
        Song song = new Song(UUID.randomUUID(), "mp3", "Titre Initial");
        masterlist.addSong(song);

        final boolean[] notified = {false};
        MasterlistListener listener = () -> notified[0] = true;
        masterlist.addListener(listener);

        song.setTitre("Titre Modifie");
        masterlist.notifyListeners();

        assertTrue(notified[0]);
        masterlist.removeListener(listener);
    }

    @Test
    public void testContainsSongByUriExactMatch() throws java.io.IOException {
        Masterlist masterlist = Masterlist.getInstance();

        UUID uuid = UUID.randomUUID();
        Song song = new Song(uuid, "mp3", "Unique Song");
        masterlist.addSong(song);

        // Créer le fichier local correspondant au Song dans filesDir
        File localFile = song.getAudioFile(context);
        byte[] content = "Audio stream payload 12345".getBytes();
        try (java.io.FileOutputStream fos = new java.io.FileOutputStream(localFile)) {
            fos.write(content);
        }

        // Créer un fichier source temporaire ayant le même contenu binaire
        File tempFile = new File(context.getCacheDir(), "source_temp.mp3");
        try (java.io.FileOutputStream fos = new java.io.FileOutputStream(tempFile)) {
            fos.write(content);
        }

        android.net.Uri uri = android.net.Uri.fromFile(tempFile);

        assertTrue("Masterlist doit reconnaître le fichier identique via URI", masterlist.containsSongFile(context, uri));
        assertTrue("Masterlist doit reconnaître le fichier via la surcharge sans context", masterlist.containsSongFile(uri));

        localFile.delete();
        tempFile.delete();
    }

    @Test
    public void testContainsSongByUriNotFound() throws java.io.IOException {
        Masterlist masterlist = Masterlist.getInstance();

        UUID uuid = UUID.randomUUID();
        Song song = new Song(uuid, "mp3", "Existing Song");
        masterlist.addSong(song);

        File localFile = song.getAudioFile(context);
        try (java.io.FileOutputStream fos = new java.io.FileOutputStream(localFile)) {
            fos.write("Contenu du morceau existant".getBytes());
        }

        // Fichier externe avec un contenu différent
        File differentTempFile = new File(context.getCacheDir(), "different_temp.mp3");
        try (java.io.FileOutputStream fos = new java.io.FileOutputStream(differentTempFile)) {
            fos.write("Contenu totalement distinct".getBytes());
        }

        android.net.Uri uri = android.net.Uri.fromFile(differentTempFile);

        assertFalse("Masterlist doit retourner false pour un fichier différent", masterlist.containsSongFile(context, uri));

        localFile.delete();
        differentTempFile.delete();
    }

    @Test
    public void testGetSongByUriExactMatch() throws java.io.IOException {
        Masterlist masterlist = Masterlist.getInstance();

        UUID uuid = UUID.randomUUID();
        Song song = new Song(uuid, "mp3", "GetSong URI Match");
        masterlist.addSong(song);

        File localFile = song.getAudioFile(context);
        byte[] content = "Audio unique getSong payload".getBytes();
        try (java.io.FileOutputStream fos = new java.io.FileOutputStream(localFile)) {
            fos.write(content);
        }

        File tempFile = new File(context.getCacheDir(), "source_get_song_temp.mp3");
        try (java.io.FileOutputStream fos = new java.io.FileOutputStream(tempFile)) {
            fos.write(content);
        }

        android.net.Uri uri = android.net.Uri.fromFile(tempFile);

        assertEquals("getSong(context, uri) doit retourner le morceau correspondant", song, masterlist.getSong(context, uri));
        assertEquals("getSong(uri) doit retourner le morceau correspondant", song, masterlist.getSong(uri));

        localFile.delete();
        tempFile.delete();
    }

    @Test
    public void testGetSongByUriNotFound() throws java.io.IOException {
        Masterlist masterlist = Masterlist.getInstance();

        UUID uuid = UUID.randomUUID();
        Song song = new Song(uuid, "mp3", "GetSong Not Found");
        masterlist.addSong(song);

        File localFile = song.getAudioFile(context);
        try (java.io.FileOutputStream fos = new java.io.FileOutputStream(localFile)) {
            fos.write("Contenu audio A".getBytes());
        }

        File differentTempFile = new File(context.getCacheDir(), "source_different_temp.mp3");
        try (java.io.FileOutputStream fos = new java.io.FileOutputStream(differentTempFile)) {
            fos.write("Contenu audio B différent".getBytes());
        }

        android.net.Uri uri = android.net.Uri.fromFile(differentTempFile);

        assertNull("getSong(context, uri) doit retourner null si aucun morceau ne correspond", masterlist.getSong(context, uri));
        assertNull("getSong(uri) doit retourner null si aucun morceau ne correspond", masterlist.getSong(uri));

        localFile.delete();
        differentTempFile.delete();
    }
}
