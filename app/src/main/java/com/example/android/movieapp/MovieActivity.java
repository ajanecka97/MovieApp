package com.example.android.movieapp;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.movieapp.model.MovieModel;
import com.example.android.movieapp.utils.JSONUtils;
import com.example.android.movieapp.utils.NetworkUtils;
import com.squareup.picasso.Picasso;

import java.net.URL;
import java.util.List;

public class MovieActivity extends AppCompatActivity implements MovieListAdapter.MovieListAdapterOnClickHandler {

    private RecyclerView mRecyclerView;
    private MovieListAdapter mMovieListAdapter;
    private ProgressBar mProgressBar;
    private TextView mErrorMessage;

    private String sortType;

    private static final String SORT_TYPE_KEY = "sort_type";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_movie);

        mRecyclerView = (RecyclerView) findViewById(R.id.rv_movie_list);
        mProgressBar = (ProgressBar) findViewById(R.id.pb_progress_bar);
        mErrorMessage = (TextView) findViewById(R.id.tv_error_message);

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
        loadData(sortType);
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

        //Toast.makeText(this, movie.getMovieTitle(), Toast.LENGTH_LONG).show();

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
                List<MovieModel> movies = JSONUtils.parseMovieJson(s);
                mMovieListAdapter.setMovieData(movies);
                showRecyclerView();
            }else{
                showErrorMessage();
            }
        }
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
        }
        else if(id == R.id.sort_best){
            sortType = NetworkUtils.BEST_MOVIES;
        }
        loadData(sortType);
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString(SORT_TYPE_KEY, sortType);

        super.onSaveInstanceState(outState);
    }
}
