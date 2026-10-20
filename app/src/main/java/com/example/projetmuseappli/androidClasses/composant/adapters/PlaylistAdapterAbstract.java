package com.example.projetmuseappli.androidClasses.composant.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projetmuseappli.elements.Playlist;

import java.util.List;

/**
 * Classe abstraite de base pour les adaptateurs de RecyclerView gérant l'affichage de listes de {@link Playlist}.
 *
 * @param <PlaylistViewHolderConcreteType> Le type concréatisé de ViewHolder héritant de {@link PlaylistViewHolderAbstract}.
 */
public abstract class PlaylistAdapterAbstract
            <PlaylistViewHolderConcreteType extends PlaylistAdapterAbstract.PlaylistViewHolderAbstract>
        extends RecyclerView.Adapter<PlaylistViewHolderConcreteType>
        implements MediaRecyclerViewAdapterInterface<Playlist> {

    /*Liste des sons à afficher*/
    protected List<Playlist> playlistList;
    protected Context appContext;

    /**
     * Construit l'adaptateur abstrait de playlists.
     *
     * @param appContext Le contexte de l'application.
     * @param allPlaylist La liste de playlists.
     */
    public PlaylistAdapterAbstract(Context appContext, List<Playlist> allPlaylist) {
        this.appContext = appContext;
        this.playlistList = allPlaylist;
    }

    @NonNull
    @Override
    public abstract PlaylistViewHolderConcreteType onCreateViewHolder(@NonNull ViewGroup parent, int viewType);

    /**
     * Inflate et retourne la vue d'un item à partir de son identifiant de layout.
     *
     * @param parent Le ViewGroup parent.
     * @param viewType Le type de vue.
     * @param layoutId L'identifiant de ressource du layout.
     * @return La vue gonflée.
     */
    @NonNull
    protected View getInflatedItemView(@NonNull ViewGroup parent, int viewType,int layoutId){
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return inflater.inflate(layoutId, parent, false);
    }

    @Override
    public abstract void onBindViewHolder(@NonNull PlaylistViewHolderConcreteType holder, int position);

    /**
     * Retourne le nombre total d'éléments dans la liste de playlists.
     *
     * @return Le nombre d'éléments.
     */
    @Override
    public int getItemCount(){
        return playlistList != null ? playlistList.size() : 0;
    }

    /**
     * Classe abstraite de base pour les ViewHolders des adaptateurs de playlists.
     */
    public abstract static class PlaylistViewHolderAbstract extends RecyclerView.ViewHolder {

        /**
         * Construit un ViewHolder de playlist.
         *
         * @param itemView La vue racine de l'élément.
         */
        public PlaylistViewHolderAbstract(@NonNull View itemView) {
            super(itemView);
        }

    }

    /**
     * Retourne le contexte associatif de cet adaptateur.
     *
     * @return Le {@link Context}.
     */
    @Override
    public Context getContext(){
        return this.appContext;
    }

    /**
     * Retourne la liste des playlists affichées.
     *
     * @return La liste des {@link Playlist}.
     */
    @Override
    public List<Playlist> getListOfAdapter(){
        return this.playlistList;
    }

    /**
     * Remplace la liste courante de playlists et notifie la RecyclerView.
     *
     * @param newItems La nouvelle liste de playlists.
     */
    @Override
    public void setItems(List<Playlist> newItems) {
        this.playlistList = newItems;
        notifyDataSetChanged();
    }

}
