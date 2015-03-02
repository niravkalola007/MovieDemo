package com.nkdroid.fransandre.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by nirav kalola on 3/1/2015.
 */
public class MoviesList {

    @SerializedName("movies")
    public ArrayList<MovieDetails> movieDetailsArrayList;
}
