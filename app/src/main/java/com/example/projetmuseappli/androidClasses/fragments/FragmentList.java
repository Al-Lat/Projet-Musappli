package com.example.projetmuseappli.androidClasses.fragments;

import androidx.fragment.app.Fragment;

import com.example.projetmuseappli.androidClasses.fragments.FragmentStrategy.FragmentStrategy;

import java.util.ArrayList;
import java.util.List;

/**
 * Todo IA documentation
 * Cet objet permet a l'activity de choisir ses fragments et leur ordre
 */
public class FragmentList {

    private ArrayList<FragmentStrategy> listOfFragments;

    public FragmentList(){
        this.listOfFragments = new ArrayList<>();
    }

    public FragmentList(List<FragmentStrategy> listOfFragments){
        this.listOfFragments = new ArrayList<>(listOfFragments);
    }

    public void addFragment(FragmentStrategy fragment){
        this.listOfFragments.add(fragment);
    }

    public int size(){
        return this.listOfFragments.size();
    }

    public Fragment createFragment(int position){
        return this.listOfFragments.get(position).createFragment();
    }

}
