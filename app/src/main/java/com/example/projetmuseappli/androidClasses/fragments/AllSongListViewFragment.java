package com.example.projetmuseappli.androidClasses.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;

import com.example.projetmuseappli.R;
import com.example.projetmuseappli.androidClasses.composant.adapters.MediaRecyclerViewAdapterInterface;
import com.example.projetmuseappli.androidClasses.composant.adapters.SongAdapter;
import com.example.projetmuseappli.androidClasses.overlays.SongItemPopupMenu;
import com.example.projetmuseappli.elements.Masterlist;
import com.example.projetmuseappli.elements.Song;

import java.util.List;
public class AllSongListViewFragment extends RecyclerListViewFragmentAbstract<Song, SongAdapter> {

    public static AllSongListViewFragment newInstance() {
        return new AllSongListViewFragment();
    }

    @Override
    protected List<Song> loadList() {
        return Masterlist.getInstance().getMasterlistSongList();
    }

    @Override
    public int getEmptyListMessageRes() {
        return R.string.no_songs_found;
    }

    @Override
    public SongAdapter getNewAdapter() {
        return new SongAdapter(getContext(), loadList(), new SongAdapter.OnSongClickListener() {
            @Override
            public void onItemClick(Song song) {
                Toast.makeText(requireActivity().getApplicationContext(),
                        getString(R.string.FEATURE_TODO), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onBurgerClick(View view, SongAdapter.SongViewHolder holder, Song song) {
                SongItemPopupMenu.showSongItemPopup(view, holder, song);
            }
        });
    }


}
