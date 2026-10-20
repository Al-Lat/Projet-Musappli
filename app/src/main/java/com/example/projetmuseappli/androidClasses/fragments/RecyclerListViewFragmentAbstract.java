package com.example.projetmuseappli.androidClasses.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projetmuseappli.R;
import com.example.projetmuseappli.androidClasses.composant.adapters.MediaRecyclerViewAdapterInterface;
import com.example.projetmuseappli.elements.Masterlist;
import com.example.projetmuseappli.elements.MasterlistListener;

import java.util.List;

/**
 * Classe de base abstraite pour les fragments affichant une liste réutilisable dans une {@link RecyclerView}.
 *
 * @param <ListType> Le type d'élément affiché.
 * @param <AdapterType> Le type d'adaptateur dérivé de RecyclerView.Adapter et implémentant {@link MediaRecyclerViewAdapterInterface}.
 */
public abstract class RecyclerListViewFragmentAbstract<ListType, AdapterType extends
        RecyclerView.Adapter<?> & MediaRecyclerViewAdapterInterface<ListType>>
        extends Fragment implements MasterlistListener {

    /** Message par défaut à afficher si la liste est vide. */
    @StringRes
    public static final int DEFAULT_EMPTY_LIST_MESSAGE_RES = R.string.no_item_found;

    private RecyclerView recyclerView;
    private TextView errorTextView;
    private AdapterType adapter;

    /* ---- À fournir par les classes filles ---- */

    /**
     * Charge et retourne la liste des données à afficher.
     *
     * @return La liste d'éléments de type {@code ListType}.
     */
    protected abstract List<ListType> loadList();

    /**
     * Construit l'adaptateur initial avec les données courantes.
     *
     * @return Une nouvelle instance de l'adaptateur.
     */
    public abstract AdapterType getNewAdapter();

    /**
     * Identifiant de la ressource texte affichée quand la liste est vide.
     *
     * @return L'identifiant R.string du message.
     */
    @StringRes
    protected int getEmptyListMessageRes() {
        return DEFAULT_EMPTY_LIST_MESSAGE_RES;
    }

    /**
     * Retourne la liste des éléments actuellement affichés dans l'adaptateur.
     *
     * @return La liste des éléments affichés.
     */
    public List<ListType> getListDisplayed() { return adapter.getListOfAdapter(); }

    /**
     * Retourne l'adaptateur de la RecyclerView.
     *
     * @return L'adaptateur de type {@code AdapterType}.
     */
    protected AdapterType getAdapter() { return adapter; }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * Gonfle la vue du fragment et initialise la RecyclerView et le TextView de liste vide.
     *
     * @param inflater Le LayoutInflater.
     * @param container Le container parent.
     * @param savedInstanceState État sauvegardé.
     * @return La vue racine du fragment.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recycler_view, container, false);

        recyclerView = view.findViewById(R.id.fragmentRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = getNewAdapter();
        recyclerView.setAdapter(adapter);

        errorTextView = view.findViewById(R.id.fragmentTextViewNoItem);
        errorTextView.setText(getEmptyListMessageRes());

        refrechFragment();
        return view;
    }

    /**
     * Rafraîchit les données du fragment à partir de {@link #loadList()} et ajuste la visibilité de la vue.
     */
    public void refrechFragment() {
        adapter.setItems(loadList());
        updateVisibility();
    }

    private void updateVisibility() {
        if (adapter.getListOfAdapter().isEmpty()) showErrorTextView(); else showRecyclerView();
    }

    /**
     * Masque la RecyclerView et affiche le message de liste vide.
     */
    public void showErrorTextView() {
        recyclerView.setVisibility(View.GONE);
        errorTextView.setVisibility(View.VISIBLE);
    }

    /**
     * Affiche la RecyclerView et masque le message de liste vide.
     */
    public void showRecyclerView() {
        recyclerView.setVisibility(View.VISIBLE);
        errorTextView.setVisibility(View.GONE);
    }

    /**
     * Callback invoqué lorsque la {@link Masterlist} signale un changement de données.
     */
    @Override
    public void onMasterlistChanged() {
        if (getView() == null) {
            return;
        }
        requireActivity().runOnUiThread(this::refrechFragment);
    }

    @Override public void onStart() {
        super.onStart();
        Masterlist.getInstance().addListener(this);
    }
    @Override public void onStop()  {
        super.onStop();
        Masterlist.getInstance().removeListener(this);
    }
}
