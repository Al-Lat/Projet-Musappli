package com.example.projetmuseappli.elements;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PlaylistTest {

    private Song song1;
    private Song song2;
    private Song song3;

    @Before
    public void setUp() {
        song1 = new Song(UUID.randomUUID(), "mp3", "Song Alpha");
        song2 = new Song(UUID.randomUUID(), "flac", "Song Beta");
        song3 = new Song(UUID.randomUUID(), "ogg", "Song Gamma");
    }

    @Test
    public void testPlaylistCreationValid() {
        Playlist playlist = new Playlist("Ma Playlist", song1);

        assertNotNull(playlist.getUuid());
        assertEquals("Ma Playlist", playlist.getName());
        assertEquals(1, playlist.getSize());
        assertEquals(song1, playlist.getSongList().get(0));
        assertEquals(LocalDate.now(), playlist.getDate());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPlaylistCreationEmptyName() {
        new Playlist("", song1);
    }

    @Test(expected = NullPointerException.class)
    public void testPlaylistCreationNullName() {
        new Playlist(null, song1);
    }

    @Test
    public void testPlaylistCreationWithList() {
        List<Song> songList = new ArrayList<>();
        songList.add(song1);
        songList.add(song2);

        Playlist playlist = new Playlist("Rock", songList);

        assertEquals(2, playlist.getSize());
        assertEquals(song1, playlist.getSongList().get(0));
        assertEquals(song2, playlist.getSongList().get(1));
    }

    @Test
    public void testAddSongSuccess() {
        Playlist playlist = new Playlist("Mix", song1);
        playlist.addSong(song2);

        assertEquals(2, playlist.getSize());
        assertTrue(playlist.getSongList().contains(song2));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddSongDuplicate() {
        Playlist playlist = new Playlist("Mix", song1);
        playlist.addSong(song1); // Devrait lever une exception
    }

    @Test
    public void testRemoveSongSuccess() {
        Playlist playlist = new Playlist("Mix", song1);
        playlist.addSong(song2);

        playlist.removeSong(song1);

        assertEquals(1, playlist.getSize());
        assertFalse(playlist.getSongList().contains(song1));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRemoveSongNotFound() {
        Playlist playlist = new Playlist("Mix", song1);
        playlist.removeSong(song2);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRemoveSongLastElement() {
        Playlist playlist = new Playlist("Mix", song1);
        // La playlist ne peut pas être vidée complètement
        playlist.removeSong(song1);
    }

    @Test
    public void testGetDateString() {
        Playlist playlist = new Playlist("Date Test", song1);
        String expectedDate = LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        assertEquals(expectedDate, playlist.getDateString());
        assertEquals(expectedDate, playlist.getDateString(""));

        String customFormat = "yyyy-MM-dd";
        String expectedCustomDate = LocalDate.now().format(DateTimeFormatter.ofPattern(customFormat));
        assertEquals(expectedCustomDate, playlist.getDateString(customFormat));
    }

    @Test
    public void testSetName() {
        Playlist playlist = new Playlist("Ancien Nom", song1);
        playlist.setName("Nouveau Nom");
        assertEquals("Nouveau Nom", playlist.getName());
    }

    @Test
    public void testDuplicatePlaylistNameAllowedCurrentBehavior() {
        // Signalement : Deux playlists avec le même nom sont autorisées dans le code actuel (différenciées par UUID)
        Playlist p1 = new Playlist("Favoris", song1);
        Playlist p2 = new Playlist("Favoris", song2);

        assertNotEquals(p1.getUuid(), p2.getUuid());
        assertEquals(p1.getName(), p2.getName());
    }

    @Test
    public void testPlaylistRenameDuplicateName() {
        Playlist p1 = new Playlist("Playlist A", song1);
        Playlist p2 = new Playlist("Playlist B", song2);

        p2.setName("Playlist A");
        assertEquals(p1.getName(), p2.getName());
    }

    @Test
    public void testPlaylistDefaultOrderIsInsertionOrder() {
        List<Song> songs = new ArrayList<>();
        songs.add(song3);
        songs.add(song1);
        songs.add(song2);

        Playlist playlist = new Playlist("Ordre d'ajout", songs);

        // L'ordre dans getSongList() doit être l'ordre d'insertion
        assertEquals(song3, playlist.getSongList().get(0));
        assertEquals(song1, playlist.getSongList().get(1));
        assertEquals(song2, playlist.getSongList().get(2));
    }

    @Test
    public void testPlaylistSortByNameUsingSongComparator() {
        List<Song> songs = new ArrayList<>();
        songs.add(song3); // Gamma
        songs.add(song1); // Alpha
        songs.add(song2); // Beta

        Playlist playlist = new Playlist("A trier", songs);

        // Copie et tri avec SongNameComparator
        List<Song> sortedList = new ArrayList<>(playlist.getSongList());
        sortedList.sort(new Song.SongNameComparator());

        assertEquals("Song Alpha", sortedList.get(0).getTitre());
        assertEquals("Song Beta", sortedList.get(1).getTitre());
        assertEquals("Song Gamma", sortedList.get(2).getTitre());
    }
}
