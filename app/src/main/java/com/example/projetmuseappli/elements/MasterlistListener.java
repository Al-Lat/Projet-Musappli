package com.example.projetmuseappli.elements;

/**
 * Interface d'écoute pour réagir aux modifications de la {@link Masterlist}.
 */
public interface MasterlistListener {

    /**
     * Méthode appelée lorsque les données enregistrées dans la {@link Masterlist} sont modifiées.
     */
    void onMasterlistChanged();
}
