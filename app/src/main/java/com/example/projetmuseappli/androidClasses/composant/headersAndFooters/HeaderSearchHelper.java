package com.example.projetmuseappli.androidClasses.composant.headersAndFooters;

import static android.content.Context.INPUT_METHOD_SERVICE;

import android.app.Activity;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.projetmuseappli.R;

/**
 * Composant helper encapsulant la logique d'en-tête de recherche textuelle et de gestion du clavier.
 */
public class HeaderSearchHelper {

    /**
     * Listener notifié lors du changement de saisie dans le champ de recherche.
     */
    public interface OnQueryChangeListener {
        void onQueryChanged(String query);
    }

    /**
     * Listener notifié lors du clic sur la croix quand le champ de recherche est vide.
     */
    public interface OnClearEmptyListener {
        void onClearEmpty();
    }

    private final View headerView;
    private final Activity activity;
    private final OnQueryChangeListener listener;
    private OnClearEmptyListener clearEmptyListener;

    private TextView headerTitle;
    private EditText searchInput;
    private ImageButton headerSearch;
    private ImageButton headerClear;
    private ImageButton headerBack;

    private boolean searchMode = false;

    /**
     * Initialise le helper de recherche.
     *
     * @param headerView La vue racine contenant les éléments de l'en-tête.
     * @param activity L'activité hôte (peut être null).
     * @param listener Le callback appelé à chaque modification de la chaîne de recherche.
     */
    public HeaderSearchHelper(@NonNull View headerView, @Nullable Activity activity, @NonNull OnQueryChangeListener listener) {
        this.headerView = headerView;
        this.activity = activity;
        this.listener = listener;
        setup();
    }

    /**
     * Définit le listener à exécuter lorsque l'utilisateur clique sur la croix alors que le champ est vide.
     *
     * @param clearEmptyListener Le listener d'effacement quand vide.
     */
    public void setOnClearEmptyListener(OnClearEmptyListener clearEmptyListener) {
        this.clearEmptyListener = clearEmptyListener;
    }

    private void setup() {
        headerBack = headerView.findViewById(R.id.header_back);
        headerTitle = headerView.findViewById(R.id.header_title);
        searchInput = headerView.findViewById(R.id.header_search_input);
        headerSearch = headerView.findViewById(R.id.header_search);
        headerClear = headerView.findViewById(R.id.header_clear);

        clearTextInput();

        if (headerSearch != null) {
            headerSearch.setOnClickListener(v -> setSearchMode(true));
        }

        if (headerClear != null) {
            headerClear.setOnClickListener(v -> {
                if (searchInput != null && searchInput.getText().length() > 0) {
                    clearTextInput();
                } else {
                    hideKeyboard();
                    if (searchInput != null) {
                        searchInput.clearFocus();
                    }
                    if (clearEmptyListener != null) {
                        clearEmptyListener.onClearEmpty();
                    }
                }
            });
        }

        if (searchInput != null) {
            searchInput.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (listener != null) {
                        listener.onQueryChanged(s.toString());
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {}
            });
        }
    }

    /**
     * Active ou désactive le mode de recherche.
     *
     * @param enabled {@code true} pour afficher le champ de recherche, {@code false} pour le masquer.
     */
    public void setSearchMode(boolean enabled) {
        searchMode = enabled;
        if (enabled) {
            if (headerTitle != null) headerTitle.setVisibility(View.GONE);
            if (headerSearch != null) headerSearch.setVisibility(View.GONE);
            if (searchInput != null) searchInput.setVisibility(View.VISIBLE);
            if (headerClear != null) headerClear.setVisibility(View.VISIBLE);

            clearTextInput();

            if (searchInput != null) {
                searchInput.requestFocus();
                Context ctx = activity != null ? activity : headerView.getContext();
                if (ctx != null) {
                    InputMethodManager imm = (InputMethodManager) ctx.getSystemService(INPUT_METHOD_SERVICE);
                    if (imm != null) {
                        imm.showSoftInput(searchInput, InputMethodManager.SHOW_IMPLICIT);
                    }
                }
            }
        } else {
            if (headerTitle != null) headerTitle.setVisibility(View.VISIBLE);
            if (headerSearch != null) headerSearch.setVisibility(View.VISIBLE);
            if (searchInput != null) searchInput.setVisibility(View.GONE);
            if (headerClear != null) headerClear.setVisibility(View.GONE);

            clearTextInput();
            hideKeyboard();
        }
    }

    /**
     * Indique si le mode de recherche est actuellement actif.
     *
     * @return {@code true} si en mode recherche, {@code false} sinon.
     */
    public boolean isSearchMode() {
        return searchMode;
    }

    /**
     * Efface le texte saisi dans le champ de recherche.
     */
    public void clearTextInput() {
        if (searchInput != null) {
            searchInput.setText("");
        }
    }

    /**
     * Masque le clavier virtuel.
     */
    public void hideKeyboard() {
        if (searchInput != null) {
            Context ctx = activity != null ? activity : headerView.getContext();
            if (ctx != null) {
                InputMethodManager imm = (InputMethodManager) ctx.getSystemService(INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(searchInput.getWindowToken(), 0);
                }
            }
        }
    }

    /**
     * Retourne le TextView du titre.
     *
     * @return Le TextView du titre ou null.
     */
    public TextView getHeaderTitle() {
        return headerTitle;
    }

    /**
     * Retourne l'ImageButton du bouton retour.
     *
     * @return L'ImageButton du bouton retour ou null.
     */
    public ImageButton getHeaderBack() {
        return headerBack;
    }

    /**
     * Retourne le champ EditText de saisie de recherche.
     *
     * @return L'EditText ou null.
     */
    public EditText getSearchInput() {
        return searchInput;
    }
}
