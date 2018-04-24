package com.example.android.movieapp;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.movieapp.model.MovieModel;
import com.example.android.movieapp.model.ReviewModel;
import com.example.android.movieapp.model.VideoModel;
import com.example.android.movieapp.utils.JSONUtils;
import com.example.android.movieapp.utils.NetworkUtils;
import com.squareup.picasso.Picasso;

import java.net.URL;
import java.util.List;

public class MovieDetailsActivity extends AppCompatActivity {

    TextView movieTitle;
    TextView voteAvg;
    TextView releaseDate;
    TextView plotSynopsis;
    ImageView poster;
    MovieModel movieDetails;
    List<VideoModel> videos;
    TextView review;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        movieTitle = (TextView) findViewById(R.id.tv_movie_title);
        voteAvg = (TextView) findViewById(R.id.tv_vote_average);
        releaseDate = (TextView) findViewById(R.id.tv_release_date);
        plotSynopsis = (TextView) findViewById(R.id.tv_plot_synopsis);
        poster = (ImageView) findViewById(R.id.iv_poster2);
        review = (TextView) findViewById(R.id.review);

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
        }
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
            }else{

            }
        }
    }

    public void playTrailer(View view){
        String baseUri = "https://www.youtube.com/watch";
        Uri uri = Uri.parse(baseUri).buildUpon()
                .appendQueryParameter("v", videos.get(0).getKey())
                .build();

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(uri);

        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }

    }
}
