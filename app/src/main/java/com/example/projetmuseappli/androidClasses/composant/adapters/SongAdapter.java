package com.example.projetmuseappli.androidClasses.composant.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.projetmuseappli.R;
import com.example.projetmuseappli.elements.Song;

import java.util.Collections;
import java.util.List;

/**
 * Adapter pour la RecyclerView affichant la liste des objets Song.
 * Gère l'affichage des titres, des extensions et les interactions (clics).
 */
public class SongAdapter extends RecyclerView.Adapter<SongAdapter.SongViewHolder>
        implements MediaRecyclerViewAdapterInterface<Song> {



    //    /*Interface definisant un listener regroupant les methodes a donner à l'adaptateur afin de gérer les clics user*/
    public interface OnSongClickListener {
        /**
         * Evenement de gestion du clic sur le viewHolder d'un morceau
         */
        void onItemClick(Song song);
        /**
         * Evenement de gestion du clic sur le bouton burger du viewHolder d'un morceau
         */
        void onBurgerClick(View view,SongViewHolder holder, Song currentSong);
    }


    /*Liste des sons à afficher*/
    private List<Song> songList;
    private Context appContext;
    private OnSongClickListener listener;


    public SongAdapter(Context appContext, List<Song> songList, OnSongClickListener listener) {
        this.appContext = appContext;
        this.songList = songList;
        this.listener = listener;
    }


    @NonNull
    @Override
    public SongViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.item_song, parent, false); //Transforme le fichier XML en vue
        return new SongViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull SongViewHolder holder, int position) {
        /*Definir les valeurs d'affichage*/
        Song currentSong = songList.get(position);

        holder.textViewTitle.setText(currentSong.getTitre());

        holder.textViewExtension.setText(
                String.format(appContext.getString(R.string.item_card_extension_format)
                        , currentSong.getExtension())
        );

        holder.textViewDate.setText(
                String.format(appContext.getString(R.string.item_card_date_format)
                        , currentSong.getDate())
        );

        /*Gestion des actions*/
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                this.listener.onItemClick(currentSong);
            }
        });

        holder.buttonBurger.setOnClickListener(view -> {
            this.listener.onBurgerClick(view,holder,currentSong);
        });
    }

    @Override
    public int getItemCount() {
        return songList != null ? songList.size() : 0;
    }

    @Override
    public List<Song> getListOfAdapter() {
        return this.songList;
    }

    @Override
    public void setItems(List<Song> newItems) {
        this.songList = newItems;
        notifyDataSetChanged();
    }

    @Override
    public Context getContext() {
        return this.appContext;
    }

    public class SongViewHolder extends RecyclerView.ViewHolder {
        TextView textViewTitle;
        TextView textViewExtension;
        TextView textViewDate;
        View buttonBurger;

        /**
         * Constructeur du ViewHolder effectuant la liaison avec les vues du layout.
         * @param itemView La vue racine de l'item.
         */
        public SongViewHolder(@NonNull View itemView) {
            super(itemView);
            // Liaison des éléments graphiques du layout item_song.xml par leur ID
            textViewTitle = itemView.findViewById(R.id.textViewTitle);
            textViewExtension = itemView.findViewById(R.id.textViewExtension);
            textViewDate = itemView.findViewById(R.id.textViewDate);
            buttonBurger = itemView.findViewById(R.id.buttonBurger);
        }



    }


}
