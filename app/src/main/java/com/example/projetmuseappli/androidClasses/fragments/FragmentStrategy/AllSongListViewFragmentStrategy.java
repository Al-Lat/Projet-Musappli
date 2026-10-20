package com.example.projetmuseappli.androidClasses.fragments.FragmentStrategy;

import androidx.fragment.app.Fragment;

import com.example.projetmuseappli.androidClasses.fragments.AllSongListViewFragment;

public class AllSongListViewFragmentStrategy implements FragmentStrategy{

    /**
     * TODO IA documentation
     * @return
     */
    @Override
    public Fragment createFragment() {
        return AllSongListViewFragment.newInstance();
    }
}
