package com.example.apoddemo;

import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
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
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

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
    }

    public void fetchInfo(View view) {
        final TextView title = (TextView) findViewById(R.id.imageTitle);
        final TextView copyright = (TextView) findViewById(R.id.imageCopyright);
        final TextView desc = (TextView) findViewById(R.id.imageDescription);

        final ImageView img = (ImageView) findViewById(R.id.podImg);

        final Calendar day = Calendar.getInstance();
        final String[] date = {formatter(day)};

        //String url = "https://www.google.com";
        //String url = "https://api.nasa.gov/planetary/apod?api_key=DEMO_KEY";
        String url = "https://api.nasa.gov/planetary/apod?api_key=hCcahvUhc0xMW2H2mox6vYpS7jKPU2SM1Rv5xMhZ";
        url.concat("&date=2018-06-12");

        final int imgDefault = getResources().getIdentifier("@drawable/rocket", null, getPackageName());

        Button prevBtn = (Button) findViewById(R.id.prevBtn);
        prevBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                day.add(Calendar.DATE, -1);
                date[0] = "&date=";
                date[0].concat("2018-06-12");
                //date[0].concat(formatter(day));
            }
        });



        final RequestQueue queue = Volley.newRequestQueue(this);

        JsonObjectRequest stringRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {

                            String picurl = response.getString("url");

                            title.setText(response.getString("title"));
                            if(response.has("copyright")) {
                                copyright.setText(response.getString("copyright"));
                            }
                            else {
                                copyright.setText("");
                            }
                            desc.setText(response.getString("explanation"));

                            if(response.has("url")){



                                ImageRequest imgRequest = new ImageRequest(picurl, new Response.Listener<Bitmap>() {
                                    @Override
                                    public void onResponse(Bitmap bmp) {
                                        img.setImageBitmap(bmp);


                                    }
                                }, 0, 0, null, /*Bitmap.Config.RGB_565,*/ new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        img.setImageResource(R.drawable.oops);
                                    }
                                });
                                queue.add(imgRequest);

                                //Bitmap bmp = imgurl.
                                //int imgRes = get
                                //img.
                            }
                            else{
                                img.setImageResource(R.drawable.oops);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        desc.setText("@string/responseError");
                    }
        });

        queue.add(stringRequest);

    }

    public String previousDate(Calendar cal){
        cal.add(cal.DATE, -1);
        Date date = cal.getTime();

        return formatter(date);
    }

    public String formatter(Date date){
        final String pattern = "yyyy-mm-dd";
        SimpleDateFormat ymd = new SimpleDateFormat(pattern);
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

