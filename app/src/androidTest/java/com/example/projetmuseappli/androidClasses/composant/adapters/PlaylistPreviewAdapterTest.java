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
public class PlaylistPreviewAdapterTest {

    private Context context;
    private List<Playlist> playlistList;

    @Before
    public void setUp() {
        context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        playlistList = new ArrayList<>();

        Song song = new Song(UUID.randomUUID(), "mp3", "Song Preview");
        playlistList.add(new Playlist("Preview 1", song));
        playlistList.add(new Playlist("Preview 2", song));
    }

    @Test
    public void testGetItemCount() {
        PlaylistPreviewAdapter adapter = new PlaylistPreviewAdapter(context, playlistList, null);
        assertEquals(2, adapter.getItemCount());
    }
}
