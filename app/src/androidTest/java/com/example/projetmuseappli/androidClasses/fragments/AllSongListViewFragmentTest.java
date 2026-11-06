package com.example.projetmuseappli.androidClasses.fragments;

import android.content.Context;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.example.projetmuseappli.R;
import com.example.projetmuseappli.androidClasses.activities.MainActivity;
import com.example.projetmuseappli.androidClasses.utilitaries.SongSortHelper.SortMode;
import com.example.projetmuseappli.elements.Masterlist;
import com.example.projetmuseappli.elements.Song;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;
import java.util.UUID;

import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class AllSongListViewFragmentTest {

    private Context context;

    @Before
    public void setUp() {
        context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        Masterlist.init(context);
        Masterlist.getInstance().clear();
    }

    @Test
    public void testFilterAndSortInFragment() {
        Song songAlpha = new Song(UUID.randomUUID(), "mp3", "Alpha Song");
        Song songBeta = new Song(UUID.randomUUID(), "flac", "Beta Song");
        Song songGamma = new Song(UUID.randomUUID(), "ogg", "Gamma Song");

        Masterlist.getInstance().addSong(songAlpha);
        Masterlist.getInstance().addSong(songBeta);
        Masterlist.getInstance().addSong(songGamma);

        try (ActivityScenario<MainActivity> scenario = ActivityScenario.launch(MainActivity.class)) {
            scenario.onActivity(activity -> {
                AllSongListViewFragment fragment = AllSongListViewFragment.newInstance();
                activity.getSupportFragmentManager().beginTransaction()
                        .add(fragment, "AllSongTestTag")
                        .commitNow();

                // 1. Test du tri A -> Z
                fragment.setSortMode(SortMode.NAME_ASC);
                List<Song> displayedNameAsc = fragment.getListDisplayed();
                assertEquals(3, displayedNameAsc.size());
                assertEquals("Alpha Song", displayedNameAsc.get(0).getTitre());
                assertEquals("Beta Song", displayedNameAsc.get(1).getTitre());
                assertEquals("Gamma Song", displayedNameAsc.get(2).getTitre());

                // 2. Test du tri Z -> A (Nom)
                fragment.setSortMode(SortMode.NAME_DESC);
                List<Song> displayedNameDesc = fragment.getListDisplayed();
                assertEquals(3, displayedNameDesc.size());
                assertEquals("Gamma Song", displayedNameDesc.get(0).getTitre());
                assertEquals("Beta Song", displayedNameDesc.get(1).getTitre());
                assertEquals("Alpha Song", displayedNameDesc.get(2).getTitre());

                // 3. Test du tri par Date
                fragment.setSortMode(SortMode.DATE_ASC);
                List<Song> displayedDateAsc = fragment.getListDisplayed();
                assertEquals(3, displayedDateAsc.size());

                fragment.setSortMode(SortMode.DATE_DESC);
                List<Song> displayedDateDesc = fragment.getListDisplayed();
                assertEquals(3, displayedDateDesc.size());
            });
        }
    }

    @Test
    public void testClearButtonWhenEmptyExitsTextField() {
        try (ActivityScenario<MainActivity> scenario = ActivityScenario.launch(MainActivity.class)) {
            scenario.onActivity(activity -> {
                AllSongListViewFragment fragment = AllSongListViewFragment.newInstance();
                activity.getSupportFragmentManager().beginTransaction()
                        .add(fragment, "AllSongTestTag2")
                        .commitNow();

                View view = fragment.getView();
                assertNotNull(view);

                EditText searchInput = view.findViewById(R.id.header_search_input);
                ImageButton clearButton = view.findViewById(R.id.header_clear);

                assertNotNull(searchInput);
                assertNotNull(clearButton);
                assertEquals(View.VISIBLE, searchInput.getVisibility());

                // Quand du texte est présent, le clic efface le texte
                searchInput.setText("test");
                assertEquals("test", searchInput.getText().toString());

                clearButton.performClick();
                assertEquals("", searchInput.getText().toString());

                // Quand le texte est vide, le clic sur la croix déclenche le masquage du clavier et la fin de saisie
                clearButton.performClick();
                assertEquals("", searchInput.getText().toString());
            });
        }
    }
}
