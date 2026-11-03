package com.example.projetmuseappli.androidClasses.fragments.FragmentStrategy;

import androidx.fragment.app.Fragment;

import com.example.projetmuseappli.androidClasses.fragments.ImportFragment;

/**
 * Stratégie de création du fragment d'importation locale.
 */
public class ImportFragmentStrategy implements FragmentStrategy {

    /**
     * Crée et retourne une nouvelle instance de {@link ImportFragment}.
     *
     * @return Une nouvelle instance de {@link Fragment} pour l'importation.
     */
    @Override
    public Fragment createFragment() {
        return ImportFragment.newInstance();
    }
}
