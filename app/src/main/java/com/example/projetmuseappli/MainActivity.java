package com.example.projetmuseappli;


import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.widget.PopupMenu;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projetmuseappli.fileManagment.Masterlist;
import com.example.projetmuseappli.fileManagment.Song;
import com.example.projetmuseappli.fileManagment.Utilitaries;
import com.example.projetmuseappli.ui.SongAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.UUID;

/**
 * Activité principale de l'application gérant l'affichage de la liste des sons
 * et les interactions utilisateur (importation, suppression).
 */
public class MainActivity extends AppCompatActivity {

    private SongAdapter songAdapter;
    private ActivityResultLauncher<String[]> filePickerLauncher;
    private boolean isDeleteMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Initialisation de la Masterlist (chargement des données persistées)
        Masterlist.init(this);

        // Gestion de l'affichage bord à bord (Edge-to-Edge)
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        
        // Ajustement des marges en fonction des barres système (status bar, navigation bar)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialisation des composants de l'interface
        setupRecyclerView();
        setupFilePicker();
        setupFloatingButton();
    }

    /**
     * Configure la liste (RecyclerView) pour l'affichage des sons.
     */
    private void setupRecyclerView() {
        RecyclerView recyclerView = findViewById(R.id.recyclerViewSongs);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        
        // Création de l'adapter avec les données de la Masterlist
        songAdapter = new SongAdapter(Masterlist.getInstance().getMasterlistList());
        
        // Définition de l'action lors d'un clic sur un élément de la liste
        songAdapter.setOnSongClickListener((song, position)->{
                    Toast.makeText(this, "Test de clic genéral", Toast.LENGTH_SHORT).show();
                });

        
        recyclerView.setAdapter(songAdapter);
    }

    /**
     * Configure le lanceur pour l'ouverture de l'explorateur de fichiers.
     */
    private void setupFilePicker() {
        filePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.OpenDocument(),
                uri -> {
                    if (uri != null) {
                        importSong(uri);
                    }
                }
        );
    }

    /**
     * Configure le bouton flottant (FAB) et son menu contextuel.
     */
    private void setupFloatingButton() {
        FloatingActionButton fab = findViewById(R.id.fabImport);
        fab.setOnClickListener(v -> {
            // Création d'un menu popup ancré sur le bouton
            PopupMenu popup = new PopupMenu(this, v);
            popup.getMenuInflater().inflate(R.menu.context_menu_global, popup.getMenu());
            
            popup.setOnMenuItemClickListener(item -> {
                int itemId = item.getItemId();
                if (itemId == R.id.action_import) {
                    // Lance le sélecteur de documents avec filtre sur les fichiers audio supportés TODO rendre le filtre dynamique
                    filePickerLauncher.launch(new String[]{"audio/mpeg", "audio/x-flac", "audio/ogg", "application/ogg"});
                    return true;
                } else if (itemId == R.id.action_new_playlist) {
                    Toast.makeText(this, R.string.FEATURE_TODO, Toast.LENGTH_SHORT).show(); //TODO ajouter la gestion d'autre playlist
                }
                return false;
            });
            popup.show();
        });
    }



//    /**
//     * Alterne entre le mode consultation normale et le mode suppression.
//     */
//    private void toggleDeleteMode() {
//        isDeleteMode = !isDeleteMode;
//        songAdapter.setSelectionMode(isDeleteMode);
//        if (isDeleteMode) {
//            Toast.makeText(this, "Sélectionnez un son à supprimer", Toast.LENGTH_SHORT).show();
//        }
//    }

    /**
     * Affiche une boîte de dialogue pour confirmer la suppression d'un son.
     */
    private void showDeleteConfirmation(Song song, int position) {


        new AlertDialog.Builder(this)
                .setTitle("Confirmation")
                .setMessage("Voulez-vous vraiment supprimer \"" + song.getTitre() + "\" ?")
                .setPositiveButton("Oui", (dialog, which) -> {
                    deleteSong(song, position);
                    //toggleDeleteMode(); // Désactivation du mode suppression après action
                });
                //.setNegativeButton("Non", (dialog, which) -> toggleDeleteMode()).show();
    }

    /**
     * Supprime le son de la liste, sauvegarde les changements et met à jour l'affichage.
     */
    private void deleteSong(Song song, int position) {
        if (Masterlist.getInstance().removeSong(song.getUUID())) {
            songAdapter.notifyItemRemoved(position); // Animation de suppression
            Toast.makeText(this, "Son supprimé", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Gère l'importation d'un son à partir de son URI.
     * Extrait le nom du fichier pour définir le titre et vérifie l'extension.
     */
    private void importSong(Uri uri) {
        String fileName = Utilitaries.getFileNameFromUri(this, uri);
        if (fileName != null) {
            String extension = "";
            String title = fileName;

            // Séparation du nom et de l'extension
            int lastDot = fileName.lastIndexOf('.');
            if (lastDot > 0) {
                extension = fileName.substring(lastDot + 1).toLowerCase();
                title = fileName.substring(0, lastDot);
            }

            try {
                // Création et ajout du nouvel objet Song
                Song newSong = new Song(UUID.randomUUID(), extension, title);
                Masterlist.getInstance().addSong(newSong);
                
                // Notification de l'adapter pour l'insertion visuelle
                songAdapter.notifyItemInserted(Masterlist.getInstance().getMasterlistList().size() - 1);
                Toast.makeText(this, "Importé : " + title, Toast.LENGTH_SHORT).show();
            } catch (IllegalArgumentException e) {
                // Gestion de l'erreur si l'extension n'est pas supportée
                Toast.makeText(this, "Erreur : " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }
}
