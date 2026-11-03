package com.example.projetmuseappli.androidClasses.composant.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.OnBackPressedDispatcherOwner;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projetmuseappli.R;
import com.example.projetmuseappli.elements.Playlist;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Adaptateur de {@link RecyclerView} pour l'affichage complet des playlists.
 * Gère l'affichage, les clics et le mode sélection multiple.
 */
public class PlaylistAdapter extends PlaylistAdapterAbstract<PlaylistAdapter.PlaylistViewHolder>
        implements MultiSelectAdapterInterface<Playlist> {

    /**
     * Interface de gestion des événements utilisateur (clic et menu d'options) sur une playlist.
     */
    public interface OnPlaylistClickListener {
        /**
         * Événement de gestion du clic principal sur l'élément d'une playlist.
         *
         * @param playlist La playlist sélectionnée.
         */
        void onItemClick(Playlist playlist);

        /**
         * Événement de gestion du clic sur le bouton menu ("burger") d'une playlist.
         *
         * @param view La vue ancre pour l'affichage du menu contextuel.
         * @param viewHolder Le ViewHolder de l'élément sélectionné.
         * @param playlist La playlist sélectionnée.
         */
        void onBurgerClick(View view, PlaylistViewHolder viewHolder, Playlist playlist);

        /**
         * Événement de gestion du clic sur le bouton d'actions sur l'ensemble des playlists sélectionnées.
         *
         * @param view La vue ancre (ex: bouton flottant).
         * @param selectedPlaylists L'ensemble des playlists actuellement sélectionnées.
         */
        void onMultiplePlaylistsBurgerClick(View view, Set<Playlist> selectedPlaylists);
    }

    private OnPlaylistClickListener listener;

    private final Set<Playlist> selectedPlaylists = new HashSet<>();
    private View floatingActionButton;
    private OnBackPressedCallback onBackPressedCallback;

    /** Identifiant de layout pour un item de playlist. */
    public static final int ITEM_LAYOUT_ID = R.layout.item_playlist;

    /**
     * Construit un adaptateur de playlists.
     *
     * @param appContext Le contexte de l'application.
     * @param allPlaylist La liste initiale de playlists à afficher.
     * @param listener L'écouteur des interactions utilisateur.
     */
    public PlaylistAdapter(Context appContext, List<Playlist> allPlaylist, OnPlaylistClickListener listener) {
        super(appContext, allPlaylist);
        this.listener = listener;
    }

    @Override
    public boolean isSelectionMode() {
        return !selectedPlaylists.isEmpty();
    }

    @Override
    public Set<Playlist> getSelectedItems() {
        return getSelectedPlaylists();
    }

    public Set<Playlist> getSelectedPlaylists() {
        return new HashSet<>(selectedPlaylists);
    }

    @Override
    public void toggleSelection(Playlist playlist) {
        if (selectedPlaylists.contains(playlist)) {
            selectedPlaylists.remove(playlist);
        } else {
            selectedPlaylists.add(playlist);
        }
        updateSelectionState();
    }

    @Override
    public void clearSelection() {
        selectedPlaylists.clear();
        updateSelectionState();
    }

    @Override
    public void setFloatingActionButton(View fab) {
        this.floatingActionButton = fab;
        if (fab != null) {
            fab.setOnClickListener(v -> {
                if (listener != null && isSelectionMode()) {
                    listener.onMultiplePlaylistsBurgerClick(v, getSelectedPlaylists());
                }
            });
            fab.setVisibility(isSelectionMode() ? View.VISIBLE : View.GONE);
        }
    }

    private void updateSelectionState() {
        boolean active = isSelectionMode();
        if (floatingActionButton != null) {
            floatingActionButton.setVisibility(active ? View.VISIBLE : View.GONE);
        }
        if (onBackPressedCallback != null) {
            onBackPressedCallback.setEnabled(active);
        }
        notifyDataSetChanged();
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        if (floatingActionButton == null && recyclerView.getRootView() != null) {
            View fab = recyclerView.getRootView().findViewById(R.id.floatingActionButtonMultiSelect);
            if (fab != null) {
                setFloatingActionButton(fab);
            }
        }
        Context context = recyclerView.getContext();
        if (context instanceof OnBackPressedDispatcherOwner) {
            OnBackPressedDispatcherOwner owner = (OnBackPressedDispatcherOwner) context;
            if (onBackPressedCallback == null) {
                onBackPressedCallback = new OnBackPressedCallback(false) {
                    @Override
                    public void handleOnBackPressed() {
                        clearSelection();
                    }
                };
                owner.getOnBackPressedDispatcher().addCallback(onBackPressedCallback);
            }
        }
    }

    @Override
    public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        if (onBackPressedCallback != null) {
            onBackPressedCallback.remove();
            onBackPressedCallback = null;
        }
    }

    /**
     * Crée et initialise un nouveau {@link PlaylistViewHolder}.
     *
     * @param parent Le ViewGroup parent.
     * @param viewType Le type de vue.
     * @return Une nouvelle instance de {@link PlaylistViewHolder}.
     */
    @NonNull
    @Override
    public PlaylistViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new PlaylistViewHolder(this.getInflatedItemView(parent, viewType,ITEM_LAYOUT_ID));
    }

    /**
     * Lie les données de la playlist à la position donnée au ViewHolder spécifié.
     *
     * @param holder Le ViewHolder à mettre à jour.
     * @param position La position dans la liste.
     */
    @Override
    public void onBindViewHolder(@NonNull PlaylistViewHolder holder, int position) {
        Playlist currentPlaylist = this.playlistList.get(position);
        boolean isSelected = selectedPlaylists.contains(currentPlaylist);

        holder.date.setText(
                String.format(appContext.getString(R.string.item_card_date_creation_format)
                        ,currentPlaylist.getDate())
        );

        holder.size.setText(
                String.format(appContext.getString(R.string.item_card_playlist_size_format)
                        ,Integer.toString(currentPlaylist.getSize()))
        );

        holder.title.setText(currentPlaylist.getName());

        holder.itemView.setSelected(isSelected);
        holder.itemView.setActivated(isSelected);
        holder.itemView.setAlpha(isSelected ? 0.5f : 1.0f);

        /*gestion des actions*/
        holder.itemView.setOnClickListener(view -> {
            if (isSelectionMode()) {
                toggleSelection(currentPlaylist);
            } else if (listener != null) {
                this.listener.onItemClick(currentPlaylist);
            }
        });

        holder.itemView.setOnLongClickListener(view -> {
            toggleSelection(currentPlaylist);
            return true;
        });

        holder.burgerButton.setOnClickListener(view -> {
            if (isSelectionMode()) {
                toggleSelection(currentPlaylist);
            } else if (listener != null) {
                this.listener.onBurgerClick(view,holder,currentPlaylist);
            }
        });
    }

    @Override
    public void setItems(List<Playlist> newItems) {
        super.setItems(newItems);
        if (newItems != null) {
            selectedPlaylists.retainAll(newItems);
        } else {
            selectedPlaylists.clear();
        }
        updateSelectionState();
    }

    /**
     * ViewHolder représentant la vue d'un item de playlist dans la RecyclerView.
     */
    public static class PlaylistViewHolder extends PlaylistViewHolderAbstract{
        private TextView title;
        private TextView date;
        private TextView size;
        private ImageButton burgerButton;

        /**
         * Constructeur du ViewHolder effectuant la liaison avec les vues du layout.
         *
         * @param itemView La vue racine de l'item.
         */
        public PlaylistViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.playlistTitle);
            size = itemView.findViewById(R.id.playlistSize);
            date = itemView.findViewById(R.id.playlistDate);
            burgerButton = itemView.findViewById(R.id.buttonBurgerPlaylist);
        }

    }

}
