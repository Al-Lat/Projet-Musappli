package com.example.projetmuseappli.androidClasses.overlays;

import android.app.AlertDialog;
import android.content.Context;

import androidx.annotation.NonNull;

import com.example.projetmuseappli.R;

/**
 * Classe utilitaire pour l'affichage centralisé et réutilisable des boîtes de dialogue de confirmation.
 */
public class ConfirmationDialogHelper {

    /**
     * Affiche une boîte de dialogue de confirmation générique avec des titres, messages et boutons personnalisés.
     *
     * @param context Le contexte d'exécution.
     * @param titleRes L'identifiant de ressource du titre du dialogue.
     * @param messageRes L'identifiant de ressource du message du dialogue.
     * @param onConfirm Callback exécuté lorsque l'utilisateur confirme l'action.
     */
    public static void showConfirmationDialog(@NonNull Context context, int titleRes, int messageRes, Runnable onConfirm) {
        new AlertDialog.Builder(context)
                .setTitle(titleRes)
                .setMessage(messageRes)
                .setPositiveButton(R.string.yes, (dialog, which) -> {
                    if (onConfirm != null) {
                        onConfirm.run();
                    }
                })
                .setNegativeButton(R.string.no, (dialog, which) -> dialog.dismiss())
                .show();
    }

    /**
     * Affiche une boîte de dialogue de confirmation de suppression standard.
     *
     * @param context Le contexte d'exécution.
     * @param onConfirm Callback exécuté lorsque l'utilisateur valide la suppression.
     */
    public static void showDeleteConfirmationDialog(@NonNull Context context, Runnable onConfirm) {
        showConfirmationDialog(context, R.string.delete_confirmation_title, R.string.delete_confirmation_message, onConfirm);
    }
}
