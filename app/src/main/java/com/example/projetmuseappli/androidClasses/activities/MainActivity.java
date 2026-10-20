package com.example.projetmuseappli.androidClasses.activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.example.projetmuseappli.R;
import com.example.projetmuseappli.androidClasses.composant.headersAndFooters.MainFooter;
import com.example.projetmuseappli.androidClasses.composant.headersAndFooters.MainHeader;
import com.example.projetmuseappli.androidClasses.fragments.FragmentList;
import com.example.projetmuseappli.androidClasses.fragments.FragmentStrategy.AllPlaylistListViewFragmentStrategy;
import com.example.projetmuseappli.androidClasses.fragments.FragmentStrategy.AllSongListViewFragmentStrategy;
import com.example.projetmuseappli.elements.Masterlist;

/**
 * Activité principale de l'application gérant la navigation par onglets via {@link ViewPager2}.
 */
public class MainActivity extends AppCompatActivity {

    private MainHeader header;
    private MainFooter footer;

    private ViewPager2 fragmentContainer;

    /**
     * Initialise la vue principale, charge la {@link Masterlist} et configure le {@link ViewPager2} avec son adapteur.
     *
     * @param savedInstanceState État sauvegardé de l'activité, le cas échéant.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {

        Masterlist.init(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fragmentContainer = findViewById(R.id.main_fragment_container);
        fragmentContainer.setAdapter(new MainActivityFragmentAdapter(this,getFragmentList()));

        this.header = new MainHeader(this,fragmentContainer);
        this.footer = new MainFooter(this);

    }

    private static FragmentList getFragmentList(){
        FragmentList list = new FragmentList();
        list.addFragment(new AllSongListViewFragmentStrategy());
        list.addFragment(new AllPlaylistListViewFragmentStrategy());
        return list;
    }


}
