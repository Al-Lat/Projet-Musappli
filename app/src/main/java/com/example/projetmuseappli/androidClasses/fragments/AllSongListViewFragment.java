package com.example.projetmuseappli.androidClasses.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.projetmuseappli.R;
import com.example.projetmuseappli.androidClasses.composant.adapters.SongAdapter;
import com.example.projetmuseappli.androidClasses.composant.headersAndFooters.HeaderSearchHelper;
import com.example.projetmuseappli.androidClasses.overlays.SongItemPopupMenu;
import com.example.projetmuseappli.androidClasses.overlays.SongSortPopupMenu;
import com.example.projetmuseappli.androidClasses.services.PlayerManager;
import com.example.projetmuseappli.androidClasses.utilitaries.SongSortHelper;
import com.example.projetmuseappli.elements.Masterlist;
import com.example.projetmuseappli.elements.Song;

import com.example.projetmuseappli.androidClasses.utilitaries.SongSortHelper.SortMode;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Fragment affichant la liste globale de tous les morceaux de musique enregistrés dans l'application,
 * avec barre de recherche et menu de tri.
 */
public class AllSongListViewFragment extends RecyclerListViewFragmentAbstract<Song, SongAdapter> {

    private HeaderSearchHelper searchHelper;
    private String currentQuery = "";
    private SortMode currentSortMode = SortMode.DATE_DESC;

    public HeaderSearchHelper getSearchHelper() {
        return searchHelper;
    }

    /**
     * Crée et retourne une nouvelle instance du fragment.
     *
     * @return Une nouvelle instance de {@link AllSongListViewFragment}.
     */
    public static AllSongListViewFragment newInstance() {
        return new AllSongListViewFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);

        ViewGroup headerContainer = view.findViewById(R.id.fragmentHeaderContainer);
        if (headerContainer != null) {
            View headerView = inflater.inflate(R.layout.component_list_header, headerContainer, false);
            headerContainer.addView(headerView);
            headerContainer.setVisibility(View.VISIBLE);

            // Masquer le bouton retour et le titre pour afficher directement la barre de recherche
            ImageButton backButton = headerView.findViewById(R.id.header_back);
            if (backButton != null) {
                backButton.setVisibility(View.GONE);
            }

            TextView headerTitle = headerView.findViewById(R.id.header_title);
            if (headerTitle != null) {
                headerTitle.setVisibility(View.GONE);
            }

            // Supprimer le bouton de transition de recherche (loupe)
            ImageButton searchButton = headerView.findViewById(R.id.header_search);
            if (searchButton != null) {
                searchButton.setVisibility(View.GONE);
            }

            // Afficher directement le champ de recherche et le bouton effacer
            View searchInput = headerView.findViewById(R.id.header_search_input);
            if (searchInput != null) {
                searchInput.setVisibility(View.VISIBLE);
            }

            ImageButton clearButton = headerView.findViewById(R.id.header_clear);
            if (clearButton != null) {
                clearButton.setVisibility(View.VISIBLE);
            }

            // Afficher le bouton de tri
            ImageButton sortButton = headerView.findViewById(R.id.header_sort);
            if (sortButton != null) {
                sortButton.setVisibility(View.VISIBLE);
                sortButton.setOnClickListener(v -> SongSortPopupMenu.showSortPopup(v, this::setSortMode));
            }

            // Initialiser le helper de recherche
            searchHelper = new HeaderSearchHelper(headerView, getActivity(), query -> {
                currentQuery = query;
                filterAndSort();
            });
            searchHelper.clearTextInput();
        }

        return view;
    }

    /**
     * Définit le mode de tri actif et met à jour l'affichage.
     *
     * @param sortMode Le nouveau mode de tri.
     */
    public void setSortMode(SortMode sortMode) {
        if (sortMode != null) {
            this.currentSortMode = sortMode;
            filterAndSort();
        }
    }

    /**
     * Mode de tri actuellement sélectionné.
     *
     * @return Le mode de tri {@link SortMode}.
     */
    public SortMode getCurrentSortMode() {
        return currentSortMode;
    }

    /**
     * Filtre et trie la liste des morceaux selon la requête et le mode de tri courants.
     */
    public void filterAndSort() {
        if (getAdapter() == null) {
            return;
        }

        List<Song> allSongs = loadList();
        List<Song> filteredSongs = new ArrayList<>();

        // 1. Filtrage textuel par titre
        if (currentQuery == null || currentQuery.trim().isEmpty()) {
            filteredSongs.addAll(allSongs);
        } else {
            String lowerQuery = currentQuery.toLowerCase();
            for (Song song : allSongs) {
                if (song != null && song.getTitre() != null && song.getTitre().toLowerCase().contains(lowerQuery)) {
                    filteredSongs.add(song);
                }
            }
        }

        // 2. Application du tri centralisé via SongSortHelper
        filteredSongs = SongSortHelper.sortSongs(filteredSongs, currentSortMode);

        getAdapter().setItems(filteredSongs);

        if (filteredSongs.isEmpty()) {
            showErrorTextView();
        } else {
            showRecyclerView();
        }
    }

    @Override
    public void refreshFragment() {
        filterAndSort();
    }

    /**
     * Charge la liste globale des morceaux à partir de la {@link Masterlist}.
     *
     * @return La liste des objets {@link Song}.
     */
    @Override
    protected List<Song> loadList() {
        return Masterlist.getInstance().getMasterlistSongList();
    }

    /**
     * Retourne l'identifiant du message à afficher lorsque aucun morceau n'est présent.
     *
     * @return L'identifiant de ressource R.string.
     */
    @Override
    public int getEmptyListMessageRes() {
        return R.string.no_songs_found;
    }

    /**
     * Instancie l'adaptateur {@link SongAdapter} configuré avec ses callbacks de clic.
     *
     * @return L'instance de {@link SongAdapter}.
     */
    @Override
    public SongAdapter getNewAdapter() {
        return new SongAdapter(getContext(), loadList(), new SongAdapter.OnSongClickListener() {
            @Override
            public void onItemClick(Song song) {
                PlayerManager playerManager = PlayerManager.getInstance(requireContext());
                if (playerManager.isPlayingSong(song)) {
                    return;
                }
                playerManager.playSong(requireContext(), song);
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

}
