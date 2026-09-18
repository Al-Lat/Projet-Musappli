package com.example.projetmuseappli.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projetmuseappli.fileManagment.Playlist;

import java.util.List;

public abstract class PlaylistAdapterAbstract <PlaylistViewHolderConcreteType extends PlaylistAdapterAbstract.PlaylistViewHolderAbstract> extends RecyclerView.Adapter<PlaylistViewHolderConcreteType>{

    /*Liste des sons à afficher*/
    protected List<Playlist> playlistList;
    protected Context appContext;

    public PlaylistAdapterAbstract(Context appContext, List<Playlist> allPlaylist) {
        this.appContext = appContext;
        this.playlistList = allPlaylist;
    }

    @NonNull
    @Override
    public abstract PlaylistViewHolderConcreteType onCreateViewHolder(@NonNull ViewGroup parent, int viewType);

    @NonNull
    protected View getInflatedItemView(@NonNull ViewGroup parent, int viewType,int layoutId){
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return inflater.inflate(layoutId, parent, false); //Transforme le fichier XML en vue
    }

    public abstract void onBindViewHolder(@NonNull PlaylistViewHolderConcreteType holder, int position);

    @Override
    public int getItemCount(){
        return playlistList.size();
    };

    public abstract static class PlaylistViewHolderAbstract extends RecyclerView.ViewHolder {
        public PlaylistViewHolderAbstract(@NonNull View itemView) {
            super(itemView);
        }

    }


}
