package com.example.projetmuseappli.androidClasses.fragments;

import android.view.View;
import android.widget.Toast;

import com.example.projetmuseappli.R;
import com.example.projetmuseappli.androidClasses.composant.adapters.SongAdapter;
import com.example.projetmuseappli.androidClasses.overlays.SongItemPopupMenu;
import com.example.projetmuseappli.elements.Masterlist;
import com.example.projetmuseappli.elements.Song;

import java.util.List;

/**
 * Fragment affichant la liste globale de tous les morceaux de musique enregistrés dans l'application.
 */
public class AllSongListViewFragment extends RecyclerListViewFragmentAbstract<Song, SongAdapter> {

    /**
     * Crée et retourne une nouvelle instance du fragment.
     *
     * @return Une nouvelle instance de {@link AllSongListViewFragment}.
     */
    public static AllSongListViewFragment newInstance() {
        return new AllSongListViewFragment();
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
