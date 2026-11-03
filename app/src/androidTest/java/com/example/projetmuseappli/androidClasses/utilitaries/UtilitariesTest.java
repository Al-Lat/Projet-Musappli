package com.example.projetmuseappli.androidClasses.utilitaries;

import android.content.Context;
import android.net.Uri;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;

import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class UtilitariesTest {

    private Context context;

    @Before
    public void setUp() {
        context = InstrumentationRegistry.getInstrumentation().getTargetContext();
    }

    @Test
    public void testGetFileNameFromFileUri() {
        Uri fileUri = Uri.parse("file:///sdcard/Music/test_song.mp3");
        String fileName = Utilitaries.getFileNameFromUri(context, fileUri);

        assertEquals("test_song.mp3", fileName);
    }

    @Test
    public void testGetFileNameFromLocalFileUri() {
        File localFile = new File(context.getCacheDir(), "sample_audio.flac");
        Uri uri = Uri.fromFile(localFile);

        String fileName = Utilitaries.getFileNameFromUri(context, uri);

        assertEquals("sample_audio.flac", fileName);
    }
}
