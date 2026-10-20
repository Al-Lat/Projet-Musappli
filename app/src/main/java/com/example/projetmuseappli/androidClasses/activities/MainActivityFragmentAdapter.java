package com.example.projetmuseappli.androidClasses.activities;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.example.projetmuseappli.androidClasses.fragments.FragmentList;

/**
 * Adaptateur de fragments pour le {@link ViewPager2} de l'activité principale.
 */
public class MainActivityFragmentAdapter extends FragmentStateAdapter {

    private static final int NUMBER_OF_FRAGMENTS = 2;

    private FragmentList listOfFragments;

    /**
     * Construit l'adaptateur de fragments pour la MainActivity.
     *
     * @param activity L'instance de {@link MainActivity}.
     * @param listOfFragments La liste des stratégies de fragments à utiliser.
     */
    public MainActivityFragmentAdapter(@NonNull MainActivity activity, FragmentList listOfFragments){
        super(activity);
        this.listOfFragments = listOfFragments;
    }

    /**
     * Crée le fragment correspondant à la position donnée.
     *
     * @param position La position du fragment à créer.
     * @return Le {@link Fragment} instancié.
     */
    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return this.listOfFragments.createFragment(position);
    }

    /**
     * Retourne le nombre total de fragments disponibles.
     *
     * @return Le nombre de fragments.
     */
    @Override
    public int getItemCount() {
        return this.listOfFragments.size();
    }


}
