package com.example.projetmuseappli.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.location.GnssAntennaInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.projetmuseappli.R;
import com.example.projetmuseappli.fileManagment.Masterlist;
import com.example.projetmuseappli.fileManagment.Song;
import java.util.List;

/**
 * Adapter pour la RecyclerView affichant la liste des objets Song.
 * Gère l'affichage des titres, des extensions et les interactions (clics).
 */
public class SongAdapter extends RecyclerView.Adapter<SongViewHolder> {

//    /*Interface definisant un listener regroupant les methodes a donner à l'adaptateur afin de gérer les clics user*/
    public interface OnSongClickListener {
        /**
         * Evenement de gestion du clic sur le viewHolder d'un morceau
         */
        //void onItemClick(Playlist playlist, int position);
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
                        , currentSong.getDateImport())
        );

        /*Gestion des actions*/
//        holder.itemView.setOnClickListener(v -> { // TODO : Gérer le clic sur l'item complet
//            if (listener != null) {
//                listener.onSongClick(currentSong, holder.getAdapterPosition());
//            }
//        });
//
//

        holder.buttonBurger.setOnClickListener(view -> { // Gérer le clic sur le bouton burger
            this.listener.onBurgerClick(view,holder,currentSong);
        });
    }

    @Override
    public int getItemCount() {
        return songList != null ? songList.size() : 0; //indique au RecyclerView combien d'éléments il doit afficher
    }








}
