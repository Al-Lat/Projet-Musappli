package com.example.projetmuseappli.androidClasses.composant.headersAndFooters;

import android.app.Activity;

import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;

import com.example.projetmuseappli.R;
import com.google.android.material.tabs.TabLayoutMediator;

/**
 * Composant représentant l'en-tête (header) principal gérant les onglets de navigation (TabLayout) reliés à un {@link ViewPager2}.
 */
public class MainHeader {

    /** Identifiant de ressource du layout de l'en-tête. */
    public static final int HEADER_ID = R.layout.component_header;

    private Activity activity;
    private TabLayout tabLayout;

    private String[] fragmentTitle;

    /**
     * Initialise le header principal et relie le {@link TabLayout} au {@link ViewPager2}.
     *
     * @param activity L'activité hôte.
     * @param viewPager Le ViewPager2 associé aux onglets.
     */
    public MainHeader(Activity activity, ViewPager2 viewPager){
        this.activity = activity;
        this.tabLayout = activity.findViewById(R.id.header_tab_layout);

        fragmentTitle = new String[]{
                activity.getString(R.string.menu_all_songs),
                activity.getString(R.string.menu_playlist),
                activity.getString(R.string.menu_history)
        };

        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> tab.setText(fragmentTitle[position])
        ).attach();

    }

    /**
     * Retourne le {@link TabLayout} contrôlé par cet en-tête.
     *
     * @return Le {@link TabLayout}.
     */
    public TabLayout getTabLayout() {
        return tabLayout;
    }

}
