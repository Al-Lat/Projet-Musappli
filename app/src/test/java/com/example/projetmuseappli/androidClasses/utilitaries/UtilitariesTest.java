package com.example.projetmuseappli.androidClasses.utilitaries;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class UtilitariesTest {

    @Test
    public void incrementSongTitle_withoutPrefix_addsInitialPrefix() {
        assertEquals("(1) - Ma chanson", Utilitaries.incrementSongTitle("Ma chanson"));
    }

    @Test
    public void incrementSongTitle_withExistingPrefix_incrementsNumber() {
        assertEquals("(2) - Ma chanson", Utilitaries.incrementSongTitle("(1) - Ma chanson"));
        assertEquals("(3) - Ma chanson", Utilitaries.incrementSongTitle("(2) - Ma chanson"));
        assertEquals("(10) - Ma chanson", Utilitaries.incrementSongTitle("(9) - Ma chanson"));
        assertEquals("(100) - Ma chanson", Utilitaries.incrementSongTitle("(99) - Ma chanson"));
    }

    @Test
    public void incrementSongTitle_withZero_incrementsToOne() {
        assertEquals("(1) - Ma chanson", Utilitaries.incrementSongTitle("(0) - Ma chanson"));
    }

    @Test
    public void incrementSongTitle_withEmptyString_returnsPrefix() {
        assertEquals("(1) - ", Utilitaries.incrementSongTitle(""));
    }

    @Test
    public void incrementSongTitle_withPrefixOnly_incrementsPrefix() {
        assertEquals("(2) - ", Utilitaries.incrementSongTitle("(1) - "));
    }

    @Test
    public void incrementSongTitle_withNonMatchingPrefix_prefixesCorrectly() {
        assertEquals("(1) - (abc) - Ma chanson", Utilitaries.incrementSongTitle("(abc) - Ma chanson"));
        assertEquals("(1) - Titre (1) - Ma chanson", Utilitaries.incrementSongTitle("Titre (1) - Ma chanson"));
    }
}
