package com.example.apoddemo;

import android.graphics.Bitmap;
import android.support.constraint.Constraints;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.*;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ImageView img = (ImageView) findViewById(R.id.podImg);
        int imgDefault = getResources().getIdentifier("@drawable/rocket", null, getPackageName());
        img.setImageResource(imgDefault);

        TextView desc = (TextView) findViewById(R.id.imageDescription);
        desc.setMovementMethod(new ScrollingMovementMethod());
        fetchInfo();
    }

    public void fetchInfo(/*View view*/) {
        final TextView title = (TextView) findViewById(R.id.imageTitle);
        final TextView copyright = (TextView) findViewById(R.id.imageCopyright);
        final TextView desc = (TextView) findViewById(R.id.imageDescription);

        final ImageView img = (ImageView) findViewById(R.id.podImg);
        final WebView webvid = (WebView) findViewById(R.id.podWeb);

        WebSettings webset = webvid.getSettings();
        webset.setJavaScriptEnabled(true);

        final Calendar currentDay = Calendar.getInstance();
        final Date currentDateDay = currentDay.getTime();
        final Calendar day = Calendar.getInstance();
        final TextView time = (TextView) findViewById(R.id.date);

        final String[] date = {formatter(day)};
        time.setText(formatter(currentDateDay));

        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.saveload, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        //String url = "https://www.google.com";
        //String url = "https://api.nasa.gov/planetary/apod?api_key=DEMO_KEY";
        final String keyurl = "https://api.nasa.gov/planetary/apod?api_key=hCcahvUhc0xMW2H2mox6vYpS7jKPU2SM1Rv5xMhZ";
        final String[] url = {""};
        //url.concat("&date=2018-06-10");

        final int imgDefault = getResources().getIdentifier("@drawable/rocket", null, getPackageName());

        Button prevBtn = (Button) findViewById(R.id.prevBtn);
        Button nextBtn = (Button) findViewById(R.id.nextBtn);
        Button fetch = (Button) findViewById(R.id.fetch);

        final RequestQueue queue = Volley.newRequestQueue(this);

        prevBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                day.add(Calendar.DATE, -1);
                Date volDay = day.getTime();
                time.setText(formatter(volDay));
                date[0] = "&date=";
                date[0] = date[0].concat(formatter(volDay));
                url[0] = keyurl;
                url[0] = url[0].concat(date[0]);


                //---------------------
                JsonObjectRequest updateRequest = new JsonObjectRequest(Request.Method.GET, url[0], null,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {

                                    String picurl = response.getString("url");

                                    title.setText(response.getString("title"));
                                    if(response.has("copyright")) {
                                            copyright.setText(response.getString("copyright").replace(System.getProperty("line.separator"), " "));
                                       }
                                    else {
                                        copyright.setText("");
                                    }
                                    desc.setText(response.getString("explanation"));

                                    if(response.has("url")){

                                        if(response.getString("media_type").equals("video")){
                                            img.setVisibility(View.INVISIBLE);
                                            webvid.setVisibility(View.VISIBLE);
                                            webvid.loadUrl(picurl);
                                        }
                                        else{
                                            webvid.setVisibility(View.INVISIBLE);
                                            //webvid.loadUrl("about:blank");
                                            img.setVisibility(View.VISIBLE);
                                            ImageRequest imgRequest = new ImageRequest(picurl, new Response.Listener<Bitmap>() {
                                                @Override
                                                public void onResponse(Bitmap bmp) {
                                                    img.setImageBitmap(bmp);


                                                }
                                            }, 0, 0, null, Bitmap.Config.RGB_565, new Response.ErrorListener() {
                                                @Override
                                                public void onErrorResponse(VolleyError error) {
                                                    img.setImageResource(R.drawable.oops2);
                                                }
                                            });
                                            queue.add(imgRequest);
                                        }
                                    }
                                    else{
                                        img.setImageResource(R.drawable.oops2);
                                    }

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        desc.setText(R.string.responseError);
                    }
                });

                queue.add(updateRequest);
                //----------------------------------
            }
        });

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                day.add(Calendar.DATE, 1);
                while(day.compareTo(currentDay) > 0){
                    //day[0] = currentDay;
                    day.add(Calendar.DATE, -1);
                }
                Date volDay = day.getTime();
                time.setText(formatter(volDay));
                date[0] = "&date=";
                date[0] = date[0].concat(formatter(volDay));
                url[0] = keyurl;
                url[0] = url[0].concat(date[0]);


                //---------------------
                JsonObjectRequest updateRequest = new JsonObjectRequest(Request.Method.GET, url[0], null,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {

                                    String picurl = response.getString("url");

                                    title.setText(response.getString("title"));
                                    if(response.has("copyright")) {
                                        copyright.setText(response.getString("copyright").replace(System.getProperty("line.separator"), " "));
                                    }
                                    else {
                                        copyright.setText("");
                                    }
                                    desc.setText(response.getString("explanation"));

                                    if(response.has("url")){

                                        if(response.getString("media_type").equals("video")){
                                            img.setVisibility(View.INVISIBLE);
                                            webvid.setVisibility(View.VISIBLE);
                                            webvid.loadUrl(picurl);
                                        }
                                        else{
                                            webvid.setVisibility(View.INVISIBLE);
                                            //webvid.loadUrl("about:blank");
                                            img.setVisibility(View.VISIBLE);
                                            ImageRequest imgRequest = new ImageRequest(picurl, new Response.Listener<Bitmap>() {
                                                @Override
                                                public void onResponse(Bitmap bmp) {
                                                    img.setImageBitmap(bmp);


                                                }
                                            }, 0, 0, null, Bitmap.Config.RGB_565, new Response.ErrorListener() {
                                                @Override
                                                public void onErrorResponse(VolleyError error) {
                                                    img.setImageResource(R.drawable.oops2);
                                                }
                                            });
                                            queue.add(imgRequest);
                                        }
                                    }
                                    else{
                                        img.setImageResource(R.drawable.oops2);
                                    }

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        desc.setText(R.string.responseError);
                    }
                });

                queue.add(updateRequest);
                //----------------------------------
            }
        });

        fetch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                time.setText(formatter(currentDateDay));
                url[0] = keyurl;
                while(day.compareTo(currentDay) > 0){
                    day.add(Calendar.DATE, -1);
                }
                while(day.compareTo(currentDay) < 0){
                    day.add(Calendar.DATE, 1);
                }
                JsonObjectRequest stringRequest = new JsonObjectRequest(Request.Method.GET, url[0], null,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {

                                    String picurl = response.getString("url");

                                    title.setText(response.getString("title"));
                                    if(response.has("copyright")) {
                                        copyright.setText(response.getString("copyright").replace(System.getProperty("line.separator"), " "));
                                    }
                                    else {
                                        copyright.setText("");
                                    }
                                    desc.setText(response.getString("explanation"));

                                    if(response.has("url")){

                                        if(response.getString("media_type").equals("video")){
                                            img.setVisibility(View.INVISIBLE);
                                            webvid.setVisibility(View.VISIBLE);
                                            webvid.loadUrl(picurl);
                                        }
                                        else{
                                            webvid.setVisibility(View.INVISIBLE);
                                            //webvid.loadUrl("about:blank");
                                            img.setVisibility(View.VISIBLE);
                                            ImageRequest imgRequest = new ImageRequest(picurl, new Response.Listener<Bitmap>() {
                                                @Override
                                                public void onResponse(Bitmap bmp) {
                                                    img.setImageBitmap(bmp);


                                                }
                                            }, 0, 0, null, Bitmap.Config.RGB_565, new Response.ErrorListener() {
                                                @Override
                                                public void onErrorResponse(VolleyError error) {
                                                    img.setImageResource(R.drawable.oops2);
                                                }
                                            });
                                            queue.add(imgRequest);
                                        }
                                    }
                                    else{
                                        img.setImageResource(R.drawable.oops2);
                                    }

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        desc.setText(R.string.responseError);
                    }
                });
                queue.add(stringRequest);

            }
        });


    }

    public String formatter(Date date){
        final String pattern = "yyyy-MM-dd";
        DateFormat ymd = new SimpleDateFormat(pattern);
        String form = ymd.format(date);

        return form;
    }
    public String formatter(Calendar cal){
        Date date = cal.getTime();
        final String pattern = "yyyy-mm-dd";
        SimpleDateFormat ymd = new SimpleDateFormat(pattern);
        String form = ymd.format(date);

        return form;
    }

}

