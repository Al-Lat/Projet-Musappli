package com.example.projetmuseappli.elements;

import static org.junit.Assert.*;
import org.junit.Test;

import java.time.format.DateTimeFormatter;
import java.util.UUID;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class SongTest {

    @Test
    public void testSongCreationValid() {
        UUID uuid = UUID.randomUUID();
        String title = "Test Song";
        String extension = "mp3";
        
        Song song = new Song(uuid, extension, title);
        
        assertEquals(uuid, song.getUUID());
        assertEquals(extension, song.getExtension());
        assertEquals(title, song.getTitre());
        assertEquals(LocalDate.now(), song.getDate());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSongCreationInvalidExtension() {
        UUID uuid = UUID.randomUUID();
        new Song(uuid, "txt", "Invalid Extension");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSongCreationEmptyTitle() {
        UUID uuid = UUID.randomUUID();
        new Song(uuid, "mp3", "");
    }

    @Test
    public void testIsExtentionValide() {
        assertTrue(Song.isExtentionValide("mp3"));
        assertTrue(Song.isExtentionValide("MP3"));
        assertTrue(Song.isExtentionValide("flac"));
        assertTrue(Song.isExtentionValide("ogg"));
        assertFalse(Song.isExtentionValide("wav"));
        assertFalse(Song.isExtentionValide("txt"));
        assertFalse(Song.isExtentionValide(null));
    }

    @Test
    public void testGetDateString() {
        UUID uuid = UUID.randomUUID();
        Song song = new Song(uuid, "mp3", "Date Test");
        
        // Default format dd/MM/yyyy
        String expectedDate = LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        assertEquals(expectedDate, song.getDateString(""));
        
        // Custom format
        String customFormat = "yyyy-MM-dd";
        String expectedCustomDate = LocalDate.now().format(DateTimeFormatter.ofPattern(customFormat));
        assertEquals(expectedCustomDate, song.getDateString(customFormat));
    }

    @Test
    public void testDuplicateSongTitlesAllowed() {
        // En l'état actuel du code, deux objets Song avec le même titre peuvent être créés (car identifiés par UUID)
        UUID uuid1 = UUID.randomUUID();
        UUID uuid2 = UUID.randomUUID();
        String title = "Chanson Identique";

        Song song1 = new Song(uuid1, "mp3", title);
        Song song2 = new Song(uuid2, "mp3", title);

        assertNotEquals(song1.getUUID(), song2.getUUID());
        assertEquals(song1.getTitre(), song2.getTitre());
    }

    @Test
    public void testSongNameComparator() {
        Song songA = new Song(UUID.randomUUID(), "mp3", "Alpha");
        Song songZ = new Song(UUID.randomUUID(), "mp3", "Zebra");

        List<Song> songs = new ArrayList<>();
        songs.add(songZ);
        songs.add(songA);

        songs.sort(new Song.SongNameComparator());

        assertEquals("Alpha", songs.get(0).getTitre());
        assertEquals("Zebra", songs.get(1).getTitre());
    }

    @Test
    public void testSongDateComparator() {
        Song song1 = new Song(UUID.randomUUID(), "mp3", "Chanson 1");
        Song song2 = new Song(UUID.randomUUID(), "mp3", "Chanson 2");

        Song.SongDateComparator comparator = new Song.SongDateComparator();
        // Deux chansons créées le même jour
        assertEquals(0, comparator.compare(song1, song2));
    }

    @Test
    public void testToString() {
        UUID uuid = UUID.randomUUID();
        Song song = new Song(uuid, "mp3", "Test String");
        String result = song.toString();

        assertTrue(result.contains("Test String"));
        assertTrue(result.contains("mp3"));
        assertTrue(result.contains(uuid.toString()));
    }

    @Test
    public void testSongRenameValid() {
        Song song = new Song(UUID.randomUUID(), "mp3", "Ancien Titre");
        assertEquals("Ancien Titre", song.getTitre());

        song.setTitre("Nouveau Titre");
        assertEquals("Nouveau Titre", song.getTitre());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSongRenameEmptyThrows() {
        Song song = new Song(UUID.randomUUID(), "mp3", "Titre Initial");
        song.setTitre("");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSongRenameBlankThrows() {
        Song song = new Song(UUID.randomUUID(), "mp3", "Titre Initial");
        song.setTitre("   ");
    }

    @Test(expected = IllegalArgumentException.class)
    @SuppressWarnings("ConstantConditions")
    public void testSongRenameNullThrows() {
        Song song = new Song(UUID.randomUUID(), "mp3", "Titre Initial");
        song.setTitre(null);
    }
}
