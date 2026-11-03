package com.example.projetmuseappli.androidClasses.utilitaries;

import android.content.Context;
import android.util.Log;

import com.example.projetmuseappli.elements.Song;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Gestionnaire de l'historique de lecture persistant conservant les 50 derniers morceaux joués.
 */
public class PlaybackHistoryManager implements Serializable {

    private static final long serialVersionUID = 123456789L;
    private static final String FILE_NAME = "playback_history.ser";
    private static final int MAX_HISTORY_SIZE = 50;

    private static volatile PlaybackHistoryManager instance;
    private LinkedList<Song> historyList;

    /**
     * Interface d'écoute pour la modification de l'historique de lecture.
     */
    public interface HistoryChangeListener {
        /**
         * Événement déclenché lorsque l'historique de lecture est mis à jour.
         */
        void onHistoryChanged();
    }

    private transient List<HistoryChangeListener> listeners;

    private List<HistoryChangeListener> getListeners() {
        if (listeners == null) {
            listeners = new CopyOnWriteArrayList<>();
        }
        return listeners;
    }

    private PlaybackHistoryManager() {
        historyList = new LinkedList<>();
    }

    /**
     * Retourne l'instance unique du gestionnaire d'historique de lecture.
     *
     * @param context Le contexte Android.
     * @return L'instance unique de {@link PlaybackHistoryManager}.
     */
    public static PlaybackHistoryManager getInstance(Context context) {
        if (instance == null) {
            synchronized (PlaybackHistoryManager.class) {
                if (instance == null) {
                    loadHistory(context);
                }
            }
        }
        return instance;
    }

    private static void loadHistory(Context context) {
        if (context == null) {
            instance = new PlaybackHistoryManager();
            return;
        }
        Context appContext = context.getApplicationContext();
        try (FileInputStream fis = appContext.openFileInput(FILE_NAME);
             ObjectInputStream ois = new ObjectInputStream(fis)) {
            instance = (PlaybackHistoryManager) ois.readObject();
            if (instance.historyList == null) {
                instance.historyList = new LinkedList<>();
            }
        } catch (Exception e) {
            Log.i("PlaybackHistoryManager", "Création d'un nouvel historique de lecture.");
            instance = new PlaybackHistoryManager();
        }
    }

    /**
     * Ajoute un morceau à l'historique de lecture (max 50 morceaux, du plus récent au plus ancien).
     *
     * @param context Le contexte Android.
     * @param song Le morceau {@link Song} joué.
     */
    public synchronized void addSongToHistory(Context context, Song song) {
        if (song == null) return;

        historyList.removeIf(s -> s.getUUID().equals(song.getUUID()));
        historyList.addFirst(song);

        while (historyList.size() > MAX_HISTORY_SIZE) {
            historyList.removeLast();
        }

        saveHistory(context);
        notifyListeners();
    }

    /**
     * Retourne la liste courante des morceaux joués (du plus récent au plus ancien).
     *
     * @return La liste d'objets {@link Song}.
     */
    public synchronized List<Song> getHistoryList() {
        return new ArrayList<>(historyList);
    }

    /**
     * Efface l'ensemble de l'historique de lecture.
     *
     * @param context Le contexte Android.
     */
    public synchronized void clearHistory(Context context) {
        historyList.clear();
        saveHistory(context);
        notifyListeners();
    }

    private void saveHistory(Context context) {
        if (context == null) return;
        Context appContext = context.getApplicationContext();
        try (FileOutputStream fos = appContext.openFileOutput(FILE_NAME, Context.MODE_PRIVATE);
             ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            oos.writeObject(this);
        } catch (Exception e) {
            Log.e("PlaybackHistoryManager", "Erreur lors de la sauvegarde de l'historique", e);
        }
    }

    /**
     * Enregistre un écouteur de modifications sur l'historique.
     *
     * @param listener L'écouteur à ajouter.
     */
    public void addListener(HistoryChangeListener listener) {
        getListeners().add(listener);
    }

    /**
     * Supprime un écouteur de modifications.
     *
     * @param listener L'écouteur à retirer.
     */
    public void removeListener(HistoryChangeListener listener) {
        getListeners().remove(listener);
    }

    private void notifyListeners() {
        for (HistoryChangeListener listener : getListeners()) {
            listener.onHistoryChanged();
        }
    }
}
