package com.example.projetmuseappli.androidClasses.fragments;

import androidx.fragment.app.Fragment;

import com.example.projetmuseappli.androidClasses.fragments.FragmentStrategy.FragmentStrategy;

import java.util.ArrayList;
import java.util.List;

/**
 * Gestionnaire d'une liste de stratégies de création de fragments.
 * Permet à une Activity ou un Adapter (tel qu'un ViewPager2 adapter) de configurer
 * les fragments à afficher ainsi que leur ordre d'apparition.
 */
public class FragmentList {

    private ArrayList<FragmentStrategy> listOfFragments;

    /**
     * Construit une liste de fragments vide.
     */
    public FragmentList(){
        this.listOfFragments = new ArrayList<>();
    }

    /**
     * Construit une liste de fragments à partir d'une liste initiale de stratégies.
     *
     * @param listOfFragments La liste initiale de stratégies de création de fragments.
     */
    public FragmentList(List<FragmentStrategy> listOfFragments){
        this.listOfFragments = new ArrayList<>(listOfFragments);
    }

    /**
     * Ajoute une stratégie de création de fragment à la fin de la liste.
     *
     * @param fragment La stratégie de fragment à ajouter.
     */
    public void addFragment(FragmentStrategy fragment){
        this.listOfFragments.add(fragment);
    }

    /**
     * Retourne le nombre de fragments dans la liste.
     *
     * @return Le nombre de stratégies de fragments.
     */
    public int size(){
        return this.listOfFragments.size();
    }

    /**
     * Instancie et retourne le fragment situé à la position spécifiée.
     *
     * @param position L'index du fragment à créer.
     * @return Une nouvelle instance de {@link Fragment} correspondant à la stratégie positionnée.
     */
    public Fragment createFragment(int position){
        return this.listOfFragments.get(position).createFragment();
    }

}
