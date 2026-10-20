package com.example.projetmuseappli.androidClasses.composant.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projetmuseappli.R;
import com.example.projetmuseappli.elements.Playlist;

import java.util.List;

/**
 * Adaptateur de {@link RecyclerView} pour l'affichage simplifié de prévisualisation de playlists.
 */
public class PlaylistPreviewAdapter extends PlaylistAdapterAbstract<PlaylistPreviewAdapter.PlaylistPreviewViewHolder>{

    /**
     * Interface de gestion du clic sur un item de prévisualisation de playlist.
     */
    public interface OnPlaylistPreviewClickListener {
        /**
         * Événement de gestion du clic sur le ViewHolder d'une playlist.
         *
         * @param playlist La playlist sélectionnée.
         */
        void onItemClick(Playlist playlist);

    }

    /** Identifiant de layout pour un item de prévisualisation de playlist. */
    public static final int ITEM_LAYOUT_ID = R.layout.item_playlist_preview;

    private OnPlaylistPreviewClickListener listener;

    /**
     * Construit l'adaptateur de prévisualisation des playlists.
     *
     * @param appContext Le contexte de l'application.
     * @param allPlaylist La liste de playlists à afficher.
     * @param listener L'écouteur des sélections de playlists.
     */
    public PlaylistPreviewAdapter(Context appContext, List<Playlist> allPlaylist,OnPlaylistPreviewClickListener listener) {
        super(appContext, allPlaylist);
        this.listener = listener;
    }

    /**
     * Crée et retourne une instance de {@link PlaylistPreviewViewHolder}.
     *
     * @param parent Le ViewGroup parent.
     * @param viewType Le type de vue.
     * @return Le ViewHolder créé.
     */
    @NonNull
    @Override
    public PlaylistPreviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new PlaylistPreviewViewHolder(this.getInflatedItemView(parent, viewType, ITEM_LAYOUT_ID));
    }

    /**
     * Lie les données d'une playlist aux éléments de vue de prévisualisation.
     *
     * @param holder Le ViewHolder.
     * @param position La position dans la liste.
     */
    @Override
    public void onBindViewHolder(@NonNull PlaylistPreviewViewHolder holder, int position) {
        Playlist currentPlaylist = this.playlistList.get(position);

        holder.date.setText(
                String.format(appContext.getString(R.string.item_card_date_creation_format)
                        ,currentPlaylist.getDate())
        );

        holder.size.setText(
                String.format(appContext.getString(R.string.item_card_playlist_size_format)
                        ,Integer.toString(currentPlaylist.getSize()))
        );

        holder.title.setText(currentPlaylist.getName());

        /*gestion des actions*/
        holder.itemView.setOnClickListener(view -> {
            this.listener.onItemClick(currentPlaylist);
        });

    }

    /**
     * ViewHolder pour un élément de prévisualisation de playlist.
     */
    public static class PlaylistPreviewViewHolder extends PlaylistViewHolderAbstract {

        private TextView title;
        private TextView date;
        private TextView size;

        /**
         * Constructeur du ViewHolder effectuant la liaison avec les vues du layout.
         *
         * @param itemView La vue racine de l'item.
         */
        public PlaylistPreviewViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.playlistPreviewTitle);
            size = itemView.findViewById(R.id.playlistPreviewSize);
            date = itemView.findViewById(R.id.playlistPreviewDate);
        }
    }

}
