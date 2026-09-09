package com.example.projetmuseappli.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.projetmuseappli.R;
import com.example.projetmuseappli.fileManagment.Song;
import java.util.List;

/**
 * Adapter pour la RecyclerView affichant la liste des objets Song.
 * Gère l'affichage des titres, des extensions et les interactions (clics).
 */
public class SongAdapter extends RecyclerView.Adapter<SongViewHolder> {

    /*Interface indiquant les evenements possible sur un objet SongViewHolder*/
    public interface OnSongClickListener {
        void onSongClick(Song song, int position); // Clic sur toute la ligne
        void onMoreButtonClick(Song song, int position, View view); // Clic sur le bouton d'options
    }

    /*Liste des sons à afficher*/
    private List<Song> songList;

    /*Object listener de cet adapter*/
    private OnSongClickListener listener;

    public SongAdapter(List<Song> songList) {
        this.songList = songList;
    }

    public void setOnSongClickListener(OnSongClickListener listener) {
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
                String.format(holder.textViewExtension.getContext().getString(
                        R.string.item_card_extension_format), currentSong.getExtension()
        ));

        /*Gestion des actions*/
        holder.itemView.setOnClickListener(v -> { // Gérer le clic sur l'item complet
            if (listener != null) {
                listener.onSongClick(currentSong, holder.getAdapterPosition());
            }
        });


        // Note : assure-toi que l'ID dans SongViewHolder correspond bien au bouton du XML
        holder.buttonBurger.setOnClickListener(v -> { // Gérer le clic sur le bouton burger
            if (listener != null) {
                // On passe la 'view' (le bouton) pour pouvoir ancrer un menu popup dessus plus tard
                listener.onMoreButtonClick(currentSong, holder.getAdapterPosition(), v);
            }
        });
    }

    @Override
    public int getItemCount() {
        return songList != null ? songList.size() : 0; //indique au RecyclerView combien d'éléments il doit afficher
    }
}
