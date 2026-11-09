package com.example.projetmuseappli.androidClasses.utilitaries;

import android.content.Context;
import android.net.Uri;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class AudioFileComparatorTest {

    private Context context;

    @Before
    public void setUp() {
        context = InstrumentationRegistry.getInstrumentation().getTargetContext();
    }

    @Test
    public void testAreEqualWithIdenticalBinaryFiles() throws IOException {
        File file1 = new File(context.getCacheDir(), "cmp1.mp3");
        File file2 = new File(context.getCacheDir(), "cmp2.mp3");

        byte[] payload = "Audio sample stream test bytes for comparator".getBytes();
        try (FileOutputStream fos1 = new FileOutputStream(file1);
             FileOutputStream fos2 = new FileOutputStream(file2)) {
            fos1.write(payload);
            fos2.write(payload);
        }

        Uri uri1 = Uri.fromFile(file1);

        assertTrue("Deux fichiers au contenu identique doivent être déclarés égaux",
                AudioFileComparator.areEqual(context, uri1, file2));
        assertTrue("La comparaison binaire directe doit être true",
                AudioFileComparator.areContentsBinaryEqual(context, uri1, file2));

        file1.delete();
        file2.delete();
    }

    @Test
    public void testAreEqualWithDifferentFiles() throws IOException {
        File file1 = new File(context.getCacheDir(), "cmp_diff1.mp3");
        File file2 = new File(context.getCacheDir(), "cmp_diff2.mp3");

        try (FileOutputStream fos1 = new FileOutputStream(file1);
             FileOutputStream fos2 = new FileOutputStream(file2)) {
            fos1.write("Sample 1".getBytes());
            fos2.write("Sample 2 differing data".getBytes());
        }

        Uri uri1 = Uri.fromFile(file1);

        assertFalse("Deux fichiers au contenu différent doivent être déclarés distincts",
                AudioFileComparator.areEqual(context, uri1, file2));
        assertFalse("La comparaison binaire doit renvoyer false",
                AudioFileComparator.areContentsBinaryEqual(context, uri1, file2));

        file1.delete();
        file2.delete();
    }

    @Test
    public void testAreEqualWithNonExistentTargetFile() throws IOException {
        File file1 = new File(context.getCacheDir(), "cmp_exists.mp3");
        try (FileOutputStream fos1 = new FileOutputStream(file1)) {
            fos1.write("data".getBytes());
        }

        File nonExistent = new File(context.getCacheDir(), "does_not_exist.mp3");
        Uri uri1 = Uri.fromFile(file1);

        assertFalse("Un fichier cible inexistant doit retourner false",
                AudioFileComparator.areEqual(context, uri1, nonExistent));

        file1.delete();
    }
}
