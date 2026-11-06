package com.example.projetmuseappli.androidClasses.services;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.robolectric.Shadows.shadowOf;

import android.content.Intent;

import androidx.media3.session.MediaSession;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.android.controller.ServiceController;
import org.robolectric.annotation.Config;

import java.util.List;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 33)
public class PlaybackServiceTest {

    @Test
    @SuppressWarnings("ConstantConditions")
    public void testSessionActivityIsConfigured() {
        ServiceController<PlaybackService> controller = Robolectric.buildService(PlaybackService.class).create();
        PlaybackService service = controller.get();

        MediaSession session = service.onGetSession(null);
        assertNotNull(session);
        assertNotNull(session.getSessionActivity());
    }

    @Test
    @SuppressWarnings("ConstantConditions")
    public void testOnTaskRemovedPausesPlayback() {
        ServiceController<PlaybackService> serviceController = Robolectric.buildService(PlaybackService.class).create();
        PlaybackService service = serviceController.get();

        service.onTaskRemoved(new Intent());
        assertNotNull(service.onGetSession(null));
    }

    @Test
    public void testStopPlaybackAndServiceBroadcastsStopApp() {
        ServiceController<PlaybackService> serviceController = Robolectric.buildService(PlaybackService.class).create();
        PlaybackService service = serviceController.get();

        service.stopPlaybackAndService();
        serviceController.destroy();

        List<Intent> broadcastIntents = shadowOf(RuntimeEnvironment.getApplication()).getBroadcastIntents();
        boolean found = broadcastIntents.stream()
                .anyMatch(intent -> PlaybackService.ACTION_STOP_APP.equals(intent.getAction()));
        assertTrue(found);
    }
}
