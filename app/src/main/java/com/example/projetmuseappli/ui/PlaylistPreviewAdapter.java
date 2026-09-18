package com.example.projetmuseappli.ui;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.projetmuseappli.R;
import com.example.projetmuseappli.fileManagment.Playlist;

import java.util.List;

public class PlaylistPreviewAdapter extends PlaylistAdapterAbstract<PlaylistPreviewAdapter.PlaylistPreviewViewHolder>{

    public static final int ID_LAYOUT = R.layout.item_playlist_preview;


    public PlaylistPreviewAdapter(Context appContext, List<Playlist> allPlaylist) {
        super(appContext, allPlaylist);
    }

    @NonNull
    @Override
    public PlaylistPreviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new PlaylistPreviewViewHolder(this.getInflatedItemView(parent, viewType,ID_LAYOUT));
    }

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
        //holder.itemView.setOnClickListener();

    }


    public static class PlaylistPreviewViewHolder extends PlaylistViewHolderAbstract {

        private TextView title;
        private TextView date;
        private  TextView size;

        /**
         * Constructeur du ViewHolder effectuant la liaison avec les vues du layout.
         * @param itemView La vue racine de l'item.
         */
        public PlaylistPreviewViewHolder(@NonNull View itemView) {
            super(itemView);
            // Liaison des éléments graphiques du layout item_song.xml par leur ID
            title = itemView.findViewById(R.id.playlistPreviewTitle);
            size = itemView.findViewById(R.id.playlistPreviewSize);
            date = itemView.findViewById(R.id.playlistPreviewDate);
        }
    }



}



