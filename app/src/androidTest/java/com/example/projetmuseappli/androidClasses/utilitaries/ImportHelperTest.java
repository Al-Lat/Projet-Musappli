package com.example.projetmuseappli.androidClasses.utilitaries;

import android.content.Context;
import android.net.Uri;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.example.projetmuseappli.androidClasses.activities.MainActivity;
import com.example.projetmuseappli.androidClasses.fragments.ImportFragment;
import com.example.projetmuseappli.elements.Masterlist;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collections;

import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class ImportHelperTest {

    private Context context;

    @Before
    public void setUp() {
        context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        Masterlist.init(context);
        Masterlist.getInstance().clear();
    }

    @Test
    public void testGetContextFromCallerWithUnattachedFragment() {
        ImportFragment fragment = ImportFragment.newInstance();
        Context retrievedContext = ImportHelper.getContextFromCaller(fragment);
        assertNull("Un fragment non attaché à une activité ne doit pas retourner de contexte", retrievedContext);
    }

    @Test
    public void testGetContextFromCallerWithAttachedFragmentAndActivity() {
        try (ActivityScenario<MainActivity> scenario = ActivityScenario.launch(MainActivity.class)) {
            scenario.onActivity(activity -> {
                ImportFragment fragment = ImportFragment.newInstance();
                activity.getSupportFragmentManager().beginTransaction()
                        .add(fragment, "ImportTestTag")
                        .commitNow();

                Context fragmentContext = ImportHelper.getContextFromCaller(fragment);
                assertNotNull("Un fragment attaché doit retourner un contexte valide", fragmentContext);

                Context activityContext = ImportHelper.getContextFromCaller(activity);
                assertNotNull("Une activité active doit retourner un contexte valide", activityContext);
                assertEquals(activity, activityContext);
            });
        }
    }

    @Test
    public void testImportSongSuccess() throws IOException {
        File tempFile = new File(context.getCacheDir(), "helper_test.mp3");
        try (FileOutputStream fos = new FileOutputStream(tempFile)) {
            fos.write("audio content".getBytes());
        }

        Uri fileUri = Uri.fromFile(tempFile);
        int initialSize = Masterlist.getInstance().getMasterlistSongList().size();

        InstrumentationRegistry.getInstrumentation().runOnMainSync(() ->
            ImportHelper.importSong(context, Collections.singletonList(fileUri))
        );

        assertEquals(initialSize + 1, Masterlist.getInstance().getMasterlistSongList().size());
        tempFile.delete();
    }
}
