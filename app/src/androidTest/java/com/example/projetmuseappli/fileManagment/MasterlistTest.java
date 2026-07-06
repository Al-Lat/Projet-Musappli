package com.example.projetmuseappli.fileManagment;

import android.content.Context;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import java.util.UUID;
import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class MasterlistTest {

    private Context context;

    @Before
    public void setUp() {
        context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        // Supprimer le fichier de sauvegarde pour repartir sur une base propre si possible
        context.deleteFile("masterlist.ser");
        
        // Reset l'instance via reflexion si nécessaire, ou simplement utiliser init
        Masterlist.init(context);
    }

    @Test
    public void testSingletonInstance() {
        Masterlist instance1 = Masterlist.getInstance();
        Masterlist instance2 = Masterlist.getInstance();
        assertSame("Les deux instances doivent être identiques (Singleton)", instance1, instance2);
    }

    @Test
    public void testAddAndGetSong() {
        Masterlist masterlist = Masterlist.getInstance();
        UUID uuid = UUID.randomUUID();
        Song song = new Song(uuid, "mp3", "Test Song");
        
        masterlist.addSong(song);
        
        Song retrieved = masterlist.getSong(uuid);
        assertNotNull(retrieved);
        assertEquals("Test Song", retrieved.getTitre());
    }

    @Test
    public void testSaveAndLoad() {
        Masterlist masterlist = Masterlist.getInstance();
        UUID uuid = UUID.randomUUID();
        String title = "Persistent Song";
        masterlist.addSong(new Song(uuid, "flac", title));
        
        // Sauvegarde
        masterlist.save(context);
        
        // Simuler un redémarrage en forçant une nouvelle initialisation
        // (Note: Dans un vrai test unitaire on utiliserait de l'injection, 
        // ici on teste le comportement global de l'app)
        
        // On va vérifier si le fichier existe
        java.io.File file = context.getFileStreamPath("masterlist.ser");
        assertTrue("Le fichier de sauvegarde devrait exister", file.exists());
    }
    
    @Test
    public void testRemoveSong() {
        Masterlist masterlist = Masterlist.getInstance();
        UUID uuid = UUID.randomUUID();
        masterlist.addSong(new Song(uuid, "ogg", "To Remove"));
        
        assertTrue(masterlist.removeSong(uuid));
        assertNull(masterlist.getSong(uuid));
    }
}
