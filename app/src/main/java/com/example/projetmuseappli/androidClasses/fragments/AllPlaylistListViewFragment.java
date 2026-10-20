package com.example.projetmuseappli.androidClasses.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;

import com.example.projetmuseappli.R;
import com.example.projetmuseappli.androidClasses.activities.IntentManager;
import com.example.projetmuseappli.androidClasses.composant.adapters.PlaylistAdapter;
import com.example.projetmuseappli.androidClasses.composant.adapters.SongAdapter;
import com.example.projetmuseappli.androidClasses.overlays.PlaylistItemPopupMenu;
import com.example.projetmuseappli.androidClasses.overlays.SongItemPopupMenu;
import com.example.projetmuseappli.elements.Masterlist;
import com.example.projetmuseappli.elements.Playlist;
import com.example.projetmuseappli.elements.Song;

import java.util.List;

public class AllPlaylistListViewFragment extends RecyclerListViewFragmentAbstract<Playlist,PlaylistAdapter> {

    private List<Playlist> listDisplayed;

    @StringRes
    public final static int EMPTY_LIST_MESSAGE_RES = R.string.no_playlist_found;

    public static AllPlaylistListViewFragment newInstance() {

        AllPlaylistListViewFragment fragment = new AllPlaylistListViewFragment();

        return fragment;
    }

    @Override
    protected List<Playlist> loadList() {
        return Masterlist.getInstance().getMasterlistPlaylistList();
    }

    @Override
    public int getEmptyListMessageRes() {
        return R.string.no_playlist_found;
    }

    @Override
    public PlaylistAdapter getNewAdapter() {
        return new PlaylistAdapter(this.getContext(), loadList(), new PlaylistAdapter.OnPlaylistClickListener() {

            @Override
            public void onItemClick(Playlist playlist) {
                startActivity(IntentManager.getPlaylistIntent(getContext(),playlist));
            }

            @Override
            public void onBurgerClick(View view, PlaylistAdapter.PlaylistViewHolder viewHolder, Playlist playlist) {
                PlaylistItemPopupMenu.showPlaylistItemPopup(view, viewHolder, playlist);
            }
        });
    }


}
