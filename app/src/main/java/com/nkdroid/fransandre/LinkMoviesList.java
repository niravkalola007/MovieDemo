package com.nkdroid.fransandre;

import android.graphics.Color;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nkdroid.fransandre.model.ComplexPreferences;
import com.nkdroid.fransandre.model.MovieDetails;


public class LinkMoviesList extends ActionBarActivity {
    private Toolbar toolbar;
    private MovieDetails movieDetails;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_link_movies_list);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        ComplexPreferences complexPreferences = ComplexPreferences.getComplexPreferences(LinkMoviesList.this, "movie_pref", 0);
        movieDetails=complexPreferences.getObject("selected_movie", MovieDetails.class);
        if (toolbar != null) {

            toolbar.setTitle("Link");
            toolbar.setSubtitle(movieDetails.movieName+" to:");
            setSupportActionBar(toolbar);

            toolbar.setNavigationIcon(R.drawable.ic_white_back);

        }
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }
}
