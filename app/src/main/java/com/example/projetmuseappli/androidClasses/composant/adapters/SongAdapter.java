package com.example.projetmuseappli.androidClasses.composant.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projetmuseappli.R;
import com.example.projetmuseappli.elements.Song;

import java.util.List;

/**
 * Adaptateur pour la {@link RecyclerView} affichant la liste des morceaux de musique (objets {@link Song}).
 * Gère l'affichage des titres, extensions, dates d'import et les clics utilisateur.
 */
public class SongAdapter extends RecyclerView.Adapter<SongAdapter.SongViewHolder>
        implements MediaRecyclerViewAdapterInterface<Song> {

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
    }

    /*Liste des sons à afficher*/
    private List<Song> songList;
    private Context appContext;
    private OnSongClickListener listener;

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
        /*Definir les valeurs d'affichage*/
        Song currentSong = songList.get(position);

        holder.textViewTitle.setText(currentSong.getTitre());

        holder.textViewExtension.setText(
                String.format(appContext.getString(R.string.item_card_extension_format)
                        , currentSong.getExtension())
        );

        holder.textViewDate.setText(
                String.format(appContext.getString(R.string.item_card_date_format)
                        , currentSong.getDate())
        );

        /*Gestion des actions*/
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                this.listener.onItemClick(currentSong);
            }
        });

        holder.buttonBurger.setOnClickListener(view -> {
            if (listener != null) {
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
     * Met à jour la liste des morceaux et raffraîchit l'affichage.
     *
     * @param newItems La nouvelle liste de morceaux.
     */
    @Override
    public void setItems(List<Song> newItems) {
        this.songList = newItems;
        notifyDataSetChanged();
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

        /**
         * Constructeur du ViewHolder effectuant la liaison avec les vues du layout.
         *
         * @param itemView La vue racine de l'item.
         */
        public SongViewHolder(@NonNull View itemView) {
            super(itemView);
            // Liaison des éléments graphiques du layout item_song.xml par leur ID
            textViewTitle = itemView.findViewById(R.id.textViewTitle);
            textViewExtension = itemView.findViewById(R.id.textViewExtension);
            textViewDate = itemView.findViewById(R.id.textViewDate);
            buttonBurger = itemView.findViewById(R.id.buttonBurger);
        }

    }

}
