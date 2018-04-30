package com.example.android.movieapp;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v4.app.LoaderManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.movieapp.data.MovieContract;
import com.example.android.movieapp.model.MovieModel;
import com.example.android.movieapp.utils.JSONUtils;
import com.example.android.movieapp.utils.NetworkUtils;
import com.squareup.picasso.Picasso;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MovieActivity extends AppCompatActivity implements MovieListAdapter.MovieListAdapterOnClickHandler
            ,LoaderManager.LoaderCallbacks<Cursor>{

    @BindView(R.id.rv_movie_list) RecyclerView mRecyclerView;
    @BindView(R.id.pb_progress_bar) ProgressBar mProgressBar;
    @BindView(R.id.tv_error_message) TextView mErrorMessage;

    private MovieListAdapter mMovieListAdapter;

    private String sortType;
    private List<MovieModel> movies;
    private Cursor movieCursor;

    private static final int FAVORITE_LOADER_ID = 123;
    private static final String SORT_TYPE_KEY = "sort_type";
    private static final String FAVORITE = "favorite";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_movie);

        ButterKnife.bind(this);

        GridLayoutManager gridManager = new GridLayoutManager(this, 2);
        mRecyclerView.setLayoutManager(gridManager);
        mRecyclerView.setHasFixedSize(true);
        mMovieListAdapter = new MovieListAdapter(getApplicationContext(), this);
        mRecyclerView.setAdapter(mMovieListAdapter);
        if(savedInstanceState != null){
            sortType = savedInstanceState.getString(SORT_TYPE_KEY);
        }else {
            sortType = NetworkUtils.POPULAR_MOVIES;
        }
        if(sortType.equals(NetworkUtils.BEST_MOVIES) ||
                sortType.equals(NetworkUtils.POPULAR_MOVIES)){
            loadData(sortType);
        }

        getSupportLoaderManager().initLoader(FAVORITE_LOADER_ID, null, this);

        mMovieListAdapter.setMovieData(movies);
        showRecyclerView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getSupportLoaderManager().restartLoader(FAVORITE_LOADER_ID, null, this);
    }

    private void loadData(String sortType){
        ConnectivityManager cm =
                (ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        if(isConnected) {
            new LoadMovieDataTask().execute(sortType);
        }else{
            showErrorMessage();
        }
    }

    @Override
    public void onClick(MovieModel movie) {
        Intent startDetailActivityIntent = new Intent(this, MovieDetailsActivity.class);
        startDetailActivityIntent.putExtra("movieIntent", movie);
        startActivity(startDetailActivityIntent);

    }

    public class LoadMovieDataTask extends AsyncTask<String,Void,String>{

        @Override
        protected void onPreExecute() {
            showProgressBar();
        }

        @Override
        protected String doInBackground(String... sortTypes) {
            URL url = NetworkUtils.buildUrl(sortTypes[0]);
            try {
                return NetworkUtils.getResponseFromHttpUrl(url);
            }catch(Exception e){
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String s) {
            if (s != null) {
                movies = JSONUtils.parseMovieJson(s);
                mMovieListAdapter.setMovieData(movies);
                showRecyclerView();
            }else{
                showErrorMessage();
            }
        }
    }

    @Override
    public android.support.v4.content.Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new android.support.v4.content.AsyncTaskLoader<Cursor>(this) {

            Cursor mMovieData;

            @Override
            protected void onStartLoading() {
                if(mMovieData != null){
                    deliverResult(mMovieData);
                }
                else{
                    forceLoad();
                }
            }

            @Override
            public Cursor loadInBackground() {
                try{
                    Cursor cursor = getContentResolver().query(MovieContract.FavoriteEntry.CONTENT_URI,
                            null,
                            null,
                            null,
                            null);
                    return cursor;
                }catch (Exception e){
                    Log.e("loader error", "Failed to load data");
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            public void deliverResult(Cursor data) {
                mMovieData = data;
                super.deliverResult(data);
            }
        };
    }

    @Override
    public void onLoadFinished(android.support.v4.content.Loader<Cursor> loader, Cursor data) {
        movieCursor = data;
        Toast.makeText(this, "Loading finished", Toast.LENGTH_LONG).show();
        if(sortType.equals(FAVORITE)){
            movies = loadDataFromCursor();
            mMovieListAdapter.setMovieData(movies);
        }
    }

    @Override
    public void onLoaderReset(android.support.v4.content.Loader<Cursor> loader) {
        movieCursor = null;
    }

    private void showRecyclerView(){
        mProgressBar.setVisibility(View.INVISIBLE);
        mErrorMessage.setVisibility(View.INVISIBLE);
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    private void showProgressBar(){
        mProgressBar.setVisibility(View.VISIBLE);
        mErrorMessage.setVisibility(View.INVISIBLE);
        mRecyclerView.setVisibility(View.INVISIBLE);
    }

    private void showErrorMessage(){
        mProgressBar.setVisibility(View.INVISIBLE);
        mErrorMessage.setVisibility(View.VISIBLE);
        mRecyclerView.setVisibility(View.INVISIBLE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();

        inflater.inflate(R.menu.movie_app_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == R.id.sort_popular){
            sortType = NetworkUtils.POPULAR_MOVIES;
            loadData(sortType);
        }
        else if(id == R.id.sort_best){
            sortType = NetworkUtils.BEST_MOVIES;
            loadData(sortType);
        }
        else if(id == R.id.sort_favorite) {
            sortType = FAVORITE;

            getSupportLoaderManager().restartLoader(FAVORITE_LOADER_ID, null, this);
            this.movies = loadDataFromCursor();
            mMovieListAdapter.setMovieData(movies);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString(SORT_TYPE_KEY, sortType);

        super.onSaveInstanceState(outState);
    }

    private List<MovieModel> loadDataFromCursor(){
        List<MovieModel> movies = new ArrayList<>();
        for (movieCursor.moveToFirst(); !movieCursor.isAfterLast(); movieCursor.moveToNext()) {
            MovieModel movie = new MovieModel();
            movie.setMovieID(movieCursor.getInt(movieCursor.getColumnIndex(MovieContract.FavoriteEntry.COLUMN_ID)));
            movie.setMovieTitle(movieCursor.getString(movieCursor.getColumnIndex(MovieContract.FavoriteEntry.COLUMN_TITLE)));
            movie.setFullPosterPath(movieCursor.getString(movieCursor.getColumnIndex(MovieContract.FavoriteEntry.COLUMN_POSTER)));
            movie.setOverwiew(movieCursor.getString(movieCursor.getColumnIndex(MovieContract.FavoriteEntry.COLUMN_SYNOPSIS)));
            movie.setReleaseDate(movieCursor.getString(movieCursor.getColumnIndex(MovieContract.FavoriteEntry.COLUMN_RELEASE_DATE)));
            movie.setVoteAvg(movieCursor.getDouble(movieCursor.getColumnIndex(MovieContract.FavoriteEntry.COLUMN_RATING)));
            movies.add(movie);
        }
        return movies;
    }

}
