package com.example.android.movieapp.data;

import android.net.Uri;
import android.provider.BaseColumns;

public class MovieContract {
    public static final String CONTENT_AUTHORITY = "com.example.android.movieapp";

    public static final String BASE_URL = "content://" + CONTENT_AUTHORITY;

    public static final String PATH_FAVORITE = "favorite";

    public static final class FavoriteEntry implements BaseColumns{

        public static final Uri CONTENT_URI = Uri.parse(BASE_URL).buildUpon()
                .appendPath(PATH_FAVORITE)
                .build();

        public static final String TABLE_NAME = "favorite";

        public static final String COLUMN_ID = "id";

        public static final String COLUMN_TITLE = "title";

        public static final String COLUMN_POSTER = "poster";

        public static final String COLUMN_SYNOPSIS = "synopsis";

        public static final String COLUMN_RATING = "rating";

        public static final String COLUMN_RELEASE_DATE = "release_date";

    }
}
