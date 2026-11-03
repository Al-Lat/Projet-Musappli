package com.example.projetmuseappli.androidClasses.composant.adapters;

import android.content.Context;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.projetmuseappli.elements.Song;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class SongAdapterTest {

    private Context context;
    private List<Song> songList;

    @Before
    public void setUp() {
        context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        songList = new ArrayList<>();
        songList.add(new Song(UUID.randomUUID(), "mp3", "Chanson 1"));
        songList.add(new Song(UUID.randomUUID(), "flac", "Chanson 2"));
    }

    @Test
    public void testGetItemCount() {
        SongAdapter adapter = new SongAdapter(context, songList, null);
        assertEquals(2, adapter.getItemCount());
    }

    @Test
    public void testSelectionModeToggle() {
        SongAdapter adapter = new SongAdapter(context, songList, null);

        assertFalse(adapter.isSelectionMode());

        Song songToSelect = songList.get(0);
        adapter.toggleSelection(songToSelect);

        assertTrue(adapter.isSelectionMode());
        assertEquals(1, adapter.getSelectedItems().size());

        adapter.clearSelection();
        assertFalse(adapter.isSelectionMode());
        assertEquals(0, adapter.getSelectedItems().size());
    }

    @Test
    public void testToggleSelection() {
        SongAdapter adapter = new SongAdapter(context, songList, null);
        Song songToSelect = songList.get(0);

        assertEquals(0, adapter.getSelectedItems().size());

        adapter.toggleSelection(songToSelect);
        assertEquals(1, adapter.getSelectedItems().size());
        assertTrue(adapter.getSelectedItems().contains(songToSelect));

        adapter.toggleSelection(songToSelect);
        assertEquals(0, adapter.getSelectedItems().size());
        assertFalse(adapter.getSelectedItems().contains(songToSelect));
    }
}
