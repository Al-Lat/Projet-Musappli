package com.example.projetmuseappli.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.projetmuseappli.R;
import com.example.projetmuseappli.fileManagment.Song;
import java.util.List;

public class SongAdapter extends RecyclerView.Adapter<SongAdapter.SongViewHolder> {

    private List<Song> songList;

    public SongAdapter(List<Song> songList) {
        this.songList = songList;
    }

    @NonNull
    @Override
    public SongViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_song, parent, false);
        return new SongViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SongViewHolder holder, int position) {
        Song song = songList.get(position);
        holder.textViewTitle.setText(song.getTitre());
        holder.textViewExtension.setText("Format: " + song.getExtension());
    }

    @Override
    public int getItemCount() {
        return songList.size();
    }

    public void updateSongs(List<Song> newSongs) {
        this.songList = newSongs;
        notifyDataSetChanged();
    }

    static class SongViewHolder extends RecyclerView.ViewHolder {
        TextView textViewTitle;
        TextView textViewExtension;

        public SongViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewTitle = itemView.findViewById(R.id.textViewTitle);
            textViewExtension = itemView.findViewById(R.id.textViewExtension);
        }
    }
}
