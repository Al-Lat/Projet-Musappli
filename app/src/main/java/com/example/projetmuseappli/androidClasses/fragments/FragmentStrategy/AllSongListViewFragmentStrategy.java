package com.example.projetmuseappli.androidClasses.fragments.FragmentStrategy;

import androidx.fragment.app.Fragment;

import com.example.projetmuseappli.androidClasses.fragments.AllSongListViewFragment;

/**
 * Stratégie de création du fragment affichant la liste de toutes les chansons.
 */
public class AllSongListViewFragmentStrategy implements FragmentStrategy {

    /**
     * Crée et retourne une nouvelle instance de {@link AllSongListViewFragment}.
     *
     * @return Une nouvelle instance de {@link Fragment} pour la liste des chansons.
     */
    @Override
    public Fragment createFragment() {
        return AllSongListViewFragment.newInstance();
    }
}
