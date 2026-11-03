package com.example.projetmuseappli.androidClasses.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.projetmuseappli.R;
import com.example.projetmuseappli.androidClasses.utilitaries.ImportHelper;

/**
 * Fragment permettant à l'utilisateur d'importer localement des fichiers audio dans l'application.
 */
public class ImportFragment extends Fragment {

    private ImportHelper importHelper;

    /**
     * Crée et retourne une nouvelle instance du fragment d'importation.
     *
     * @return Une nouvelle instance de {@link ImportFragment}.
     */
    public static ImportFragment newInstance() {
        return new ImportFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        importHelper = new ImportHelper(this);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_import, container, false);

        Button btnImport = view.findViewById(R.id.btnImportLocal);
        if (btnImport != null) {
            btnImport.setOnClickListener(v -> {
                if (importHelper != null) {
                    importHelper.launch();
                }
            });
        }

        return view;
    }
}
