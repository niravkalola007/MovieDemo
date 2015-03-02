package com.nkdroid.fransandre.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by nirav kalola on 3/1/2015.
 */
public class MovieDetails {

    @SerializedName("movie_id")
    public String movieId;
    @SerializedName("movie_name")
    public String movieName;
    @SerializedName("movie_avatar")
    public String movieAvatar;
    @SerializedName("movie_usage")
    public String movieUsage;
    @SerializedName("movie_genres")
    public ArrayList<Generation> generationArrayList;

}
