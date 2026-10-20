package com.example.projetmuseappli.androidClasses.fragments.FragmentStrategy;

import androidx.fragment.app.Fragment;

import com.example.projetmuseappli.androidClasses.fragments.AllPlaylistListViewFragment;

/**
 * Stratégie de création du fragment affichant la liste de toutes les playlists.
 */
public class AllPlaylistListViewFragmentStrategy implements FragmentStrategy {

    /**
     * Crée et retourne une nouvelle instance de {@link AllPlaylistListViewFragment}.
     *
     * @return Une nouvelle instance de {@link Fragment} pour la liste des playlists.
     */
    @Override
    public Fragment createFragment() {
        return AllPlaylistListViewFragment.newInstance();
    }
}
