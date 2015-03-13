package com.nkdroid.fransandre.UI;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.GsonBuilder;
import com.nkdroid.fransandre.R;
import com.nkdroid.fransandre.model.AdvancedSpannableString;
import com.nkdroid.fransandre.model.AppConstants;
import com.nkdroid.fransandre.model.ComplexPreferences;
import com.nkdroid.fransandre.model.DemoList;
import com.nkdroid.fransandre.model.ListDetail;
import com.nkdroid.fransandre.model.MovieDetails;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;


public class LinkMoviesList extends ActionBarActivity {
    private Toolbar toolbar;
    ArrayList<ListDetail> drawerTitleList;
    private RelativeLayout txtLinkRecommandation;
    private MovieDetails movieDetails;
    private  TextView headerText;
    private ListView movieList;
    private MoviesListAdapter moviesListAdapter;
    private ArrayList<ListDetail> moviesList;
    private static InputStream is = null;
    private int code;
    private String json = null;
    private ProgressDialog progressDialog;
    private DemoList moviesListObject;
    private String method = "POST";
    private String response;
    private String ADD_COMMENT_SERVICE = AppConstants.LINK_RECOMMENDATION;
    private String regId="";
    private String recId;
    String subTitle;
    private ArrayList<String> arrayListValue=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_link_movies_list);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        ComplexPreferences complexPreferences = ComplexPreferences.getComplexPreferences(LinkMoviesList.this, "movie_pref", 0);
        movieDetails=complexPreferences.getObject("selected_movie", MovieDetails.class);
        headerText= (TextView) findViewById(R.id.headerText);
        Intent i=getIntent();
        recId=i.getStringExtra("rec_id");
        AdvancedSpannableString advancedSpannableString=new AdvancedSpannableString("Link "+movieDetails.movieName+" to:");
        advancedSpannableString.setColor(Color.parseColor("#ffffff"), movieDetails.movieName);
        advancedSpannableString.setBold(movieDetails.movieName);
        advancedSpannableString.setCustomSizeOf(1.2f,movieDetails.movieName);
        headerText.setText(advancedSpannableString);
        if (toolbar != null) {
            toolbar.setTitle("");
            setSupportActionBar(toolbar);
            toolbar.setNavigationIcon(R.drawable.ic_white_back);
        }
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        movieList= (ListView) findViewById(R.id.linkMovieList);

        txtLinkRecommandation= (RelativeLayout) findViewById(R.id.txtLinkRecommandation);
        txtLinkRecommandation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO
                try{

                StringBuffer stringBuffer=new StringBuffer();
                for(int i=0;i<drawerTitleList.size();i++){
                    if(drawerTitleList.get(i).isCheckedValue==true){
                        stringBuffer.append(drawerTitleList.get(i).list_id+", ");
                    }
                }
                    subTitle=stringBuffer.toString();
                subTitle=subTitle.substring(0, subTitle.length() - 2);

                } catch (Exception e){
                    e.printStackTrace();
                }

                if(subTitle.length()!=0) {
                    postComment();
                }
            }
        });
        getMoviesList();
    }

    private void getMoviesList() {

        new AsyncTask<Void,Void,Void>(){
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressDialog=new ProgressDialog(LinkMoviesList.this);
                progressDialog.setCancelable(true);
                progressDialog.setMessage("Fetching Lists...");
                progressDialog.show();
            }

            @Override
            protected Void doInBackground(Void... params) {

                response=getJsonStringfromUrl(AppConstants.LIST);

                moviesListObject=new GsonBuilder().create().fromJson(response,DemoList.class);
                moviesList=moviesListObject.arrayList;


                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                progressDialog.dismiss();
                try {
                    Log.e("size...........", moviesList.size() + "");
                }catch (Exception e){
                    e.printStackTrace();
                }


                moviesListAdapter=new MoviesListAdapter(LinkMoviesList.this,moviesList);
                ViewGroup header = (ViewGroup) getLayoutInflater().inflate(R.layout.footer_view, movieList, false);
                ViewGroup emptyView = (ViewGroup) getLayoutInflater().inflate(R.layout.empty_view, movieList, false);
                movieList.addFooterView(header);
                movieList.setEmptyView(emptyView);
                movieList.setAdapter(moviesListAdapter);
//                movieList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//                    @Override
//                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//
//                    }
//                });
                TextView txtNewList=(TextView) header.findViewById(R.id.txtFooterAddList);
                txtNewList.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //TODO
                    }
                });

            }
        }.execute();
    }


    public class MoviesListAdapter extends BaseAdapter {

        Context context;

        ArrayList<ListDetail> arraylist;
        public MoviesListAdapter(Context context, ArrayList<ListDetail> drawerTitleListValue) {
            this.context = context;
            drawerTitleList = drawerTitleListValue;
            arraylist = new ArrayList<ListDetail>();
            arraylist.addAll(drawerTitleList);

        }

        public int getCount() {

            return drawerTitleList.size();
        }

        public Object getItem(int position) {
            return drawerTitleList.get(position);
        }

        public long getItemId(int position) {
            return position;
        }

        public class ViewHolder {
            TextView movieTitle,number;
            ImageView first,second,third,movieImage;
            ImageView extra;
        }

        public View getView(final int position, View convertView, ViewGroup parent) {

            final ViewHolder holder;
            LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.item_linked_movie_list, parent,false);
                holder = new ViewHolder();
                holder.movieTitle=(TextView)convertView.findViewById(R.id.movieTitle);
                holder.number=(TextView)convertView.findViewById(R.id.number);
                holder.first=(ImageView)convertView.findViewById(R.id.first);
                holder.second=(ImageView)convertView.findViewById(R.id.second);
                holder.third=(ImageView)convertView.findViewById(R.id.third);
                holder.movieImage=(ImageView)convertView.findViewById(R.id.movieImage);
                holder.extra= (ImageView) convertView.findViewById(R.id.extra);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.movieTitle.setText(drawerTitleList.get(position).list_name);

            holder.number.setText(drawerTitleList.get(position).list_rec_count);
            if (drawerTitleList.get(position).isCheckedValue==true) {
                holder.extra.setImageResource(R.drawable.square_check);
                holder.extra.setTag("active");
            } else {
                holder.extra.setImageResource(R.drawable.square);
                holder.extra.setTag("deactive");
            }




//            StringBuffer stringBuffer=new StringBuffer();
//            for(int i=0;i<drawerTitleList.get(position).generationArrayList.size();i++){
//                stringBuffer.append(drawerTitleList.get(position).generationArrayList.get(i).generation+", ");
//            }
//            String subTitle=stringBuffer.toString();
//            subTitle=subTitle.substring(0,subTitle.length()-2);
//            holder.movieSubTitle.setText(subTitle);
            Glide.with(LinkMoviesList.this)
                    .load("http://fransandre.com/flixlist/rev3/"+drawerTitleList.get(position).
                    list_avatar)
                    .placeholder(R.drawable.placeholder)
                    .centerCrop()
                    .into(holder.movieImage);
            Glide.with(LinkMoviesList.this)
                    .load("http://fransandre.com/flixlist/rev3/"+drawerTitleList.get(position).movie_recommendations.get(0).movie_avatar)
                    .placeholder(R.drawable.placeholder)
                    .centerCrop()
                    .into(holder.first);
            Glide.with(LinkMoviesList.this)
                    .load("http://fransandre.com/flixlist/rev3/"+drawerTitleList.get(position).movie_recommendations.get(1).movie_avatar)
                    .placeholder(R.drawable.placeholder)
                    .centerCrop()
                    .into(holder.second);
            Glide.with(LinkMoviesList.this)
                    .load("http://fransandre.com/flixlist/rev3/"+drawerTitleList.get(position).movie_recommendations.get(2).movie_avatar)
                    .placeholder(R.drawable.placeholder)
                    .centerCrop()
                    .into(holder.third);
            holder.extra.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String tag = holder.extra.getTag().toString();
                    if (tag.equalsIgnoreCase("active")) { // uncheck checkbox, add
                        arrayListValue.remove(drawerTitleList.get(position).list_id);

                        drawerTitleList.get(position).isCheckedValue = false;
                        holder.extra.setImageResource(R.drawable.square);
                        holder.extra.setTag("deactive");
                    } else { // check checkbox, remove
                        arrayListValue.add(drawerTitleList.get(position).list_id);
                        holder.extra.setImageResource(R.drawable.square_check);
                        holder.extra.setTag("active");
                        drawerTitleList.get(position).isCheckedValue = true;
                    }
                    if(arrayListValue.size()>0){
                        txtLinkRecommandation.setBackgroundResource(R.drawable.button_link_selector);
                    } else {
                        txtLinkRecommandation.setBackgroundResource(R.drawable.button_new_list_selector);
                    }
                }
            });

            convertView.setOnClickListener( new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String tag = holder.extra.getTag().toString();
                    if (tag.equalsIgnoreCase("active")) { // uncheck checkbox, add
                        arrayListValue.remove(drawerTitleList.get(position).list_id);
                        drawerTitleList.get(position).isCheckedValue=false;
                        holder.extra.setImageResource(R.drawable.square);
                        holder.extra.setTag("deactive");
                    } else { // check checkbox, remove
                        arrayListValue.add(drawerTitleList.get(position).list_id);
                        holder.extra.setImageResource(R.drawable.square_check);
                        holder.extra.setTag("active");
                        drawerTitleList.get(position).isCheckedValue=true;
                    }
                    if(arrayListValue.size()>0){
                        txtLinkRecommandation.setBackgroundResource(R.drawable.button_link_selector);
                    } else {
                        txtLinkRecommandation.setBackgroundResource(R.drawable.button_new_list_selector);
                    }
                }
            });
            return convertView;
        }


    }

    public String getJsonStringfromUrl(String url) {

        try {

            StringBuilder builder = new StringBuilder();

            DefaultHttpClient httpClient = new DefaultHttpClient();

            HttpGet httpGet = new HttpGet(url);

            HttpResponse httpResponse = httpClient.execute(httpGet);

            code = httpResponse.getStatusLine().getStatusCode();

            if (code == 200) {
                HttpEntity httpentity = httpResponse.getEntity();
                is = httpentity.getContent();

                BufferedReader br = new BufferedReader(new InputStreamReader(
                        is, "iso-8859-1"), 8);

                String line = null;
                while ((line = br.readLine()) != null) {
                    builder.append(line + "\n");
                }
                is.close();
            }

            json = builder.toString();

        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return json;

    }

    private void postComment() {

        new AsyncTask<Void,Void,Void>(){
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressDialog=new ProgressDialog(LinkMoviesList.this);
                progressDialog.setMessage("Linking...");
                progressDialog.show();
            }

            @Override
            protected Void doInBackground(Void... params) {
                try {
                    // Making HTTP request
                    // check for request method
                    List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                    try{
                        StringBuffer stringBuffer=new StringBuffer();
                        for(int i=0;i<drawerTitleList.size();i++){
                            if(drawerTitleList.get(i).isCheckedValue==true){
                                stringBuffer.append(drawerTitleList.get(i).list_id+", ");
                            }
                        }
                        subTitle=stringBuffer.toString();
                        subTitle=subTitle.substring(0, subTitle.length() - 2);
                    } catch (Exception e){
                        e.printStackTrace();
                    }

                    nameValuePairs.add(new BasicNameValuePair("list_id",subTitle+""));
                    nameValuePairs.add(new BasicNameValuePair("movie_rec_id",recId+""));
                    if (method.equals("POST")) {
                        // request method is POST
                        // defaultHttpClient
                        DefaultHttpClient httpClient = new DefaultHttpClient();
                        HttpPost httpPost = new HttpPost(ADD_COMMENT_SERVICE);
                        httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                        HttpResponse httpResponse = httpClient.execute(httpPost);
                        HttpEntity httpEntity = httpResponse.getEntity();
                        is = httpEntity.getContent();

                    } else if (method == "GET") {
                        // request method is GET
                        DefaultHttpClient httpClient = new DefaultHttpClient();
                        String paramString = URLEncodedUtils.format(nameValuePairs, "utf-8");
                        ADD_COMMENT_SERVICE += "?" + paramString;
                        HttpGet httpGet = new HttpGet(ADD_COMMENT_SERVICE);
                        HttpResponse httpResponse = httpClient.execute(httpGet);
                        HttpEntity httpEntity = httpResponse.getEntity();
                        is = httpEntity.getContent();
                    }

                    BufferedReader reader = new BufferedReader(new InputStreamReader(is, "iso-8859-1"), 8);
                    StringBuilder sb = new StringBuilder();
                    String line = null;
                    while ((line = reader.readLine()) != null) {
                        sb.append(line);
                    }
                    is.close();
                    json = sb.toString();
                    Log.e("response msg===> ", json.toString());


                } catch (HttpHostConnectException e) {
                    e.printStackTrace();

                } catch (SocketException e) {
                    e.printStackTrace();

                } catch (UnknownHostException e) {
                    e.printStackTrace();

                } catch (SocketTimeoutException e) {
                    e.printStackTrace();

                } catch (ConnectTimeoutException e) {
                    e.printStackTrace();

                } catch (Exception e) {
                    e.printStackTrace();

                }

                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                progressDialog.dismiss();
                try {
                    final JSONObject jsonObject = new JSONObject(json);

                    String responseCode=jsonObject.getString("response_code");
                    if(responseCode.equalsIgnoreCase("201")){

                        Intent intent=new Intent(LinkMoviesList.this,MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();
                        Toast.makeText(LinkMoviesList.this, jsonObject.getString("response_message"), Toast.LENGTH_LONG).show();


                    } else {
                        Toast.makeText(LinkMoviesList.this, "Error, Please Try again...", Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        }.execute();
    }
}
