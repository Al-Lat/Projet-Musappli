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

import java.util.ArrayList;
import java.util.List;

public abstract class RecyclerListViewFragmentAbstract<ListType, AdapterType extends
        RecyclerView.Adapter<?> & MediaRecyclerViewAdapterInterface<ListType>>
        extends Fragment implements MasterlistListener {

    @StringRes
    public static final int DEFAULT_EMPTY_LIST_MESSAGE_RES = R.string.no_item_found;

    private RecyclerView recyclerView;
    private TextView errorTextView;
    private AdapterType adapter;

    /* ---- A fournir par les classes filles ---- */
    /** Source des données (ex : Masterlist.getInstance().getMasterlistSongList()). */
    protected abstract List<ListType> loadList();

    /** Construit l'adapter initial avec la liste courante. */
    public abstract AdapterType getNewAdapter();

    /** Message affiché quand la liste est vide. */
    @StringRes
    protected int getEmptyListMessageRes() {
        return DEFAULT_EMPTY_LIST_MESSAGE_RES;
    }


    public List<ListType> getListDisplayed() { return adapter.getListOfAdapter(); }

    protected AdapterType getAdapter() { return adapter; }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

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

    public void refrechFragment() {
        adapter.setItems(loadList());
        updateVisibility();
    }

    private void updateVisibility() {
        if (adapter.getListOfAdapter().isEmpty()) showErrorTextView(); else showRecyclerView();
    }

    public void showErrorTextView() {
        recyclerView.setVisibility(View.GONE);
        errorTextView.setVisibility(View.VISIBLE);
    }

    public void showRecyclerView() {
        recyclerView.setVisibility(View.VISIBLE);
        errorTextView.setVisibility(View.GONE);
    }

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
