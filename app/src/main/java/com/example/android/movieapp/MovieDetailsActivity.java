package com.example.android.movieapp;

import android.content.Intent;
import android.graphics.Movie;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.movieapp.model.MovieModel;
import com.squareup.picasso.Picasso;

public class MovieDetailsActivity extends AppCompatActivity {

    TextView movieTitle;
    TextView voteAvg;
    TextView releaseDate;
    TextView plotSynopsis;
    ImageView poster;
    MovieModel movieDetails;

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
        }
    }
}
