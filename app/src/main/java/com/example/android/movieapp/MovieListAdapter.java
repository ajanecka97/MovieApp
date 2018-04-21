package com.example.android.movieapp;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.movieapp.model.MovieModel;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by szjan on 20.03.2018.
 */

public class MovieListAdapter extends RecyclerView.Adapter<MovieListAdapter.MovieListAdapterViewHolder>{

    private List<MovieModel> movieData;
    private Context context;

    private final MovieListAdapterOnClickHandler movieClickHandler;

    public interface MovieListAdapterOnClickHandler{
        void onClick(MovieModel movie);
    }

    public MovieListAdapter(Context context, MovieListAdapterOnClickHandler clickHandler){
        this.context = context;
        this.movieClickHandler = clickHandler;
    }



    public class MovieListAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public final ImageView poster;

        public MovieListAdapterViewHolder(View view){
            super(view);
            poster = (ImageView) view.findViewById(R.id.iv_poster);

            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            MovieModel movie = movieData.get(adapterPosition);
            movieClickHandler.onClick(movie);
        }
    }

    @Override
    public MovieListAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.recycler_view_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);
        return new MovieListAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MovieListAdapterViewHolder movieListAdapterViewHolder, int position) {
        String moviePoster = movieData.get(position).getPosterPath();
        Picasso.with(context)
                .load(moviePoster)
                .into(movieListAdapterViewHolder.poster);
    }

    @Override
    public int getItemCount() {
        if(movieData == null) return 0;
        return movieData.size();
    }

    public void setMovieData(List<MovieModel> movieData){
        this.movieData = movieData;
        notifyDataSetChanged();
    }
}
