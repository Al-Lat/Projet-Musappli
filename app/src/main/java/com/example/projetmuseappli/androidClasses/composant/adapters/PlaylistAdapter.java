package com.example.projetmuseappli.androidClasses.composant.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.projetmuseappli.R;
import com.example.projetmuseappli.elements.Playlist;

import java.util.List;

public class PlaylistAdapter extends PlaylistAdapterAbstract<PlaylistAdapter.PlaylistViewHolder>{

    public interface OnPlaylistClickListener {
        /**
         * Evenement de gestion du clic sur le viewHolder d'une playlist
         */
        void onItemClick(Playlist playlist);
        void onBurgerClick(View view,PlaylistViewHolder viewHolder,Playlist playlist);

    }

    private OnPlaylistClickListener listener;

    public static final int ITEM_LAYOUT_ID = R.layout.item_playlist;

    public PlaylistAdapter(Context appContext, List<Playlist> allPlaylist, OnPlaylistClickListener listener) {
        super(appContext, allPlaylist);
        this.listener = listener;
    }

    @NonNull
    @Override
    public PlaylistViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new PlaylistViewHolder(this.getInflatedItemView(parent, viewType,ITEM_LAYOUT_ID));
    }

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




    public static class PlaylistViewHolder extends PlaylistAdapterAbstract.PlaylistViewHolderAbstract{
        private TextView title;
        private TextView date;
        private  TextView size;
        private ImageButton burgerButton;

        /**
         * Constructeur du ViewHolder effectuant la liaison avec les vues du layout.
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
