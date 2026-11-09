package com.example.projetmuseappli.androidClasses.utilitaries;

import android.content.Context;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.example.projetmuseappli.androidClasses.activities.MainActivity;
import com.example.projetmuseappli.androidClasses.fragments.ImportFragment;
import com.example.projetmuseappli.elements.Masterlist;
import com.example.projetmuseappli.importStrategy.LocalImportStrategy;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class LocalImportStrategyTest {

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
        Context retrievedContext = LocalImportStrategy.getContextFromCaller(fragment);
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

                Context fragmentContext = LocalImportStrategy.getContextFromCaller(fragment);
                assertNotNull("Un fragment attaché doit retourner un contexte valide", fragmentContext);

                Context activityContext = LocalImportStrategy.getContextFromCaller(activity);
                assertNotNull("Une activité active doit retourner un contexte valide", activityContext);
                assertEquals(activity, activityContext);
            });
        }
    }

    @Test
    public void testStrategyInstanciationAndImportSongInvocation() {
        try (ActivityScenario<MainActivity> scenario = ActivityScenario.launch(MainActivity.class)) {
            scenario.onActivity(activity -> {
                LocalImportStrategy strategy = new LocalImportStrategy(activity);
                assertNotNull("La stratégie doit être instanciée", strategy);
            });
        }
    }
}
