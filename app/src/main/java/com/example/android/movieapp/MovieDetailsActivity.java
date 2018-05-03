package com.example.android.movieapp;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.movieapp.data.MovieContract;
import com.example.android.movieapp.data.MovieDbHelper;
import com.example.android.movieapp.model.MovieModel;
import com.example.android.movieapp.model.ReviewModel;
import com.example.android.movieapp.model.VideoModel;
import com.example.android.movieapp.utils.JSONUtils;
import com.example.android.movieapp.utils.NetworkUtils;
import com.squareup.picasso.Picasso;

import java.net.URL;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MovieDetailsActivity extends AppCompatActivity implements TrailerListAdapter.TrailerListAdapterOnClickHandler{

    @BindView(R.id.tv_movie_title) TextView movieTitle;
    @BindView(R.id.tv_vote_average) TextView voteAvg;
    @BindView(R.id.tv_release_date) TextView releaseDate;
    @BindView(R.id.tv_plot_synopsis) TextView plotSynopsis;
    @BindView(R.id.iv_poster2) ImageView poster;
    @BindView(R.id.review) TextView review;
    @BindView(R.id.like_button) Button likeButton;
    @BindView(R.id.unlike_button) Button unlikeButton;
    @BindView(R.id.rv_trailer_list) RecyclerView trailerRecyclerView;
    @BindView(R.id.tv_trailers) TextView trailers;
    @BindView(R.id.scroll_view) ScrollView scrollView;

    private TrailerListAdapter trailerListAdapter;
    private int scrollPositionX = -1;
    private int scrollPositionY = -1;

    MovieModel movieDetails;
    List<VideoModel> videos;

    private static final String SCROLL_POSITION_X_KEY = "scroll_position_x";
    private static final String SCROLL_POSITION_Y_KEY = "scroll_position_y";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);
        ButterKnife.bind(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this,
                LinearLayoutManager.HORIZONTAL, false);
        linearLayoutManager.setMeasurementCacheEnabled(false);
        trailerRecyclerView.setLayoutManager(linearLayoutManager);
        trailerRecyclerView.setHasFixedSize(true);
        trailerListAdapter = new TrailerListAdapter(getApplicationContext(), this);
        trailerRecyclerView.setAdapter(trailerListAdapter);

        Intent intentThatStartedThisActivity = getIntent();
        if(intentThatStartedThisActivity.hasExtra("movieIntent")){
            movieDetails = intentThatStartedThisActivity.getParcelableExtra("movieIntent");
            movieTitle.setText(movieDetails.getMovieTitle());
            voteAvg.setText(Double.toString(movieDetails.getVoteAvg()));
            releaseDate.setText(movieDetails.getReleaseDate());
            plotSynopsis.setText(movieDetails.getOverwiew());
            Picasso.with(this)
                    .load(movieDetails.getPosterPath())
                    .into(poster);
            loadData();
            if(CheckIsDataAlreadyInDBorNot(MovieContract.FavoriteEntry.TABLE_NAME, MovieContract.FavoriteEntry.COLUMN_ID, String.valueOf(movieDetails.getMovieID()))){
                likeButton.setVisibility(View.GONE);
                unlikeButton.setVisibility(View.VISIBLE);
            }
            else {
                likeButton.setVisibility(View.VISIBLE);
                unlikeButton.setVisibility(View.GONE);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void loadData(){
        ConnectivityManager cm =
                (ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        if(isConnected) {
            String[] urls = new String[2];
            urls[0] = "http://api.themoviedb.org/3/movie/"
                    + movieDetails.getMovieID() + "/reviews";
            urls[1] = "http://api.themoviedb.org/3/movie/"
                    + movieDetails.getMovieID() + "/videos";
            new LoadMovieDataTask().execute(urls);
        }else{
        }
    }

    public class LoadMovieDataTask extends AsyncTask<String,Void,String[]> {

        @Override
        protected String[] doInBackground(String... urls) {
            URL reviewUrl = NetworkUtils.buildUrl(urls[0]);
            URL videoUrl = NetworkUtils.buildUrl(urls[1]);
            try {
                String[] jsons = new String[2];
                jsons[0] = NetworkUtils.getResponseFromHttpUrl(reviewUrl);
                jsons[1] = NetworkUtils.getResponseFromHttpUrl(videoUrl);
                return jsons;
            }catch(Exception e){
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String[] s) {
            if (s != null) {
                List<ReviewModel> reviewDetails = JSONUtils.parseReviewJson(s[0]);
                for(ReviewModel reviewModel : reviewDetails){
                    review.append(reviewModel.getAuthor() + ":\n\n");
                    review.append(reviewModel.getContent() + "\n\n\n");
                }
                videos = JSONUtils.parseVideoJson(s[1]);
                trailerListAdapter.setTrailerData(videos);
                scroll();
            }else{

            }
        }
    }

    @Override
    public void onClick(VideoModel trailer) {
        String baseUri = "https://www.youtube.com/watch";
        Uri uri = Uri.parse(baseUri).buildUpon()
                .appendQueryParameter("v", trailer.getKey())
                .build();

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(uri);

        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    public void likeMovie(View view){
        ContentValues contentValues = new ContentValues();

        contentValues.put(MovieContract.FavoriteEntry.COLUMN_ID, movieDetails.getMovieID());
        contentValues.put(MovieContract.FavoriteEntry.COLUMN_TITLE, movieDetails.getMovieTitle());
        contentValues.put(MovieContract.FavoriteEntry.COLUMN_POSTER, movieDetails.getPosterPath());
        contentValues.put(MovieContract.FavoriteEntry.COLUMN_RATING, movieDetails.getVoteAvg());
        contentValues.put(MovieContract.FavoriteEntry.COLUMN_RELEASE_DATE, movieDetails.getReleaseDate());
        contentValues.put(MovieContract.FavoriteEntry.COLUMN_SYNOPSIS, movieDetails.getOverwiew());

        Uri uri = getContentResolver().insert(MovieContract.FavoriteEntry.CONTENT_URI, contentValues);
        if(uri != null){
            Toast.makeText(this, "Movie was added to " + uri, Toast.LENGTH_SHORT).show();
        }
        likeButton.setVisibility(View.GONE);
        unlikeButton.setVisibility(View.VISIBLE);
    }

    public void unlikeMovie(View view){
        String selection = MovieContract.FavoriteEntry.COLUMN_ID + "=?";
        String movieID = String.valueOf(movieDetails.getMovieID());
        String[] selectionArgs = new String[]{movieID};

        int rowsDeleted = getContentResolver().delete(MovieContract.FavoriteEntry.CONTENT_URI,
                selection, selectionArgs);

        Toast.makeText(this, "Rows deleted: " + rowsDeleted, Toast.LENGTH_SHORT).show();
        likeButton.setVisibility(View.VISIBLE);
        unlikeButton.setVisibility(View.GONE);
    }

    boolean CheckIsDataAlreadyInDBorNot(String TableName, String dbfield, String fieldValue) {
        MovieDbHelper movieDbHelper = new MovieDbHelper(this);
        SQLiteDatabase sqldb = movieDbHelper.getReadableDatabase();
        String Query = "Select * from " + TableName + " where " + dbfield + " = " + fieldValue;
        Cursor cursor = sqldb.rawQuery(Query, null);
        if(cursor.getCount() <= 0){
            cursor.close();
            return false;
        }
        cursor.close();
        return true;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        scrollPositionX = scrollView.getScrollX();
        scrollPositionY = scrollView.getScrollY();

        outState.putInt(SCROLL_POSITION_X_KEY, scrollPositionX);
        outState.putInt(SCROLL_POSITION_Y_KEY, scrollPositionY);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        scrollPositionX = savedInstanceState.getInt(SCROLL_POSITION_X_KEY);
        scrollPositionY = savedInstanceState.getInt(SCROLL_POSITION_Y_KEY);
        scroll();
    }

    private void scroll(){
        if (scrollPositionX != 0 || scrollPositionY != 0) {
            scrollView.post(new Runnable() {
                @Override
                public void run() {
                    scrollView.scrollTo(scrollPositionX, scrollPositionY);
                }
            });
        }
    }
}
