package com.example.projetmuseappli.androidClasses.fragments;

import android.view.View;

import androidx.annotation.StringRes;

import com.example.projetmuseappli.R;
import com.example.projetmuseappli.androidClasses.activities.IntentManager;
import com.example.projetmuseappli.androidClasses.composant.adapters.PlaylistAdapter;
import com.example.projetmuseappli.androidClasses.overlays.PlaylistItemPopupMenu;
import com.example.projetmuseappli.elements.Masterlist;
import com.example.projetmuseappli.elements.Playlist;

import java.util.List;

/**
 * Fragment affichant la liste de toutes les playlists enregistrées dans l'application.
 */
public class AllPlaylistListViewFragment extends RecyclerListViewFragmentAbstract<Playlist,PlaylistAdapter> {

    private List<Playlist> listDisplayed;

    /** Ressource chaîne de caractères pour le message affiché lorsque la liste de playlists est vide. */
    @StringRes
    public final static int EMPTY_LIST_MESSAGE_RES = R.string.no_playlist_found;

    /**
     * Crée et retourne une nouvelle instance du fragment.
     *
     * @return Une nouvelle instance de {@link AllPlaylistListViewFragment}.
     */
    public static AllPlaylistListViewFragment newInstance() {
        return new AllPlaylistListViewFragment();
    }

    /**
     * Charge la liste des playlists à partir de la {@link Masterlist}.
     *
     * @return La liste des objets {@link Playlist}.
     */
    @Override
    protected List<Playlist> loadList() {
        return Masterlist.getInstance().getMasterlistPlaylistList();
    }

    /**
     * Retourne l'identifiant du message à afficher en cas de liste vide.
     *
     * @return L'identifiant de ressource R.string.
     */
    @Override
    public int getEmptyListMessageRes() {
        return R.string.no_playlist_found;
    }

    /**
     * Instancie l'adaptateur {@link PlaylistAdapter} configuré avec les écouteurs de clics.
     *
     * @return L'instance de {@link PlaylistAdapter}.
     */
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
