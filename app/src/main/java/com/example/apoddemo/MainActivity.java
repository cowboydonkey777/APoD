package com.example.apoddemo;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.constraint.Constraints;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.nio.channels.FileChannel;
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

        // not all urls are images, some are videos. webviews can play those
        WebSettings webset = webvid.getSettings();
        webset.setJavaScriptEnabled(true);

        // need a date to build a url. currentday is held constant, day can change
        final Calendar currentDay = Calendar.getInstance();
        final Date currentDateDay = currentDay.getTime();
        final Calendar day = Calendar.getInstance();
        final TextView time = (TextView) findViewById(R.id.date);

        // formatter turns day into format yyyy-MM-dd. M is month, m is minutes
        final String[] date = {formatter(day)};
        time.setText(formatter(currentDateDay));

        //String url = "https://api.nasa.gov/planetary/apod?api_key=DEMO_KEY";
        // applied for my own api key, it's the one below
        final String keyurl = "https://api.nasa.gov/planetary/apod?api_key=hCcahvUhc0xMW2H2mox6vYpS7jKPU2SM1Rv5xMhZ";
        final String[] url = {""};

        // if true, user can save, if false, user cannot save
        final Boolean[] saveable = {false};

        final int imgDefault = getResources().getIdentifier("@drawable/rocket", null, getPackageName());

        Button prevBtn = (Button) findViewById(R.id.prevBtn);
        Button nextBtn = (Button) findViewById(R.id.nextBtn);
        Button fetch = (Button) findViewById(R.id.fetch);
        ImageButton saveBtn = (ImageButton) findViewById(R.id.saveBtn);
        final RequestQueue queue = Volley.newRequestQueue(this);

        saveBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                JsonObjectRequest updateRequest = new JsonObjectRequest(Request.Method.GET, url[0], null,
                        new Response.Listener<JSONObject>(){
                            @Override
                            public void onResponse(JSONObject response){
                                try{
                                    String picurl = response.getString("url");

                                    ImageRequest imgRequest = new ImageRequest(picurl, new Response.Listener<Bitmap>() {
                                        @Override
                                        public void onResponse(Bitmap bmp) {
                                            if (saveable[0].equals(true)) {
                                                Date nameget = day.getTime();
                                                String savename = formatter(nameget);


                                                // saves to a directory called storage/emulated/0/Pictures
                                                // it's internal storage from what i can tell, yet i need external storage?
                                                String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
                                                requestPermissions(permissions, 1);
                                                OutputStream out;
                                                File filepath = Environment.getExternalStorageDirectory();
                                                File dir = new File(filepath.getAbsolutePath() + "/Pictures");
                                                dir.mkdirs();
                                                savename.concat(".png");
                                                File file = new File(dir, savename);

                                                // notifies the user they just saved
                                                Toast.makeText(MainActivity.this, "Image saved to: " + dir, Toast.LENGTH_SHORT).show();

                                                try{
                                                    out = new FileOutputStream(file);

                                                    // this is what is actually saving
                                                    bmp.compress(Bitmap.CompressFormat.PNG, 100, out);
                                                    out.flush();
                                                    out.close();
                                                }
                                                catch(Exception e){
                                                    e.printStackTrace();
                                                }
                                            }

                                        }
                                    }, 0, 0, null, Bitmap.Config.RGB_565, new Response.ErrorListener() {
                                        @Override
                                        public void onErrorResponse(VolleyError error) {
                                            img.setImageResource(R.drawable.oops2);
                                        }
                                    });
                                    queue.add(imgRequest);
                                }catch(JSONException e){
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
            }
        });

        // -----------------------------------------------------------------------------------------

        // sets dat to previous day, pulls image/video
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
                //important value in the above is the result of url[0], it's the url used to request image

                //---------------------
                JsonObjectRequest updateRequest = new JsonObjectRequest(Request.Method.GET, url[0], null,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {

                                    // picurl: picture url. used to request through volley either an image or video
                                    String picurl = response.getString("url");

                                    title.setText(response.getString("title"));
                                    if(response.has("copyright")) {
                                            copyright.setText(response.getString("copyright").replace(System.getProperty("line.separator"), " "));
                                            // from demo, useful to figure out how things work
                                       }
                                    else {
                                        copyright.setText("");
                                    }
                                    desc.setText(response.getString("explanation"));

                                    if(response.has("url")){

                                        if(response.getString("media_type").equals("video")){
                                            saveable[0] = false;
                                            // image and videos are not displayed together, when one is up, set the other invisible
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
                                                    saveable[0] = true;
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

        // -----------------------------------------------------------------------------------------

        // get image/video for next day
        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                day.add(Calendar.DATE, 1);
                while(day.compareTo(currentDay) > 0){ //if day is greater than current constant, loop it back
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
                                            saveable[0] = false;
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
                                                    saveable[0] = true;
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

        // -----------------------------------------------------------------------------------------

        // get image/video from the current day
        fetch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                time.setText(formatter(currentDateDay));
                url[0] = keyurl;
                while(day.compareTo(currentDay) > 0){ // loop day back to current day if either before day, or somehow greater than day
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
                                            saveable[0] = false;
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
                                                    saveable[0] = true;
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

