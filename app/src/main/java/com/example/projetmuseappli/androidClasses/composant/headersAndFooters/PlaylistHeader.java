package com.example.projetmuseappli.androidClasses.composant.headersAndFooters;

import static android.content.Context.INPUT_METHOD_SERVICE;

import android.app.Activity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.projetmuseappli.R;
import com.example.projetmuseappli.androidClasses.composant.adapters.SongAdapter;
import com.example.projetmuseappli.elements.Masterlist;
import com.example.projetmuseappli.elements.Playlist;

import java.util.UUID;

/**
 * Composant représentant l'en-tête spécifique d'une vue de playlist avec gestion du titre et de la recherche.
 */
public class PlaylistHeader {

    /** Identifiant de ressource du layout de l'en-tête de playlist. */
    public static final int HEADER_ID = R.layout.component_list_header;
    private final Activity activity;

    private SongAdapter adapter;

    private UUID uuidOfPlaylist;

    /*Indique si l'on doit afficher la barre de recherche ou non*/
    private boolean searchMode = false;

    private ImageButton headerBack;
    private ImageButton headerSearch;
    private ImageButton headerClear;
    private TextView headerTitle;
    private EditText searchInput;

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
        this.uuidOfPlaylist = playlist.getUuid();
        this.adapter = adapter;
        setup();
    }

    private void setup() {

        this.headerBack   = activity.findViewById(R.id.header_back);
        this.headerSearch = activity.findViewById(R.id.header_search);
        this.headerClear  = activity.findViewById(R.id.header_clear);
        this.headerTitle  = activity.findViewById(R.id.header_title);
        this.searchInput  = activity.findViewById(R.id.header_search_input);

        headerTitle.setText(Masterlist.getInstance().getPlaylist(uuidOfPlaylist).getName());

        headerBack.setOnClickListener(v -> {
            if (searchMode) {
                setSearchMode(false);
            } else {
                activity.onBackPressed();
            }
        });

        headerClear.setOnClickListener(v -> clearTextInput());

        headerSearch.setOnClickListener(v -> setSearchMode(true));

    }

    private void setSearchMode(boolean enabled) {
        searchMode = enabled;

        if (enabled) {
            headerTitle.setVisibility(View.GONE);
            headerSearch.setVisibility(View.GONE);
            searchInput.setVisibility(View.VISIBLE);
            headerClear.setVisibility(View.VISIBLE);

            searchInput.requestFocus();
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.showSoftInput(searchInput, InputMethodManager.SHOW_IMPLICIT);
            }
        } else {
            headerTitle.setVisibility(View.VISIBLE);
            headerSearch.setVisibility(View.VISIBLE);
            searchInput.setVisibility(View.GONE);
            headerClear.setVisibility(View.GONE);

            clearTextInput();
            hideKeyboard();
        }
    }

    private void clearTextInput(){
        searchInput.setText("");
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(searchInput.getWindowToken(), 0);
        }
    }

}
