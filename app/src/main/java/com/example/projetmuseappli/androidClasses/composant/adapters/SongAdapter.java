package com.example.projetmuseappli.androidClasses.composant.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.OnBackPressedDispatcherOwner;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projetmuseappli.R;
import com.example.projetmuseappli.elements.Song;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Adaptateur pour la {@link RecyclerView} affichant la liste des morceaux de musique (objets {@link Song}).
 * Gère l'affichage des titres, extensions, dates d'import, les clics et le mode sélection multiple.
 */
public class SongAdapter extends RecyclerView.Adapter<SongAdapter.SongViewHolder>
        implements MultiSelectAdapterInterface<Song> {

    /**
     * Interface définissant un listener regroupant les méthodes à fournir à l'adaptateur pour gérer les clics.
     */
    public interface OnSongClickListener {
        /**
         * Événement de gestion du clic sur le ViewHolder d'un morceau.
         *
         * @param song Le morceau sur lequel l'utilisateur a cliqué.
         */
        void onItemClick(Song song);

        /**
         * Événement de gestion du clic sur le bouton burger du ViewHolder d'un morceau.
         *
         * @param view La vue sur laquelle le menu contextuel doit s'ancrer.
         * @param holder Le ViewHolder contenant le morceau.
         * @param currentSong Le morceau sélectionné.
         */
        void onBurgerClick(View view, SongViewHolder holder, Song currentSong);

        /**
         * Événement de gestion du clic sur le bouton d'actions sur l'ensemble des morceaux sélectionnés.
         *
         * @param view La vue ancre (ex: bouton flottant).
         * @param selectedSongs L'ensemble des morceaux actuellement sélectionnés.
         */
        void onMultipleSongsBurgerClick(View view, Set<Song> selectedSongs);
    }

    /*Liste des sons à afficher*/
    private List<Song> songList;
    private Context appContext;
    private OnSongClickListener listener;

    private final Set<Song> selectedSongs = new HashSet<>();
    private View floatingActionButton;
    private OnBackPressedCallback onBackPressedCallback;

    /**
     * Construit un adaptateur de morceaux de musique.
     *
     * @param appContext Le contexte de l'application.
     * @param songList La liste initiale de morceaux à afficher.
     * @param listener L'écouteur d'événements de clic sur les éléments.
     */
    public SongAdapter(Context appContext, List<Song> songList, OnSongClickListener listener) {
        this.appContext = appContext;
        this.songList = songList;
        this.listener = listener;
    }

    @Override
    public boolean isSelectionMode() {
        return !selectedSongs.isEmpty();
    }

    @Override
    public Set<Song> getSelectedItems() {
        return getSelectedSongs();
    }

    public Set<Song> getSelectedSongs() {
        return new HashSet<>(selectedSongs);
    }

    @Override
    public void toggleSelection(Song song) {
        if (selectedSongs.contains(song)) {
            selectedSongs.remove(song);
        } else {
            selectedSongs.add(song);
        }
        updateSelectionState();
    }

    @Override
    public void clearSelection() {
        selectedSongs.clear();
        updateSelectionState();
    }

    @Override
    public void setFloatingActionButton(View fab) {
        this.floatingActionButton = fab;
        if (fab != null) {
            fab.setOnClickListener(v -> {
                if (listener != null && isSelectionMode()) {
                    listener.onMultipleSongsBurgerClick(v, getSelectedSongs());
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
     * Crée et gonfle le layout d'item pour instancier un {@link SongViewHolder}.
     *
     * @param parent Le ViewGroup parent.
     * @param viewType Le type de vue.
     * @return Une nouvelle instance de {@link SongViewHolder}.
     */
    @NonNull
    @Override
    public SongViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.item_song, parent, false);
        return new SongViewHolder(itemView);
    }

    /**
     * Attribue les données d'un morceau aux vues d'un {@link SongViewHolder}.
     *
     * @param holder Le ViewHolder à mettre à jour.
     * @param position La position du morceau dans la liste.
     */
    @Override
    public void onBindViewHolder(@NonNull SongViewHolder holder, int position) {
        Song currentSong = songList.get(position);
        boolean isSelected = selectedSongs.contains(currentSong);

        holder.textViewTitle.setText(currentSong.getTitre());

        holder.textViewExtension.setText(
                String.format(appContext.getString(R.string.item_card_extension_format)
                        , currentSong.getExtension())
        );

        holder.textViewDate.setText(
                String.format(appContext.getString(R.string.item_card_date_format)
                        , currentSong.getDate())
        );

        holder.itemView.setSelected(isSelected);
        holder.itemView.setActivated(isSelected);
        applySelectionFilter(holder.containerLayout, isSelected);

        /*Gestion des actions*/
        holder.itemView.setOnClickListener(v -> {
            if (isSelectionMode()) {
                toggleSelection(currentSong);
            } else if (listener != null) {
                this.listener.onItemClick(currentSong);
            }
        });

        holder.itemView.setOnLongClickListener(v -> {
            toggleSelection(currentSong);
            return true;
        });

        holder.buttonBurger.setOnClickListener(view -> {
            if (isSelectionMode()) {
                toggleSelection(currentSong);
            } else if (listener != null) {
                this.listener.onBurgerClick(view, holder, currentSong);
            }
        });
    }

    /**
     * Retourne le nombre total d'éléments gérés par l'adaptateur.
     *
     * @return Le nombre de morceaux dans la liste.
     */
    @Override
    public int getItemCount() {
        return songList != null ? songList.size() : 0;
    }

    /**
     * Retourne la liste des morceaux associés à l'adaptateur.
     *
     * @return La liste des objets {@link Song}.
     */
    @Override
    public List<Song> getListOfAdapter() {
        return this.songList;
    }

    /**
     * Met à jour la liste des morceaux et rafraîchit l'affichage.
     *
     * @param newItems La nouvelle liste de morceaux.
     */
    @Override
    public void setItems(List<Song> newItems) {
        this.songList = newItems;
        if (newItems != null) {
            selectedSongs.retainAll(newItems);
        } else {
            selectedSongs.clear();
        }
        updateSelectionState();
    }

    /**
     * Retourne le contexte applicatif.
     *
     * @return Le {@link Context}.
     */
    @Override
    public Context getContext() {
        return this.appContext;
    }

    /**
     * ViewHolder représentant un item de morceau de musique.
     */
    public class SongViewHolder extends RecyclerView.ViewHolder {
        TextView textViewTitle;
        TextView textViewExtension;
        TextView textViewDate;
        View buttonBurger;
        View containerLayout;

        /**
         * Constructeur du ViewHolder effectuant la liaison avec les vues du layout.
         *
         * @param itemView La vue racine de l'item.
         */
        public SongViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewTitle = itemView.findViewById(R.id.textViewTitle);
            textViewExtension = itemView.findViewById(R.id.textViewExtension);
            textViewDate = itemView.findViewById(R.id.textViewDate);
            buttonBurger = itemView.findViewById(R.id.buttonBurger);
            if (itemView instanceof ViewGroup && ((ViewGroup) itemView).getChildCount() > 0) {
                containerLayout = ((ViewGroup) itemView).getChildAt(0);
            } else {
                containerLayout = itemView;
            }
        }

    }

}
