package com.example.projetmuseappli.androidClasses.fragments.FragmentStrategy;

import androidx.fragment.app.Fragment;

import com.example.projetmuseappli.androidClasses.fragments.HistoryListViewFragment;

/**
 * Stratégie de création du fragment affichant l'historique de lecture.
 */
public class HistoryListViewFragmentStrategy implements FragmentStrategy {

    /**
     * Crée et retourne une nouvelle instance de {@link HistoryListViewFragment}.
     *
     * @return Une nouvelle instance de {@link Fragment} pour l'historique.
     */
    @Override
    public Fragment createFragment() {
        return HistoryListViewFragment.newInstance();
    }
}
