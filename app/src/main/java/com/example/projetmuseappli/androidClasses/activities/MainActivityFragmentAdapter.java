package com.example.projetmuseappli.androidClasses.activities;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.projetmuseappli.androidClasses.fragments.FragmentList;

public class MainActivityFragmentAdapter extends FragmentStateAdapter {

    private static final int NUMBER_OF_FRAGMENTS = 2;

    private FragmentList listOfFragments;

    public MainActivityFragmentAdapter(@NonNull MainActivity activity, FragmentList listOfFragments){
        super(activity);
        this.listOfFragments = listOfFragments;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return this.listOfFragments.createFragment(position);
    }

    @Override
    public int getItemCount() {
        return this.listOfFragments.size();
    }


}
