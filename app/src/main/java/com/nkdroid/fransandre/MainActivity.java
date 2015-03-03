package com.nkdroid.fransandre;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.GsonBuilder;
import com.nkdroid.fransandre.model.AdvancedSpannableString;
import com.nkdroid.fransandre.model.AppConstants;
import com.nkdroid.fransandre.model.ComplexPreferences;
import com.nkdroid.fransandre.model.MovieDetails;
import com.nkdroid.fransandre.model.MoviesList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Locale;


public class MainActivity extends ActionBarActivity {


    private Toolbar toolbar;
    private EditText etSearch;
    private ListView movieList;
    private MoviesListAdapter moviesListAdapter;
    private ArrayList<MovieDetails> moviesList;
    private static InputStream is = null;
    private int code;
    private String json = null;
    private ProgressDialog progressDialog;
    private MoviesList moviesListObject;
    private String response;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
//        etSearch= (EditText) findViewById(R.id.etSearch);


        if (toolbar != null) {

            toolbar.setTitle("");
            setSupportActionBar(toolbar);
            Toolbar.LayoutParams layoutParams = new Toolbar.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
            ImageView icon=new ImageView(MainActivity.this);
            icon.setBackgroundResource(R.drawable.ic_white_serach);
            icon.setLayoutParams(layoutParams);
            toolbar.addView(icon);
            toolbar.setNavigationIcon(R.drawable.ic_white_back);

        }


        movieList= (ListView) findViewById(R.id.movieList);
        getMoviesList();
    }



    private void getMoviesList() {

        new AsyncTask<Void,Void,Void>(){
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressDialog=new ProgressDialog(MainActivity.this);
                progressDialog.setCancelable(true);
                progressDialog.setMessage("Fetching Movies...");
                progressDialog.show();
            }

            @Override
            protected Void doInBackground(Void... params) {

                response=getJsonStringfromUrl(AppConstants.MOVIES_LIST);

                moviesListObject=new GsonBuilder().create().fromJson(response,MoviesList.class);
                moviesList=moviesListObject.movieDetailsArrayList;


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
                moviesListAdapter=new MoviesListAdapter(MainActivity.this,moviesList);
                ViewGroup header = (ViewGroup) getLayoutInflater().inflate(R.layout.headerview, movieList, false);
                movieList.addHeaderView(header);
                movieList.setAdapter(moviesListAdapter);
//                movieList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//                    @Override
//                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//
//                    }
//                });
                etSearch=(EditText) header.findViewById(R.id.etSearch);
                etSearch.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        moviesListAdapter.filter(etSearch.getText().toString().trim());
                        movieList.invalidate();
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });
            }
        }.execute();
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.menu_main, menu);
//        MenuItem searchItem = menu.findItem(R.id.action_search);
//        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
//        //*** setOnQueryTextFocusChangeListener ***
//        searchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
//
//            @Override
//            public void onFocusChange(View v, boolean hasFocus) {
//
//
//
//            }
//        });
//
//        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//
//            @Override
//            public boolean onQueryTextSubmit(String query) {
////                Toast.makeText(MainActivity.this, "called", Toast.LENGTH_SHORT).show();
//
//
//
//                return false;
//            }
//
//            @Override
//            public boolean onQueryTextChange(String searchQuery) {
//
//                return true;
//            }
//        });
//
//        MenuItemCompat.setOnActionExpandListener(searchItem, new MenuItemCompat.OnActionExpandListener() {
//            @Override
//            public boolean onMenuItemActionCollapse(MenuItem item) {
//                // Do something when collapsed
//                return true;  // Return true to collapse action view
//            }
//
//            @Override
//            public boolean onMenuItemActionExpand(MenuItem item) {
//                // Do something when expanded
//                return true;  // Return true to expand action view
//            }
//        });
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        int id = item.getItemId();
//        if (id == R.id.action_search) {
////            Toast.makeText(this, "Search", Toast.LENGTH_SHORT).show();
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }

    public class MoviesListAdapter extends BaseAdapter {

        Context context;
        ArrayList<MovieDetails> drawerTitleList;
        ArrayList<MovieDetails> arraylist;
        public MoviesListAdapter(Context context, ArrayList<MovieDetails> drawerTitleList) {
            this.context = context;
            this.drawerTitleList = drawerTitleList;
            arraylist = new ArrayList<MovieDetails>();
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
            TextView movieTitle,movieSubTitle;
            ImageView movieImage;
        }

        public View getView(final int position, View convertView, ViewGroup parent) {

            final ViewHolder holder;
            LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.item_movie_list, parent,false);
                holder = new ViewHolder();
                holder.movieTitle=(TextView)convertView.findViewById(R.id.movieTitle);
                holder.movieSubTitle=(TextView)convertView.findViewById(R.id.movieSubTitle);
                holder.movieImage=(ImageView)convertView.findViewById(R.id.movieImage);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.movieTitle.setText(drawerTitleList.get(position).movieName);
            StringBuffer stringBuffer=new StringBuffer();
            for(int i=0;i<drawerTitleList.get(position).generationArrayList.size();i++){
                stringBuffer.append(drawerTitleList.get(position).generationArrayList.get(i).generation+", ");
            }
            String subTitle=stringBuffer.toString();
            subTitle=subTitle.substring(0,subTitle.length()-2);
            holder.movieSubTitle.setText(subTitle);
            Glide.with(MainActivity.this)
                    .load("http://fransandre.com/flixlist/rev3/"+drawerTitleList.get(position).movieAvatar)
                    .placeholder(R.drawable.placeholder)
                    .centerCrop()
                    .into(holder.movieImage);
            convertView.setOnClickListener( new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ComplexPreferences complexPreferences = ComplexPreferences.getComplexPreferences(MainActivity.this, "movie_pref", 0);
                    complexPreferences.putObject("selected_movie",drawerTitleList.get(position));
                    complexPreferences.commit();
                    Intent intent=new Intent(MainActivity.this,AddCommentActivity.class);
                    startActivity(intent);
                }
            });
            return convertView;
        }

        // Filter Class
        public void filter(String charText) {

            charText = charText.toLowerCase(Locale.getDefault());

            drawerTitleList.clear();
            if (charText.length() == 0) {
                drawerTitleList.addAll(arraylist);

            } else {
                for (MovieDetails movieDetails : arraylist) {
                    if (charText.length() != 0 && movieDetails.movieName.toLowerCase(Locale.getDefault()).contains(charText)) {
                        drawerTitleList.add(movieDetails);
                    }

                }
            }
            notifyDataSetChanged();
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


}
