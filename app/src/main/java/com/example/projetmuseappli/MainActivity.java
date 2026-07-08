package com.example.projetmuseappli;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projetmuseappli.fileManagment.Masterlist;
import com.example.projetmuseappli.fileManagment.Song;
import com.example.projetmuseappli.ui.SongAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    private SongAdapter songAdapter;
    private ActivityResultLauncher<String[]> filePickerLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Initialisation de la Masterlist au lancement
        Masterlist.init(this);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        setupRecyclerView();
        setupFilePicker();
        setupFab();
    }

    private void setupRecyclerView() {
        RecyclerView recyclerView = findViewById(R.id.recyclerViewSongs);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        songAdapter = new SongAdapter(Masterlist.getInstance().getMasterlistArrayList());
        recyclerView.setAdapter(songAdapter);
    }

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

    private void setupFab() {
        FloatingActionButton fab = findViewById(R.id.fabImport);
        fab.setOnClickListener(v -> {
            // On filtre pour les types audio supportés par la classe Song (mp3, flac, ogg)
            filePickerLauncher.launch(new String[]{"audio/mpeg", "audio/x-flac", "audio/ogg", "application/ogg"});
        });
    }

    private void importSong(Uri uri) {
        String fileName = getFileName(uri);
        if (fileName != null) {
            String extension = "";
            String title = fileName;

            int lastDot = fileName.lastIndexOf('.');
            if (lastDot > 0) {
                extension = fileName.substring(lastDot + 1).toLowerCase();
                title = fileName.substring(0, lastDot);
            }

            try {
                Song newSong = new Song(UUID.randomUUID(), extension, title);
                Masterlist.getInstance().addSong(newSong);
                Masterlist.getInstance().save(this);
                songAdapter.notifyItemInserted(Masterlist.getInstance().getMasterlistArrayList().size() - 1);
                Toast.makeText(this, "Importé : " + title, Toast.LENGTH_SHORT).show();
            } catch (IllegalArgumentException e) {
                Toast.makeText(this, "Erreur : " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }

    private String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme() != null && uri.getScheme().equals("content")) {
            try (Cursor cursor = getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    int index = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    if (index != -1) {
                        result = cursor.getString(index);
                    }
                }
            }
        }
        if (result == null) {
            result = uri.getPath();
            if (result != null) {
                int cut = result.lastIndexOf('/');
                if (cut != -1) {
                    result = result.substring(cut + 1);
                }
            }
        }
        return result;
    }
}
