package com.nkdroid.fransandre;

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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.GsonBuilder;
import com.nhaarman.listviewanimations.swinginadapters.AnimationAdapter;
import com.nhaarman.listviewanimations.swinginadapters.prepared.SwingBottomInAnimationAdapter;
import com.nkdroid.fransandre.UI.MainActivity;
import com.nkdroid.fransandre.model.AppConstants;
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


public class SendListActivity extends ActionBarActivity {
    private Toolbar toolbar;
    ArrayList<ListDetail> drawerTitleList;


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
        setContentView(R.layout.activity_send_list);
        movieList= (ListView) findViewById(R.id.movieList);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            toolbar.setTitle("SEND LIST");
            toolbar.setTitleTextColor(getResources().getColor(R.color.primaryColor));
            setSupportActionBar(toolbar);
            toolbar.setNavigationIcon(R.drawable.ic_back_color);
        }
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        getMoviesList();
    }
    private void getMoviesList() {

        new AsyncTask<Void,Void,Void>(){
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressDialog=new ProgressDialog(SendListActivity.this);
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

                moviesList.addAll(moviesList);
                moviesListAdapter=new MoviesListAdapter(SendListActivity.this,moviesList);
//                ViewGroup header = (ViewGroup) getLayoutInflater().inflate(R.layout.footer_view, movieList, false);
                ViewGroup emptyView = (ViewGroup) getLayoutInflater().inflate(R.layout.empty_view, movieList, false);
//                movieList.addFooterView(header);
                movieList.setEmptyView(emptyView);
//                movieList.setAdapter(moviesListAdapter);
                AnimationAdapter animAdapter = new SwingBottomInAnimationAdapter(moviesListAdapter);
                animAdapter.setAbsListView(movieList);
                movieList.setAdapter(animAdapter);
//                movieList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//                    @Override
//                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//
//                    }
//                });
//                TextView txtNewList=(TextView) header.findViewById(R.id.txtFooterAddList);
//                txtNewList.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        //TODO
//                    }
//                });

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
            TextView title;
            ImageView first,second,third,four;

        }

        public View getView(final int position, View convertView, ViewGroup parent) {

            final ViewHolder holder;
            LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.item_sent_list, parent,false);
                holder = new ViewHolder();
                holder.title=(TextView)convertView.findViewById(R.id.title);

                holder.first=(ImageView)convertView.findViewById(R.id.one);
                holder.second=(ImageView)convertView.findViewById(R.id.two);
                holder.third=(ImageView)convertView.findViewById(R.id.three);
                holder.four=(ImageView)convertView.findViewById(R.id.four);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.title.setText(drawerTitleList.get(position).list_name);
            Log.e("image link: ",drawerTitleList.get(position).movie_recommendations.get(0).movie_avatar);
//
//            holder.number.setText(drawerTitleList.get(position).list_rec_count);
//            if (drawerTitleList.get(position).isCheckedValue==true) {
//                holder.extra.setImageResource(R.drawable.square_check);
//                holder.extra.setTag("active");
//            } else {
//                holder.extra.setImageResource(R.drawable.square);
//                holder.extra.setTag("deactive");
//            }
//
//
//
//
////            StringBuffer stringBuffer=new StringBuffer();
////            for(int i=0;i<drawerTitleList.get(position).generationArrayList.size();i++){
////                stringBuffer.append(drawerTitleList.get(position).generationArrayList.get(i).generation+", ");
////            }
////            String subTitle=stringBuffer.toString();
////            subTitle=subTitle.substring(0,subTitle.length()-2);
////            holder.movieSubTitle.setText(subTitle);
            Glide.with(SendListActivity.this)

                    .load(drawerTitleList.get(position).movie_recommendations.get(0).movie_avatar)
                    .placeholder(R.drawable.placeholder)
                    .centerCrop()
                    .into(holder.four);
            Glide.with(SendListActivity.this)
                    .load(drawerTitleList.get(position).movie_recommendations.get(0).movie_avatar)
                    .placeholder(R.drawable.placeholder)
                    .centerCrop()
                    .into(holder.first);
            Glide.with(SendListActivity.this)
                    .load(drawerTitleList.get(position).movie_recommendations.get(1).movie_avatar)
                    .placeholder(R.drawable.placeholder)
                    .centerCrop()
                    .into(holder.second);
            Glide.with(SendListActivity.this)
                    .load(drawerTitleList.get(position).movie_recommendations.get(2).movie_avatar)
                    .placeholder(R.drawable.placeholder)
                    .centerCrop()
                    .into(holder.third);
//            holder.extra.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    String tag = holder.extra.getTag().toString();
//                    if (tag.equalsIgnoreCase("active")) { // uncheck checkbox, add
//                        arrayListValue.remove(drawerTitleList.get(position).list_id);
//
//                        drawerTitleList.get(position).isCheckedValue = false;
//                        holder.extra.setImageResource(R.drawable.square);
//                        holder.extra.setTag("deactive");
//                    } else { // check checkbox, remove
//                        arrayListValue.add(drawerTitleList.get(position).list_id);
//                        holder.extra.setImageResource(R.drawable.square_check);
//                        holder.extra.setTag("active");
//                        drawerTitleList.get(position).isCheckedValue = true;
//                    }
//
//                }
//            });
//
//            convertView.setOnClickListener( new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    String tag = holder.extra.getTag().toString();
//                    if (tag.equalsIgnoreCase("active")) { // uncheck checkbox, add
//                        arrayListValue.remove(drawerTitleList.get(position).list_id);
//                        drawerTitleList.get(position).isCheckedValue=false;
//                        holder.extra.setImageResource(R.drawable.square);
//                        holder.extra.setTag("deactive");
//                    } else { // check checkbox, remove
//                        arrayListValue.add(drawerTitleList.get(position).list_id);
//                        holder.extra.setImageResource(R.drawable.square_check);
//                        holder.extra.setTag("active");
//                        drawerTitleList.get(position).isCheckedValue=true;
//                    }
//                }
//            });
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
                progressDialog=new ProgressDialog(SendListActivity.this);
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

                        Intent intent=new Intent(SendListActivity.this,MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();
                        Toast.makeText(SendListActivity.this, jsonObject.getString("response_message"), Toast.LENGTH_LONG).show();


                    } else {
                        Toast.makeText(SendListActivity.this, "Error, Please Try again...", Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        }.execute();
    }

}
