package com.example.projetmuseappli.androidClasses.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.example.projetmuseappli.R;
import com.example.projetmuseappli.androidClasses.composant.headersAndFooters.MainFooter;
import com.example.projetmuseappli.androidClasses.composant.headersAndFooters.MainHeader;
import com.example.projetmuseappli.androidClasses.fragments.FragmentList;
import com.example.projetmuseappli.androidClasses.fragments.FragmentStrategy.AllPlaylistListViewFragmentStrategy;
import com.example.projetmuseappli.androidClasses.fragments.FragmentStrategy.AllSongListViewFragmentStrategy;
import com.example.projetmuseappli.androidClasses.fragments.FragmentStrategy.HistoryListViewFragmentStrategy;
import com.example.projetmuseappli.androidClasses.fragments.FragmentStrategy.ImportFragmentStrategy;
import com.example.projetmuseappli.androidClasses.services.PlaybackService;
import com.example.projetmuseappli.elements.Masterlist;

/**
 * Activité principale de l'application gérant la navigation par onglets via {@link ViewPager2}.
 */
public class MainActivity extends AppCompatActivity {

    private MainHeader header;
    private MainFooter footer;

    private ViewPager2 fragmentContainer;

    private final BroadcastReceiver stopReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null && PlaybackService.ACTION_STOP_APP.equals(intent.getAction())) {
                finishAndRemoveTask();
            }
        }
    };

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

        IntentFilter filter = new IntentFilter(PlaybackService.ACTION_STOP_APP);
        ContextCompat.registerReceiver(this, stopReceiver, filter, ContextCompat.RECEIVER_NOT_EXPORTED);
    }

    @Override
    protected void onDestroy() {
        try {
            unregisterReceiver(stopReceiver);
        } catch (Exception ignored) {}
        super.onDestroy();
    }

    private static FragmentList getFragmentList(){
        FragmentList list = new FragmentList();
        list.addFragment(new AllSongListViewFragmentStrategy());
        list.addFragment(new AllPlaylistListViewFragmentStrategy());
        list.addFragment(new HistoryListViewFragmentStrategy());
        list.addFragment(new ImportFragmentStrategy());
        return list;
    }


}
