package com.example.projetmuseappli.androidClasses.overlays;

import android.content.Context;
import android.view.View;
import android.widget.PopupMenu;

import androidx.recyclerview.widget.RecyclerView;

import com.example.projetmuseappli.R;
import com.example.projetmuseappli.androidClasses.composant.adapters.MediaRecyclerViewAdapterInterface;
import com.example.projetmuseappli.elements.Masterlist;
import com.example.projetmuseappli.elements.Song;

public class SongItemPopupMenu {


    public static void showSongItemPopup(View view, RecyclerView.ViewHolder holder, Song currentSong) {

        MediaRecyclerViewAdapterInterface adapterOfItem = (MediaRecyclerViewAdapterInterface) holder.getBindingAdapter();


        PopupMenu pop = new PopupMenu(adapterOfItem.getContext(),view);
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
                holder.getBindingAdapter().notifyItemRemoved(currentPosition);
                holder.getBindingAdapter().notifyItemRangeChanged(currentPosition, adapterOfItem.getListOfAdapter().size() - currentPosition);

                return true;
            } else if (itemId == R.id.action_add_to_playlist) {
                /*Debuter la logique d'ajout a une playlist*/
                new PlaylistMenuChain(adapterOfItem.getContext()).beginAddToPlaylistChain(currentSong);

            }
            return false;
        });
        pop.show();
    }

}
