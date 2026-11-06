package com.example.projetmuseappli.androidClasses.composant.headersAndFooters;

import android.content.Context;
import android.content.Intent;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.example.projetmuseappli.androidClasses.activities.PlaylistActivity;
import com.example.projetmuseappli.androidClasses.utilitaries.SongSortHelper.SortMode;
import com.example.projetmuseappli.elements.Masterlist;
import com.example.projetmuseappli.elements.Playlist;
import com.example.projetmuseappli.elements.Song;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;
import java.util.UUID;

import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class PlaylistHeaderTest {

    private Context context;

    @Before
    public void setUp() {
        context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        Masterlist.init(context);
        Masterlist.getInstance().clear();
    }

    @Test
    public void testPlaylistHeaderSortingAndFiltering() {
        Song song1 = new Song(UUID.randomUUID(), "mp3", "Zulu Song");
        Song song2 = new Song(UUID.randomUUID(), "flac", "Alpha Song");

        Playlist playlist = new Playlist("Test Sort Playlist", Arrays.asList(song1, song2));
        Masterlist.getInstance().addPlaylist(playlist);

        Intent intent = new Intent(context, PlaylistActivity.class);
        intent.putExtra("playlist", playlist);

        try (ActivityScenario<PlaylistActivity> scenario = ActivityScenario.launch(intent)) {
            scenario.onActivity(activity -> {
                PlaylistHeader header = new PlaylistHeader(activity, playlist, null);

                // Mode de tri par défaut doit être CUSTOM
                assertEquals(SortMode.CUSTOM, header.getCurrentSortMode());

                // Tri A -> Z
                header.setSortMode(SortMode.NAME_ASC);
                assertEquals(SortMode.NAME_ASC, header.getCurrentSortMode());

                // Tri Z -> A
                header.setSortMode(SortMode.NAME_DESC);
                assertEquals(SortMode.NAME_DESC, header.getCurrentSortMode());

                // Mode Personalisé
                header.setSortMode(SortMode.CUSTOM);
                assertEquals(SortMode.CUSTOM, header.getCurrentSortMode());
            });
        }
    }
}
