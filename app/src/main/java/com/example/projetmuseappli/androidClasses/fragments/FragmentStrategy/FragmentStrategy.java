package com.example.projetmuseappli.androidClasses.fragments.FragmentStrategy;

import androidx.fragment.app.Fragment;

/**
 * Interface définissant la stratégie de création d'un {@link Fragment}.
 */
public interface FragmentStrategy {

    /**
     * Instancie et retourne le {@link Fragment} associé à cette stratégie.
     *
     * @return Une instance de {@link Fragment}.
     */
    public Fragment createFragment();

}
