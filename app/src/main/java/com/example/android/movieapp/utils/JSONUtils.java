package com.example.android.movieapp.utils;

import com.example.android.movieapp.model.MovieModel;
import com.example.android.movieapp.model.ReviewModel;
import com.example.android.movieapp.model.VideoModel;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by szjan on 20.03.2018.
 */

public class JSONUtils {

    public static List<MovieModel> parseMovieJson(String movieJson){
        List<MovieModel> moviesList = new ArrayList<>();
        try{
            JSONObject parsedJson = new JSONObject(movieJson);
            JSONArray moviesJsonArray = parsedJson.getJSONArray("results");
            if(moviesJsonArray != null) {
                for (int i = 0; i < moviesJsonArray.length(); i++) {
                    MovieModel movie = JSONUtils.parseMovieDetailsJson(moviesJsonArray.getJSONObject(i));
                    moviesList.add(movie);
                }
            }
        }
        catch (JSONException e){
            e.printStackTrace();
        }
        return moviesList;
    }


    private static MovieModel parseMovieDetailsJson(JSONObject movieJson){
        MovieModel movie = new MovieModel();
        try {
            int voteCount = movieJson.getInt("vote_count");
            int movieId = movieJson.getInt("id");
            boolean video = movieJson.getBoolean("video");
            double voteAvg = movieJson.getDouble("vote_average");
            String title = movieJson.getString("title");
            double popularity = movieJson.getDouble("popularity");
            String posterPath = movieJson.getString("poster_path");
            String originalLanguage = movieJson.getString("original_language");
            String originalTitle = movieJson.getString("original_title");
            JSONArray genreIDs = movieJson.getJSONArray("genre_ids");
            String backdropPath = movieJson.getString("backdrop_path");
            boolean adult = movieJson.getBoolean("adult");
            String overview = movieJson.getString("overview");
            String releaseDate = movieJson.getString("release_date");

            List<Integer> genreIDList = new ArrayList<>();
            if(genreIDs != null){
                for(int i = 0; i < genreIDs.length(); i++){
                    genreIDList.add(genreIDs.getInt(i));
                }
            }

            movie.setVoteCount(voteCount);
            movie.setMovieID(movieId);
            movie.setVideo(video);
            movie.setVoteAvg(voteAvg);
            movie.setMovieTitle(title);
            movie.setPopularity(popularity);
            movie.setPosterPath(posterPath);
            movie.setOriginalLanguage(originalLanguage);
            movie.setOriginalTitle(originalTitle);
            movie.setGenreIds(genreIDList);
            movie.setBackdropPath(backdropPath);
            movie.setAdult(adult);
            movie.setOverwiew(overview);
            movie.setReleaseDate(releaseDate);
        }
        catch(JSONException e){
            e.printStackTrace();
        }
        return movie;
    }

    public static List<ReviewModel> parseReviewJson(String reviewJson){
        List<ReviewModel> reviewList = new ArrayList<>();
        try{
            JSONObject parsedJson = new JSONObject(reviewJson);
            JSONArray reviewJsonArray = parsedJson.getJSONArray("results");
            if(reviewJsonArray != null) {
                for (int i = 0; i < reviewJsonArray.length(); i++) {
                    ReviewModel review = JSONUtils.parseReviewDetailsJson(reviewJsonArray.getJSONObject(i));
                    reviewList.add(review);
                }
            }
        }
        catch (JSONException e){
            e.printStackTrace();
        }
        return reviewList;
    }

    private static ReviewModel parseReviewDetailsJson(JSONObject json){
        ReviewModel reviewModel = new ReviewModel();
        try{
            String author = json.getString("author");
            String content = json.getString("content");

            reviewModel.setAuthor(author);
            reviewModel.setContent(content);
        } catch (JSONException e){
            e.printStackTrace();
        }
        return reviewModel;
    }

    public static List<VideoModel> parseVideoJson(String videoJson){
        List<VideoModel> videoList = new ArrayList<>();
        try{
            JSONObject parsedJson = new JSONObject(videoJson);
            JSONArray videoJsonArray = parsedJson.getJSONArray("results");
            if(videoJsonArray != null) {
                for (int i = 0; i < videoJsonArray.length(); i++) {
                    VideoModel video = JSONUtils.parseVideoDetailsJson(videoJsonArray.getJSONObject(i));
                    videoList.add(video);
                }
            }
        }
        catch (JSONException e){
            e.printStackTrace();
        }
        return videoList;
    }

    private static VideoModel parseVideoDetailsJson(JSONObject json){
        VideoModel videoModel = new VideoModel();
        try{
            String key = json.getString("key");
            String name = json.getString("name");
            String site = json.getString("site");
            String type = json.getString("type");

            videoModel.setKey(key);
            videoModel.setName(name);
            videoModel.setSite(site);
            videoModel.setType(type);
        } catch (JSONException e){
            e.printStackTrace();
        }

        return videoModel;
    }
}
