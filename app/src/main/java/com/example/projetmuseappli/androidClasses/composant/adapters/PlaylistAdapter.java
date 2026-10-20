package com.example.projetmuseappli.androidClasses.composant.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projetmuseappli.R;
import com.example.projetmuseappli.elements.Playlist;

import java.util.List;

/**
 * Adaptateur de {@link RecyclerView} pour l'affichage complet des playlists.
 */
public class PlaylistAdapter extends PlaylistAdapterAbstract<PlaylistAdapter.PlaylistViewHolder>{

    /**
     * Interface de gestion des événements utilisateur (clic et menu d'options) sur une playlist.
     */
    public interface OnPlaylistClickListener {
        /**
         * Événement de gestion du clic principal sur l'élément d'une playlist.
         *
         * @param playlist La playlist sélectionnée.
         */
        void onItemClick(Playlist playlist);

        /**
         * Événement de gestion du clic sur le bouton menu ("burger") d'une playlist.
         *
         * @param view La vue ancre pour l'affichage du menu contextuel.
         * @param viewHolder Le ViewHolder de l'élément sélectionné.
         * @param playlist La playlist sélectionnée.
         */
        void onBurgerClick(View view, PlaylistViewHolder viewHolder, Playlist playlist);

    }

    private OnPlaylistClickListener listener;

    /** Identifiant de layout pour un item de playlist. */
    public static final int ITEM_LAYOUT_ID = R.layout.item_playlist;

    /**
     * Construit un adaptateur de playlists.
     *
     * @param appContext Le contexte de l'application.
     * @param allPlaylist La liste initiale de playlists à afficher.
     * @param listener L'écouteur des interactions utilisateur.
     */
    public PlaylistAdapter(Context appContext, List<Playlist> allPlaylist, OnPlaylistClickListener listener) {
        super(appContext, allPlaylist);
        this.listener = listener;
    }

    /**
     * Crée et initialise un nouveau {@link PlaylistViewHolder}.
     *
     * @param parent Le ViewGroup parent.
     * @param viewType Le type de vue.
     * @return Une nouvelle instance de {@link PlaylistViewHolder}.
     */
    @NonNull
    @Override
    public PlaylistViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new PlaylistViewHolder(this.getInflatedItemView(parent, viewType,ITEM_LAYOUT_ID));
    }

    /**
     * Lie les données de la playlist à la position donnée au ViewHolder spécifié.
     *
     * @param holder Le ViewHolder à mettre à jour.
     * @param position La position dans la liste.
     */
    @Override
    public void onBindViewHolder(@NonNull PlaylistViewHolder holder, int position) {
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

        holder.burgerButton.setOnClickListener(view -> {
            this.listener.onBurgerClick(view,holder,currentPlaylist);
        });
    }

    /**
     * ViewHolder représentant la vue d'un item de playlist dans la RecyclerView.
     */
    public static class PlaylistViewHolder extends PlaylistViewHolderAbstract{
        private TextView title;
        private TextView date;
        private TextView size;
        private ImageButton burgerButton;

        /**
         * Constructeur du ViewHolder effectuant la liaison avec les vues du layout.
         *
         * @param itemView La vue racine de l'item.
         */
        public PlaylistViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.playlistTitle);
            size = itemView.findViewById(R.id.playlistSize);
            date = itemView.findViewById(R.id.playlistDate);
            burgerButton = itemView.findViewById(R.id.buttonBurgerPlaylist);
        }

    }

}
