package com.example.projetmuseappli.androidClasses.activities;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projetmuseappli.R;
import com.example.projetmuseappli.androidClasses.composant.adapters.SongAdapter;
import com.example.projetmuseappli.androidClasses.composant.headersAndFooters.MainFooter;
import com.example.projetmuseappli.androidClasses.composant.headersAndFooters.PlaylistHeader;
import com.example.projetmuseappli.androidClasses.utilitaries.SongSortHelper.SortMode;
import com.example.projetmuseappli.androidClasses.overlays.SongItemPopupMenu;
import com.example.projetmuseappli.androidClasses.services.PlayerManager;
import com.example.projetmuseappli.elements.Masterlist;
import com.example.projetmuseappli.elements.Playlist;
import com.example.projetmuseappli.elements.Song;

import java.util.Set;
import java.util.UUID;

/**
 * Activité affichant le contenu détaillé d'une playlist sous forme de liste de morceaux.
 */
public class PlaylistActivity extends AppCompatActivity {

    private PlaylistHeader header;
    private MainFooter footer;

    private UUID uuidOfRefPlaylist;

    private SongAdapter adapter;
    private RecyclerView recyclerView;

    /**
     * Initialise l'activité, récupère la playlist passée en paramètre d'intent
     * et configure le RecyclerView ainsi que le header et le footer.
     *
     * @param savedInstanceState État sauvegardé de l'activité, le cas échéant.
     */
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

        this.header = new PlaylistHeader(this, uuidOfRefPlaylist, adapter);
        this.header.setPlaylistAdapter(adapter);
        this.header.setOnSortModeChangedListener(newMode -> updateDragAndDropState());
        this.footer = new MainFooter(this);

        setupItemTouchHelper();
        updateDragAndDropState();
    }

    private void setupItemTouchHelper() {
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(
                ItemTouchHelper.UP | ItemTouchHelper.DOWN, 0) {

            @Override
            public boolean isLongPressDragEnabled() {
                return header != null
                        && header.getCurrentSortMode() == SortMode.CUSTOM
                        && !header.isSearchActive()
                        && adapter != null
                        && !adapter.isSelectionMode();
            }

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView,
                                  @NonNull RecyclerView.ViewHolder viewHolder,
                                  @NonNull RecyclerView.ViewHolder target) {
                int fromPosition = viewHolder.getBindingAdapterPosition();
                int toPosition = target.getBindingAdapterPosition();

                if (fromPosition == RecyclerView.NO_POSITION || toPosition == RecyclerView.NO_POSITION
                        || fromPosition == toPosition) {
                    return false;
                }

                adapter.moveItem(fromPosition, toPosition);
                Masterlist.getInstance().moveSongInPlaylist(uuidOfRefPlaylist, fromPosition, toPosition);
                return true;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                // Rien à faire pour le swipe
            }
        });

        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    private void updateDragAndDropState() {
        if (adapter != null && header != null) {
            boolean isCustomMode = header.getCurrentSortMode() == SortMode.CUSTOM && !header.isSearchActive();
            adapter.setLongClickSelectionEnabled(!isCustomMode);
        }
    }

    private SongAdapter getNewAdapter(){
        return new SongAdapter(this
                , Masterlist.getInstance().getPlaylist(uuidOfRefPlaylist).getSongList()
                , new SongAdapter.OnSongClickListener() {
            @Override
            public void onItemClick(Song song) {
                PlayerManager playerManager = PlayerManager.getInstance(PlaylistActivity.this);
                if (playerManager.isPlayingSong(song)) {
                    return;
                }
                playerManager.playSongFromList(
                        PlaylistActivity.this,
                        Masterlist.getInstance().getPlaylist(uuidOfRefPlaylist).getSongList(),
                        song);
            }

            @Override
            public void onBurgerClick(View view, SongAdapter.SongViewHolder holder, Song currentSong) {
                SongItemPopupMenu.showSongItemPopup(view, holder, currentSong);
            }

            @Override
            public void onMultipleSongsBurgerClick(View view, Set<Song> selectedSongs) {
                SongItemPopupMenu.showMultipleSongsItemPopup(view, adapter, selectedSongs);
            }
        });

    }

}
