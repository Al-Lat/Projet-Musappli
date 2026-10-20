package com.example.projetmuseappli.androidClasses.overlays;

import android.view.View;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.example.projetmuseappli.R;
import com.example.projetmuseappli.androidClasses.composant.adapters.MediaRecyclerViewAdapterInterface;
import com.example.projetmuseappli.elements.Masterlist;
import com.example.projetmuseappli.elements.Playlist;

public class PlaylistItemPopupMenu {

    public static void showPlaylistItemPopup(View view, RecyclerView.ViewHolder holder, Playlist currentPlaylist) {

        MediaRecyclerViewAdapterInterface adapterOfItem = (MediaRecyclerViewAdapterInterface) holder.getBindingAdapter();

        PopupMenu pop = new PopupMenu(adapterOfItem.getContext(),view);
        pop.getMenuInflater().inflate(R.menu.context_menu_playlist, pop.getMenu());
        pop.setOnMenuItemClickListener((menuItem) -> {
            int itemId = menuItem.getItemId();

            int currentPosition = holder.getBindingAdapterPosition();
            if (currentPosition == RecyclerView.NO_POSITION) {
                return false; // sécurité : le holder n'est plus attaché à un item valide
            }

            if (itemId == R.id.action_delete_playlist) {

                /*suppression reel*/
                Masterlist.getInstance().removePlaylist(currentPlaylist);

                /*actualisation de la vue*/
                holder.getBindingAdapter().notifyItemRemoved(currentPosition);
                holder.getBindingAdapter().notifyItemRangeChanged(currentPosition, adapterOfItem.getListOfAdapter().size() - currentPosition);

                return true;
            } else if (itemId == R.id.action_play_playlist) {
                /*Debuter la lecture d'une playlist*/
                Toast.makeText(adapterOfItem.getContext(), adapterOfItem.getContext().getString(R.string.FEATURE_TODO), Toast.LENGTH_SHORT).show();
                //TODO implémenter apres ajout de la lecture de son
            }
            return false;
        });
        pop.show();
    }

}
