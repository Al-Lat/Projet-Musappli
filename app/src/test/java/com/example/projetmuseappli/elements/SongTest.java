package com.example.projetmuseappli.elements;

import static org.junit.Assert.*;
import org.junit.Test;
import java.util.UUID;
import java.time.LocalDate;

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
    public void testGetDateString() {
        UUID uuid = UUID.randomUUID();
        Song song = new Song(uuid, "mp3", "Date Test");
        
        // Default format dd/MM/yyyy
        String expectedDate = LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        assertEquals(expectedDate, song.getDateString(""));
        
        // Custom format
        String customFormat = "yyyy-MM-dd";
        String expectedCustomDate = LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern(customFormat));
        assertEquals(expectedCustomDate, song.getDateString(customFormat));
    }
}
