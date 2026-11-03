package com.example.projetmuseappli.androidClasses.utilitaries;

import android.content.Context;
import android.net.Uri;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import com.example.projetmuseappli.elements.Masterlist;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class LocalStorageImportManagerTest {

    private Context context;

    @Before
    public void setUp() {
        context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        Masterlist.init(context);
    }

    @Test
    public void testImportAudioFileSuccess() throws IOException {
        // Créer un fichier audio temporaire
        File tempFile = new File(context.getCacheDir(), "imported_song.mp3");
        try (FileOutputStream fos = new FileOutputStream(tempFile)) {
            fos.write("dummy audio content".getBytes());
        }

        Uri fileUri = Uri.fromFile(tempFile);
        int initialCount = Masterlist.getInstance().getMasterlistSongList().size();

        LocalStorageImportManager.importAudioFile(context, fileUri);

        int newCount = Masterlist.getInstance().getMasterlistSongList().size();
        assertEquals(initialCount + 1, newCount);

        // Nettoyage
        tempFile.delete();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testImportAudioFileInvalidExtension() throws IOException {
        File tempFile = new File(context.getCacheDir(), "unsupported_doc.txt");
        try (FileOutputStream fos = new FileOutputStream(tempFile)) {
            fos.write("text content".getBytes());
        }

        Uri fileUri = Uri.fromFile(tempFile);

        try {
            LocalStorageImportManager.importAudioFile(context, fileUri);
        } finally {
            tempFile.delete();
        }
    }
}
