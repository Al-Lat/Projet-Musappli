package com.example.projetmuseappli.androidClasses.composant.headersAndFooters;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import android.widget.ImageButton;

import com.example.projetmuseappli.R;
import com.example.projetmuseappli.androidClasses.activities.MainActivity;
import com.example.projetmuseappli.androidClasses.services.PlayerManager;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 33)
public class MainFooterTest {

    @Test
    public void testFormatTimeShortDuration() {
        // 1 min 30s / 3 min 45s
        long currentMs = 90_000L;
        long totalMs = 225_000L;
        String formatted = MainFooter.formatTime(currentMs, totalMs);
        assertEquals("01:30 / 03:45", formatted);
    }

    @Test
    public void testFormatTimeLongDurationMoreThan10Hours() {
        // 1 hour 23 min 45 sec / 10 hours 15 min 30 sec
        long currentMs = (3600 + 23 * 60 + 45) * 1000L;
        long totalMs = (10 * 3600 + 15 * 60 + 30) * 1000L;
        String formatted = MainFooter.formatTime(currentMs, totalMs);
        assertEquals("01:23:45 / 10:15:30", formatted);
    }

    @Test
    public void testFormatTimeZeroAndNegative() {
        String formatted = MainFooter.formatTime(-100L, 0L);
        assertEquals("00:00 / 00:00", formatted);
    }

    @Test
    public void testReplayButtonToggle() {
        androidx.media3.session.MediaController mockController = org.mockito.Mockito.mock(androidx.media3.session.MediaController.class);
        PlayerManager.setInstance(new PlayerManager(mockController));
        try {
            android.app.Activity activity = Robolectric.buildActivity(android.app.Activity.class).setup().get();
            ImageButton replayButton = new ImageButton(activity);
            replayButton.setId(R.id.replayButton);
            activity.setContentView(replayButton);

            MainFooter footer = new MainFooter(activity);
            assertNotNull(footer.getReplayButton());

            // Par défaut : désactivé (off)
            assertFalse(PlayerManager.getInstance(activity).isReplayEnabled());
            assertFalse(footer.getReplayButton().isSelected());

            // Clic pour activer (on)
            footer.getReplayButton().performClick();
            assertTrue(PlayerManager.getInstance(activity).isReplayEnabled());
            assertTrue(footer.getReplayButton().isSelected());

            // Clic pour désactiver (off)
            footer.getReplayButton().performClick();
            assertFalse(PlayerManager.getInstance(activity).isReplayEnabled());
            assertFalse(footer.getReplayButton().isSelected());
        } finally {
            PlayerManager.setInstance(null);
        }
    }
}
