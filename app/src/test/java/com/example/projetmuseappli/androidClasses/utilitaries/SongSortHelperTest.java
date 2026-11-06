package com.example.projetmuseappli.androidClasses.utilitaries;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import com.example.projetmuseappli.androidClasses.utilitaries.SongSortHelper.SortMode;
import com.example.projetmuseappli.elements.Song;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SongSortHelperTest {

    private List<Song> songs;

    @Before
    public void setUp() {
        Song songAlpha = new Song(UUID.randomUUID(), "mp3", "Alpha");
        Song songBeta = new Song(UUID.randomUUID(), "flac", "Beta");
        Song songGamma = new Song(UUID.randomUUID(), "ogg", "Gamma");

        songs = new ArrayList<>();
        songs.add(songGamma);
        songs.add(songAlpha);
        songs.add(songBeta);
    }

    @Test
    public void testSortNullOrEmpty() {
        assertNull(SongSortHelper.sortSongs(null, SortMode.NAME_ASC));
        List<Song> emptyList = new ArrayList<>();
        assertEquals(emptyList, SongSortHelper.sortSongs(emptyList, SortMode.NAME_ASC));
    }

    @Test
    public void testSortNameAscendingAndDescending() {
        List<Song> sortedAsc = SongSortHelper.sortSongs(songs, SortMode.NAME_ASC);
        assertEquals("Alpha", sortedAsc.get(0).getTitre());
        assertEquals("Beta", sortedAsc.get(1).getTitre());
        assertEquals("Gamma", sortedAsc.get(2).getTitre());

        List<Song> sortedDesc = SongSortHelper.sortSongs(songs, SortMode.NAME_DESC);
        assertEquals("Gamma", sortedDesc.get(0).getTitre());
        assertEquals("Beta", sortedDesc.get(1).getTitre());
        assertEquals("Alpha", sortedDesc.get(2).getTitre());
    }

    @Test
    public void testSortCustomAndDate() {
        List<Song> custom = SongSortHelper.sortSongs(songs, SortMode.CUSTOM);
        assertEquals("Gamma", custom.get(0).getTitre());

        List<Song> dateAsc = SongSortHelper.sortSongs(songs, SortMode.DATE_ASC);
        assertEquals(3, dateAsc.size());

        List<Song> dateDesc = SongSortHelper.sortSongs(songs, SortMode.DATE_DESC);
        assertEquals(3, dateDesc.size());
    }
}
