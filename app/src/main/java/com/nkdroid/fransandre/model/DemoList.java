package com.nkdroid.fransandre.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by nirav kalola on 3/3/2015.
 */
public class DemoList {

    @SerializedName("movie_lists")
    public ArrayList<ListDetail> arrayList;
}
