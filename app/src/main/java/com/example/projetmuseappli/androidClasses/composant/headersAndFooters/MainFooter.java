package com.example.projetmuseappli.androidClasses.composant.headersAndFooters;

import android.app.Activity;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.media3.common.MediaItem;
import androidx.media3.common.Player;
import androidx.media3.common.Timeline;
import androidx.media3.session.MediaController;

import com.example.projetmuseappli.R;
import com.example.projetmuseappli.androidClasses.services.PlayerManager;

import java.util.Locale;

/**
 * Composant représentant le pied de page (footer) principal de l'application, gérant le bouton d'importation,
 * le bouton Play/Pause, les boutons de navigation (précédent/suivant), la barre de progression et le temps de lecture.
 */
public class MainFooter {

    /** Identifiant de ressource du layout du footer. */
    public static final int FOOTER_ID = R.layout.component_footer;

    private final Activity activity;

    private final View footerContainer;
    private final ImageButton boutonPlayPause;
    private final ImageButton previousButton;
    private final ImageButton nextButton;
    private final SeekBar playbackSeekBar;
    private final TextView songTitleView;
    private final TextView playbackTimeText;

    private boolean isUserSeeking = false;
    private final Handler updateHandler = new Handler(Looper.getMainLooper());
    private final Runnable updateProgressRunnable;

    private final Player.Listener playerListener = new Player.Listener() {
        @Override
        public void onEvents(@NonNull Player player, @NonNull Player.Events events) {
            updateUI();
        }

        @Override
        public void onIsPlayingChanged(boolean isPlaying) {
            updateUI();
            if (isPlaying) {
                startProgressUpdate();
            } else {
                stopProgressUpdate();
            }
        }

        @Override
        public void onMediaItemTransition(MediaItem mediaItem, int reason) {
            updateUI();
        }

        @Override
        public void onPlaybackStateChanged(int playbackState) {
            updateUI();
            if (playbackState == Player.STATE_ENDED) {
                updatePlayPauseButton(false);
                stopProgressUpdate();
            }
        }

        @Override
        public void onTimelineChanged(@NonNull Timeline timeline, int reason) {
            updateUI();
        }
    };

    /**
     * Initialise le composant footer et configure les actions sur les éléments d'interface.
     *
     * @param activity L'activité hébergeant ce footer.
     */
    public MainFooter(Activity activity){
        this.activity = activity;

        footerContainer = activity.findViewById(R.id.importButtonLayout);
        boutonPlayPause = activity.findViewById(R.id.playPauseButton);
        previousButton = activity.findViewById(R.id.previousButton);
        nextButton = activity.findViewById(R.id.nextButton);
        playbackSeekBar = activity.findViewById(R.id.playbackSeekBar);
        songTitleView = activity.findViewById(R.id.currentSongTitle);
        playbackTimeText = activity.findViewById(R.id.playbackTimeText);

        if (boutonPlayPause != null) {
            boutonPlayPause.setOnClickListener(v -> PlayerManager.getInstance(activity).togglePlayPause());
        }

        if (previousButton != null) {
            previousButton.setOnClickListener(v -> PlayerManager.getInstance(activity).seekToPrevious());
        }

        if (nextButton != null) {
            nextButton.setOnClickListener(v -> PlayerManager.getInstance(activity).seekToNext());
        }

        if (playbackSeekBar != null) {
            playbackSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    if (fromUser) {
                        PlayerManager.getInstance(activity).seekTo(progress);
                    }
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                    isUserSeeking = true;
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    isUserSeeking = false;
                    PlayerManager.getInstance(activity).seekTo(seekBar.getProgress());
                }
            });
        }

        updateProgressRunnable = new Runnable() {
            @Override
            public void run() {
                updateProgress();
                if (PlayerManager.getInstance(activity).isPlaying()) {
                    updateHandler.postDelayed(this, 500);
                }
            }
        };

        PlayerManager.getInstance(activity).addListener(activity, playerListener);
        updateUI();
    }

    private void updatePlayPauseButton(boolean isPlaying) {
        if (boutonPlayPause != null) {
            boutonPlayPause.setImageResource(isPlaying ? R.drawable.ic_pause : R.drawable.ic_play);
        }
    }

    private void updateNavigationButtons() {
        PlayerManager pm = PlayerManager.getInstance(activity);

        if (previousButton != null) {
            previousButton.setVisibility(pm.hasPrevious() ? View.VISIBLE : View.INVISIBLE);
        }
        if (nextButton != null) {
            nextButton.setVisibility(pm.hasNext() ? View.VISIBLE : View.INVISIBLE);
        }
    }

    private void updateSongTitle(MediaItem mediaItem) {
        if (songTitleView != null) {
            if (mediaItem != null && mediaItem.mediaMetadata.title != null) {
                songTitleView.setText(mediaItem.mediaMetadata.title);
                songTitleView.setVisibility(View.VISIBLE);
            } else {
                songTitleView.setVisibility(View.GONE);
            }
        }
    }

    private void updateProgress() {
        if (isUserSeeking || playbackSeekBar == null) {
            return;
        }

        PlayerManager pm = PlayerManager.getInstance(activity);
        long duration = pm.getDuration();
        long currentPos = pm.getCurrentPosition();

        if (playbackTimeText != null) {
            playbackTimeText.setText(formatTime(currentPos, duration));
        }

        if (duration > 0) {
            playbackSeekBar.setMax((int) duration);
            playbackSeekBar.setProgress((int) Math.min(currentPos, duration));
        } else {
            playbackSeekBar.setProgress(0);
        }
    }

    /**
     * Formate le temps écoulé par rapport au temps total
     * (ex: "01:23 / 03:45" ou "01:23:45 / 10:15:30").
     * Prévu pour supporter des durées d'au moins 10 heures.
     *
     * @param currentMs Temps écoulé en millisecondes.
     * @param totalMs Temps total en millisecondes.
     * @return La chaîne formatée "tempsEcoule / tempsTotal".
     */
    public static String formatTime(long currentMs, long totalMs) {
        long safeTotalMs = Math.max(0, totalMs);
        long safeCurrentMs = Math.max(0, currentMs);

        boolean showHours = safeTotalMs >= 3600000L;

        return formatSingleTime(safeCurrentMs, showHours)
                        + " / " + formatSingleTime(safeTotalMs, showHours);
    }

    private static String formatSingleTime(long ms, boolean showHours) {
        long totalSeconds = Math.max(0, ms / 1000);
        long hours = totalSeconds / 3600;
        long minutes = (totalSeconds % 3600) / 60;
        long seconds = totalSeconds % 60;

        if (showHours) {
            return String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, seconds);
        } else {
            return String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
        }
    }

    private void startProgressUpdate() {
        stopProgressUpdate();
        updateHandler.post(updateProgressRunnable);
    }

    private void stopProgressUpdate() {
        updateHandler.removeCallbacks(updateProgressRunnable);
    }

    private void updateFooterVisibility() {
        if (footerContainer == null) return;

        footerContainer.setVisibility(PlayerManager.getInstance(activity).hasSongLoaded()
                                                                ? View.VISIBLE : View.GONE);
    }

    private void updateUI() {
        PlayerManager pm = PlayerManager.getInstance(activity);
        updateFooterVisibility();
        boolean isPlaying = pm.isPlaying();
        updatePlayPauseButton(isPlaying);
        updateNavigationButtons();
        updateProgress();

        if (pm.hasSongLoaded()) {
            updateSongTitle(pm.getMediaController().getCurrentMediaItem());
        }

        if (isPlaying) {
            startProgressUpdate();
        }
    }
}
