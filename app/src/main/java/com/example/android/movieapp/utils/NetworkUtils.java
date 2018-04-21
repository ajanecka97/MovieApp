package com.example.android.movieapp.utils;

import android.net.Uri;

import com.example.android.movieapp.BuildConfig;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

/**
 * Created by szjan on 20.03.2018.
 */

public class NetworkUtils {
    public static final String POPULAR_MOVIES = "http://api.themoviedb.org/3/movie/popular";
    public static final String BEST_MOVIES= "http://api.themoviedb.org/3/movie/top_rated";

    private static final String API_KEY_PARAM = "api_key";

    private static final String apiKey = BuildConfig.API_KEY;

    private static String baseUrl;

    public static URL buildUrl(String sortType){

        baseUrl = sortType;

        Uri buildtUri = Uri.parse(baseUrl).buildUpon()
                .appendQueryParameter(API_KEY_PARAM, apiKey)
                .build();

        URL url = null;

        try{
            url = new URL(buildtUri.toString());
        }
        catch(MalformedURLException e){
            e.printStackTrace();
        }
        return url;
    }

// Method below was taken from project Sunshine as it has no major impact on the task given to me

    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }


}
