package com.example.projetmuseappli.ui;

import android.content.Context;
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


    /*Liste des sons à afficher*/
    private List<Song> songList;
    private Context appContext;


    public SongAdapter(Context appContext, List<Song> songList) {
        this.appContext = appContext;
        this.songList = songList;
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

//        /*Gestion des actions*/
//        holder.itemView.setOnClickListener(v -> { // TODO : Gérer le clic sur l'item complet
//            if (listener != null) {
//                listener.onSongClick(currentSong, holder.getAdapterPosition());
//            }
//        });
//
//

        holder.buttonBurger.setOnClickListener(v -> { // Gérer le clic sur le bouton burger
            PopupMenu pop = new PopupMenu(this.appContext,v);
            pop.getMenuInflater().inflate(R.menu.context_menu_song, pop.getMenu());

            pop.setOnMenuItemClickListener((menuItem) -> {
                int itemId = menuItem.getItemId();

                int currentPosition = holder.getBindingAdapterPosition();
                if (currentPosition == RecyclerView.NO_POSITION) {
                    return false; // sécurité : le holder n'est plus attaché à un item valide
                }

                if (itemId == R.id.action_delete_song) {

                    /*suppression reel*/
                    Masterlist.getInstance().removeSong(currentSong);

                    /*actualisation de la vue*/
                    notifyItemRemoved(currentPosition);
                    notifyItemRangeChanged(currentPosition, songList.size() - currentPosition);

                    return true;
                } else if (itemId == R.id.action_add_to_playlist) {
                    Toast.makeText(this.appContext, R.string.FEATURE_TODO, Toast.LENGTH_SHORT).show(); //TODO ajouter la gestion d'autre playlist
                }
                return false;
            });

            pop.show();
        });
    }

    @Override
    public int getItemCount() {
        return songList != null ? songList.size() : 0; //indique au RecyclerView combien d'éléments il doit afficher
    }
}
