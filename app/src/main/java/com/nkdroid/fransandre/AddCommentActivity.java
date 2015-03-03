package com.nkdroid.fransandre;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.nkdroid.fransandre.model.AppConstants;
import com.nkdroid.fransandre.model.ComplexPreferences;
import com.nkdroid.fransandre.model.MovieDetails;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;


public class AddCommentActivity extends ActionBarActivity {

    private EditText etShortComment;
    private ImageView txtSubmitComment;
    private Toolbar toolbar;
    private MovieDetails movieDetails;
    private ImageView movieImage;
    private TextView movieTitle,movieSubTitle;
    private ProgressDialog progressDialog;
    private String method = "POST";
    private String ADD_COMMENT_SERVICE = AppConstants.ADD_COMMENTS;
    private InputStream is = null;
    private String json = "",regId="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_comment);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {

            toolbar.setTitle("");
            setSupportActionBar(toolbar);
            Toolbar.LayoutParams layoutParams = new Toolbar.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
            ImageView icon=new ImageView(AddCommentActivity.this);
            icon.setBackgroundResource(R.drawable.ic_white_add_icon);
            icon.setLayoutParams(layoutParams);
            toolbar.addView(icon);
            toolbar.setNavigationIcon(R.drawable.ic_white_back);

        }
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(etShortComment.getWindowToken(), 0);
                finish();
            }
        });
        etShortComment= (EditText) findViewById(R.id.etShortComment);
        etShortComment.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(etShortComment.getText().toString().toString().length()>0) {
                    ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(etShortComment.getWindowToken(), 0);
                    postComment();
                }
                return true;
            }
        });

        etShortComment.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if(etShortComment.getText().toString().toString().length()==0){
                    txtSubmitComment.setBackgroundColor(Color.parseColor("#cccccc"));
                    txtSubmitComment.setClickable(false);
                } else {
                    txtSubmitComment.setBackgroundColor(Color.parseColor("#21a9e1"));
                    txtSubmitComment.setClickable(true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        txtSubmitComment= (ImageView) findViewById(R.id.txtSubmitCommentImage);
        txtSubmitComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(etShortComment.getWindowToken(), 0);
                postComment();
            }
        });
        movieImage= (ImageView) findViewById(R.id.movieImage);
        movieTitle= (TextView) findViewById(R.id.movieTitle);
        movieSubTitle= (TextView) findViewById(R.id.movieSubTitle);
        ComplexPreferences complexPreferences = ComplexPreferences.getComplexPreferences(AddCommentActivity.this, "movie_pref", 0);
        movieDetails=complexPreferences.getObject("selected_movie", MovieDetails.class);
        Glide.with(AddCommentActivity.this)
                .load("http://fransandre.com/flixlist/rev3/"+movieDetails.movieAvatar)
                .placeholder(R.drawable.placeholder)
                .centerCrop()
                .into(movieImage);

        movieTitle.setText(movieDetails.movieName);
        StringBuffer stringBuffer=new StringBuffer();
        for(int i=0;i<movieDetails.generationArrayList.size();i++){
            stringBuffer.append(movieDetails.generationArrayList.get(i).generation+", ");
        }
        String subTitle=stringBuffer.toString();
        subTitle=subTitle.substring(0, subTitle.length() - 2);
        movieSubTitle.setText(subTitle);
    }

    private void postComment() {

        new AsyncTask<Void,Void,Void>(){
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressDialog=new ProgressDialog(AddCommentActivity.this);
                progressDialog.setMessage("Adding Comment...");
                progressDialog.show();
            }

            @Override
            protected Void doInBackground(Void... params) {
                try {
                    // Making HTTP request
                    // check for request method
                    List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

                    nameValuePairs.add(new BasicNameValuePair("movie_id",movieDetails.movieId+""));
                    nameValuePairs.add(new BasicNameValuePair("rec_comment",etShortComment.getText().toString().trim()+""));

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
                    Log.e("Exception in doInBackground - Contact Us===> ",e.toString());
                } catch (SocketException e) {
                    e.printStackTrace();
                    Log.e("Exception in doInBackground - Contact Us===> ",e.toString());
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                    Log.e("Exception in doInBackground - Contact Us===> ",e.toString());
                } catch (SocketTimeoutException e) {
                    e.printStackTrace();
                    Log.e("Exception in doInBackground - Contact Us===> ",e.toString());
                } catch (ConnectTimeoutException e) {
                    e.printStackTrace();
                    Log.e("Exception in doInBackground - Contact Us===> ",e.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e("Exception in doInBackground - Contact Us===> ",e.toString());
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
                        etShortComment.setText("");
                        Toast.makeText(AddCommentActivity.this, jsonObject.getString("response_message"), Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(AddCommentActivity.this, LinkMoviesList.class);
                        intent.putExtra("rec_id", jsonObject.getString("recommendation_id"));
                        startActivity(intent);

                    } else {
                        Toast.makeText(AddCommentActivity.this, "Error, Please Try again...", Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        }.execute();
    }

}
