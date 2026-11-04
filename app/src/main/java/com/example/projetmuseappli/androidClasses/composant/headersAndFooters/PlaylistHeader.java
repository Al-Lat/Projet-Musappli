package com.example.projetmuseappli.androidClasses.composant.headersAndFooters;

import android.app.Activity;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import com.example.projetmuseappli.R;
import com.example.projetmuseappli.androidClasses.composant.adapters.SongAdapter;
import com.example.projetmuseappli.androidClasses.fragments.AllSongListViewFragment.SortMode;
import com.example.projetmuseappli.androidClasses.overlays.PlaylistMenuChain;
import com.example.projetmuseappli.androidClasses.overlays.SongSortPopupMenu;
import com.example.projetmuseappli.elements.Masterlist;
import com.example.projetmuseappli.elements.Playlist;
import com.example.projetmuseappli.elements.Song;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Composant représentant l'en-tête spécifique d'une vue de playlist avec gestion du titre, de la recherche et du tri.
 */
public class PlaylistHeader {

    /** Identifiant de ressource du layout de l'en-tête de playlist. */
    public static final int HEADER_ID = R.layout.component_list_header;
    private final Activity activity;

    private SongAdapter adapter;

    private UUID uuidOfPlaylist;
    private Playlist playlist;

    private HeaderSearchHelper searchHelper;

    private String currentQuery = "";
    private SortMode currentSortMode = SortMode.DATE_DESC;

    public void setPlaylistAdapter(SongAdapter adapter) {
        this.adapter = adapter;
    }

    /**
     * Interface d'écoute pour la filtration des résultats de la playlist.
     */
    public interface PlaylistHeaderInterface {
        /**
         * Filtre les morceaux de la playlist en fonction de la requête saisie.
         *
         * @param query La chaîne de recherche.
         */
        void filterPlaylist(String query);
    }

    /**
     * Initialise l'en-tête de playlist avec l'identifiant UUID de la playlist.
     *
     * @param activity L'activité hôte.
     * @param uuidOfPlaylist L'identifiant {@link UUID} de la playlist.
     * @param adapter L'adaptateur de morceaux associé.
     */
    public PlaylistHeader(Activity activity, UUID uuidOfPlaylist, SongAdapter adapter){
        this.activity = activity;
        this.uuidOfPlaylist = uuidOfPlaylist;
        this.playlist = Masterlist.getInstance().getPlaylist(uuidOfPlaylist);
        this.adapter = adapter;
        setup();
    }

    /**
     * Initialise l'en-tête de playlist avec un objet {@link Playlist}.
     *
     * @param activity L'activité hôte.
     * @param playlist La playlist affichée.
     * @param adapter L'adaptateur de morceaux associé.
     */
    public PlaylistHeader(Activity activity, Playlist playlist, SongAdapter adapter){
        this.activity = activity;
        this.playlist = playlist;
        if (playlist != null) {
            this.uuidOfPlaylist = playlist.getUuid();
        }
        this.adapter = adapter;
        setup();
    }

    private void setup() {
        View decorView = activity.getWindow().getDecorView();
        searchHelper = new HeaderSearchHelper(decorView, activity, this::filterSongs);
        searchHelper.setOnClearEmptyListener(() -> searchHelper.setSearchMode(false));

        Playlist p = uuidOfPlaylist != null ? Masterlist.getInstance().getPlaylist(uuidOfPlaylist) : playlist;
        if (p != null && searchHelper.getHeaderTitle() != null) {
            searchHelper.getHeaderTitle().setText(p.getName());
        }

        if (searchHelper.getHeaderTitle() != null) {
            searchHelper.getHeaderTitle().setOnClickListener(v -> {
                if (adapter != null && adapter.isSelectionMode()) {
                    return;
                }
                Playlist targetPlaylist = uuidOfPlaylist != null ? Masterlist.getInstance().getPlaylist(uuidOfPlaylist) : playlist;
                if (targetPlaylist != null) {
                    new PlaylistMenuChain(activity).beginRenamePlaylistChain(targetPlaylist, () -> {
                        searchHelper.getHeaderTitle().setText(targetPlaylist.getName());
                    });
                }
            });
        }

        if (searchHelper.getHeaderBack() != null) {
            searchHelper.getHeaderBack().setVisibility(View.VISIBLE);
            searchHelper.getHeaderBack().setOnClickListener(v -> {
                if (searchHelper.isSearchMode()) {
                    searchHelper.setSearchMode(false);
                } else {
                    if (activity instanceof AppCompatActivity) {
                        ((AppCompatActivity) activity).getOnBackPressedDispatcher().onBackPressed();
                    } else {
                        activity.finish();
                    }
                }
            });
        }

        // Bouton de tri
        ImageButton headerSort = decorView.findViewById(R.id.header_sort);
        if (headerSort != null) {
            headerSort.setVisibility(View.VISIBLE);
            headerSort.setOnClickListener(v -> SongSortPopupMenu.showSortPopup(v, this::setSortMode));
        }
    }

    /**
     * Définit le mode de tri actif et met à jour la liste affichée.
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
     * Retourne le mode de tri actuel.
     *
     * @return Le mode de tri {@link SortMode}.
     */
    public SortMode getCurrentSortMode() {
        return currentSortMode;
    }

    private List<Song> getOriginalSongList() {
        if (uuidOfPlaylist != null) {
            Playlist p = Masterlist.getInstance().getPlaylist(uuidOfPlaylist);
            if (p != null && p.getSongList() != null) {
                return p.getSongList();
            }
        }
        if (playlist != null && playlist.getSongList() != null) {
            return playlist.getSongList();
        }
        return new ArrayList<>();
    }

    /**
     * Filtre les morceaux par requête textuelle.
     *
     * @param query La requête de recherche.
     */
    public void filterSongs(String query) {
        this.currentQuery = query != null ? query : "";
        filterAndSort();
    }

    /**
     * Applique le filtrage textuel et le tri sélectionné sur la liste des morceaux.
     */
    public void filterAndSort() {
        if (adapter == null) {
            return;
        }

        List<Song> originalList = getOriginalSongList();
        List<Song> filteredList = new ArrayList<>();

        // 1. Filtrage textuel par titre
        if (currentQuery == null || currentQuery.trim().isEmpty()) {
            filteredList.addAll(originalList);
        } else {
            String lowerQuery = currentQuery.toLowerCase();
            for (Song song : originalList) {
                if (song != null && song.getTitre() != null && song.getTitre().toLowerCase().contains(lowerQuery)) {
                    filteredList.add(song);
                }
            }
        }

        // 2. Application du tri
        switch (currentSortMode) {
            case DATE_ASC:
                filteredList.sort((s1, s2) -> s1.getDate().compareTo(s2.getDate()));
                break;
            case NAME_ASC:
                filteredList.sort((s1, s2) -> s1.getTitre().compareToIgnoreCase(s2.getTitre()));
                break;
            case NAME_DESC:
                filteredList.sort((s1, s2) -> s2.getTitre().compareToIgnoreCase(s1.getTitre()));
                break;
            case DATE_DESC:
            default:
                filteredList.sort((s1, s2) -> s2.getDate().compareTo(s1.getDate()));
                break;
        }

        adapter.setItems(filteredList);
    }

}
