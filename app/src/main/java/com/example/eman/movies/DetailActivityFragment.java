package com.example.eman.movies;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.eman.movies.Data.FavoriteMoviesContract;
import com.squareup.picasso.Picasso;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailActivityFragment extends Fragment {

    private static final String MOVIES_SHARE_HASHTAG = "# Movie";
    private static final String LOG_TAG ="TAG" ;
    private String mId;
    private String mTitle;
    private String mSynopsis;
    private String mPosterPath;
    private String mUserRating;
    private String mReleaseDate;
    private int mIsFavorite;
    private ShareActionProvider mShareActionProvider;
    private String Movie;

    private CheckBox mCheckbox;


    public DetailActivityFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

        ImageView poster = (ImageView) rootView.findViewById(R.id.poster_image_view);
        mCheckbox = (CheckBox) rootView.findViewById(R.id.favorite_checkbox);
        Intent intent = getActivity().getIntent();
        Bundle args = getArguments();

        //Retrieve data from the intent or args
        if (args != null) {
            mId = args.getString("id");
            mTitle = args.getString("title");
            mSynopsis = args.getString("synopsis");
            mPosterPath = args.getString("posterPath");
            mUserRating = args.getString("userRating");
            mReleaseDate = args.getString("releaseDate");

        } else if (intent != null) {
            mId = intent.getStringExtra("id");
            mTitle = intent.getStringExtra("title");
            mSynopsis = intent.getStringExtra("synopsis");
            mPosterPath = intent.getStringExtra("posterPath");
            mUserRating = intent.getStringExtra("userRating");
            mReleaseDate = intent.getStringExtra("releaseDate");
        }

        //Fetch trailers info
        FetchTrailerTask fetchTrailerTask = new FetchTrailerTask(getActivity(), rootView);
        fetchTrailerTask.execute(mId);

        //Fetch reviews info
        FetchReviewTask fetchReviewTask = new FetchReviewTask(getActivity(), rootView);
        fetchReviewTask.execute(mId);

        //Add the data to the UI
        ((TextView) rootView.findViewById(R.id.title_text_view)).setText(mTitle);
        ((TextView) rootView.findViewById(R.id.overview_text_view)).setText(mSynopsis);
        ((TextView) rootView.findViewById(R.id.user_rating_text_view)).setText(mUserRating);
        ((TextView) rootView.findViewById(R.id.release_year_text_view)).setText(mReleaseDate);
        Picasso.with(getActivity())
                .load(mPosterPath)
                .into(poster);

        //Set onClick method for the checkBox
        mCheckbox.setChecked(isFavorite());
        mCheckbox.setOnClickListener(new FavoriteOnClickListener());

        return rootView;
    }
    ///////////////////////share/////////////////////////////////////////
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.detailfragment, menu);

        // Retrieve the share menu item
        MenuItem menuItem = menu.findItem(R.id.action_share);

        // Get the provider and hold onto it to set/change the share intent.
        ShareActionProvider mShareActionProvider =
                (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);


        if (mShareActionProvider != null) {
            mShareActionProvider.setShareIntent(createShareForecastIntent());
        } else {
            Log.d(LOG_TAG, "Share Action Provider is null?");
        }
    }
    private Intent createShareForecastIntent() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, Movie + MOVIES_SHARE_HASHTAG);
        return shareIntent;
    }

    //////////////////////////////////////////////////////////////////////////

    public boolean isFavorite (){
        Cursor movieCursor = getActivity().getContentResolver().query(
                FavoriteMoviesContract.movieDetailEntry.CONTENT_URI,
                new String[]{FavoriteMoviesContract.movieDetailEntry.COLUMN_FAVORITE},
                FavoriteMoviesContract.movieDetailEntry.COLUMN_MOVIE_ID + " = ? ",
                new String[]{mId},
                null
        );

        if(!movieCursor.moveToFirst()){
            return false;
        } else {
            return movieCursor.getInt(0) == 1;
        }
    }

    public class FavoriteOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {

            mIsFavorite = 1;

            ContentValues values = new ContentValues();
            values.put(FavoriteMoviesContract.movieDetailEntry.COLUMN_MOVIE_ID, mId);
            values.put(FavoriteMoviesContract.movieDetailEntry.COLUMN_MOVIE_TITLE, mTitle);
            values.put(FavoriteMoviesContract.movieDetailEntry.COLUMN_POSTER_PATH, mPosterPath);
            values.put(FavoriteMoviesContract.movieDetailEntry.COLUMN_RELEASE_DATE, mReleaseDate);
            values.put(FavoriteMoviesContract.movieDetailEntry.COLUMN_USER_RATING, mUserRating);
            values.put(FavoriteMoviesContract.movieDetailEntry.COLUMN_SYNOPSIS, mSynopsis);

            Cursor movieCursor = getActivity().getContentResolver().query(
                    FavoriteMoviesContract.movieDetailEntry.CONTENT_URI,
                    new String[]{FavoriteMoviesContract.movieDetailEntry._ID},
                    FavoriteMoviesContract.movieDetailEntry.COLUMN_MOVIE_ID + " = ? ",
                    new String[]{mId},
                    null
            );

            if (!movieCursor.moveToFirst()) {
                //If not on the DB, add it as a Favorite
                values.put(FavoriteMoviesContract.movieDetailEntry.COLUMN_FAVORITE, mIsFavorite);
                getActivity().getContentResolver().insert(
                        FavoriteMoviesContract.movieDetailEntry.CONTENT_URI,
                        values
                );
            } else {
                //Already on the DB, update it's status
                if (!mCheckbox.isChecked()) {
                    mIsFavorite = 0;
                }
                values.put(FavoriteMoviesContract.movieDetailEntry.COLUMN_FAVORITE, mIsFavorite);
                getActivity().getContentResolver().update(
                        FavoriteMoviesContract.movieDetailEntry.CONTENT_URI,
                        values,
                        FavoriteMoviesContract.movieDetailEntry.COLUMN_MOVIE_ID + " = ? ",
                        new String[]{mId}
                );
            }
        }
    }}