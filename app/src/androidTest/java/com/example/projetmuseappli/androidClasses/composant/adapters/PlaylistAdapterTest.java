package com.example.projetmuseappli.androidClasses.composant.adapters;

import android.content.Context;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.projetmuseappli.elements.Playlist;
import com.example.projetmuseappli.elements.Song;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class PlaylistAdapterTest {

    private Context context;
    private List<Playlist> playlistList;

    @Before
    public void setUp() {
        context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        playlistList = new ArrayList<>();

        Song song1 = new Song(UUID.randomUUID(), "mp3", "Song 1");
        Song song2 = new Song(UUID.randomUUID(), "flac", "Song 2");

        playlistList.add(new Playlist("Playlist 1", song1));
        playlistList.add(new Playlist("Playlist 2", song2));
    }

    @Test
    public void testGetItemCount() {
        PlaylistAdapter adapter = new PlaylistAdapter(context, playlistList, null);
        assertEquals(2, adapter.getItemCount());
    }

    @Test
    public void testSelectionModeToggle() {
        PlaylistAdapter adapter = new PlaylistAdapter(context, playlistList, null);

        assertFalse(adapter.isSelectionMode());

        Playlist pToSelect = playlistList.get(0);
        adapter.toggleSelection(pToSelect);

        assertTrue(adapter.isSelectionMode());
        assertEquals(1, adapter.getSelectedItems().size());

        adapter.clearSelection();
        assertFalse(adapter.isSelectionMode());
        assertEquals(0, adapter.getSelectedItems().size());
    }
}
