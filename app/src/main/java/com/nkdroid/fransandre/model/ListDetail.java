package com.nkdroid.fransandre.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by nirav kalola on 3/3/2015.
 */
public class ListDetail {

    @SerializedName("list_id")
    public String list_id;
    @SerializedName("list_name")
    public String list_name;
    @SerializedName("list_avatar")
    public String list_avatar;
    @SerializedName("list_rec_count")
    public String list_rec_count;
    @SerializedName("list_usage")
    public String list_usage;
    @SerializedName("list_added_ago")
    public String list_added_ago;
    @SerializedName("movie_recommendations")
    public ArrayList<Avatar> movie_recommendations;
    public boolean isCheckedValue=false;



}
