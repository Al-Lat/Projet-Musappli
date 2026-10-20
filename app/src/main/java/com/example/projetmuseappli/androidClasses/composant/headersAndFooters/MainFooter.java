package com.example.projetmuseappli.androidClasses.composant.headersAndFooters;

import android.app.Activity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.PopupMenu;

import androidx.activity.result.ActivityResultCaller;

import com.example.projetmuseappli.R;
import com.example.projetmuseappli.androidClasses.utilitaries.ImportHelper;

public class MainFooter {

    public static final int FOOTER_ID = R.layout.component_footer;

    private Activity activity;
    private ImportHelper importHelper;

    public MainFooter(Activity activity){
        this.activity = activity;
        this.importHelper = new ImportHelper((ActivityResultCaller) activity);

        ImageButton boutonImport = activity.findViewById(R.id.importButton);
        boutonImport.setOnClickListener(view -> {
            //summonGeneralPopup(activity,view,importHelper);
            importHelper.launch();
        });
    }

    @Deprecated
    private static void summonGeneralPopup(Activity activity, View view, ImportHelper importHelper) {
        PopupMenu popup = new PopupMenu(activity, view);
        popup.getMenuInflater().inflate(R.menu.context_menu_global, popup.getMenu());
        popup.setOnMenuItemClickListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.action_import) {
                // Lance le sélecteur de documents avec filtre sur les fichiers audio supportés
                importHelper.launch();
                return true;
            }
            return false;
        });
        popup.show();
    }



}
