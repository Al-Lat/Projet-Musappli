package com.example.projetmuseappli.androidClasses.fragments.FragmentStrategy;

import androidx.fragment.app.Fragment;

import com.example.projetmuseappli.androidClasses.fragments.AllPlaylistListViewFragment;

public class AllPlaylistListViewFragmentStrategy implements FragmentStrategy {

    /**
     * TODO IA documentation
     * @return
     */
    @Override
    public Fragment createFragment() {
        return AllPlaylistListViewFragment.newInstance();
    }
}
