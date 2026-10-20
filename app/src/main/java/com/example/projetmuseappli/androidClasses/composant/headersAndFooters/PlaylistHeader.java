package com.example.projetmuseappli.androidClasses.composant.headersAndFooters;

import static android.content.Context.INPUT_METHOD_SERVICE;

import static com.google.android.material.internal.ViewUtils.hideKeyboard;

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

public class PlaylistHeader {

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

    public interface PlaylistHeaderInterface {
        public void filterPlaylist(String query);


    }






    public PlaylistHeader(Activity activity, UUID uuidOfPlaylist, SongAdapter adapter){
        this.activity = activity;
        this.uuidOfPlaylist = uuidOfPlaylist;
        this.adapter = adapter;
        setup();
    }

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
            imm.showSoftInput(searchInput, InputMethodManager.SHOW_IMPLICIT);
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
        imm.hideSoftInputFromWindow(searchInput.getWindowToken(), 0);
    }



}
