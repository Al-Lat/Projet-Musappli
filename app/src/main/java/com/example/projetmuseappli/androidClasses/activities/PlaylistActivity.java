package com.example.projetmuseappli.androidClasses.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projetmuseappli.R;
import com.example.projetmuseappli.androidClasses.composant.adapters.SongAdapter;
import com.example.projetmuseappli.androidClasses.composant.headersAndFooters.MainFooter;
import com.example.projetmuseappli.androidClasses.composant.headersAndFooters.PlaylistHeader;
import com.example.projetmuseappli.androidClasses.overlays.SongItemPopupMenu;
import com.example.projetmuseappli.elements.Masterlist;
import com.example.projetmuseappli.elements.Playlist;
import com.example.projetmuseappli.elements.Song;

import java.util.UUID;

public class PlaylistActivity extends AppCompatActivity {

    private PlaylistHeader header;
    private MainFooter footer;

    private UUID uuidOfRefPlaylist;

    private SongAdapter adapter;
    private RecyclerView recyclerView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist);

        recyclerView = findViewById(R.id.playlist_recycler_view);

        Playlist received = getIntent().getSerializableExtra("playlist", Playlist.class);
        if(received == null){
            throw new IllegalStateException("Appel de l'activité playlist avec une playlist null");
        }
        uuidOfRefPlaylist = received.getUuid();

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = getNewAdapter();
        adapter.setItems(Masterlist.getInstance().getPlaylist(uuidOfRefPlaylist).getSongList());
        recyclerView.setAdapter(adapter);


        this.header = new PlaylistHeader(this,uuidOfRefPlaylist, adapter);
        this.footer = new MainFooter(this);

    }

    private SongAdapter getNewAdapter(){
        return new SongAdapter(this
                , Masterlist.getInstance().getPlaylist(uuidOfRefPlaylist).getSongList()
                , new SongAdapter.OnSongClickListener() {
            @Override
            public void onItemClick(Song song) {
                Toast.makeText(PlaylistActivity.this, "TODO:Play", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onBurgerClick(View view, SongAdapter.SongViewHolder holder, Song currentSong) {
                SongItemPopupMenu.showSongItemPopup(view, holder, currentSong);
            }
        });

    }


}
