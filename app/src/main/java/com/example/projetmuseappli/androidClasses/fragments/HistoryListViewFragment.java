package com.example.projetmuseappli.androidClasses.fragments;

import android.view.View;

import com.example.projetmuseappli.R;
import com.example.projetmuseappli.androidClasses.composant.adapters.SongAdapter;
import com.example.projetmuseappli.androidClasses.overlays.SongItemPopupMenu;
import com.example.projetmuseappli.androidClasses.services.PlayerManager;
import com.example.projetmuseappli.androidClasses.utilitaries.PlaybackHistoryManager;
import com.example.projetmuseappli.elements.Song;

import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * Fragment affichant l'historique de lecture persistant des 50 derniers morceaux joués.
 */
public class HistoryListViewFragment extends RecyclerListViewFragmentAbstract<Song, SongAdapter>
        implements PlaybackHistoryManager.HistoryChangeListener {

    /**
     * Crée et retourne une nouvelle instance du fragment.
     *
     * @return Une nouvelle instance de {@link HistoryListViewFragment}.
     */
    public static HistoryListViewFragment newInstance() {
        return new HistoryListViewFragment();
    }

    @Override
    protected List<Song> loadList() {
        if (getContext() == null) {
            return Collections.emptyList();
        }
        return PlaybackHistoryManager.getInstance(requireContext()).getHistoryList();
    }

    @Override
    public int getEmptyListMessageRes() {
        return R.string.no_history_found;
    }

    @Override
    public SongAdapter getNewAdapter() {
        return new SongAdapter(getContext(), loadList(), new SongAdapter.OnSongClickListener() {
            @Override
            public void onItemClick(Song song) {
                PlayerManager.getInstance(requireContext()).playSong(requireContext(), song);
            }

            @Override
            public void onBurgerClick(View view, SongAdapter.SongViewHolder holder, Song song) {
                SongItemPopupMenu.showSongItemPopup(view, holder, song);
            }

            @Override
            public void onMultipleSongsBurgerClick(View view, Set<Song> selectedSongs) {
                SongItemPopupMenu.showMultipleSongsItemPopup(view, getAdapter(), selectedSongs);
            }
        });
    }

    @Override
    public void onHistoryChanged() {
        if (getView() == null || getActivity() == null) {
            return;
        }
        requireActivity().runOnUiThread(this::refrechFragment);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getContext() != null) {
            PlaybackHistoryManager.getInstance(requireContext()).addListener(this);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (getContext() != null) {
            PlaybackHistoryManager.getInstance(requireContext()).removeListener(this);
        }
    }
}
