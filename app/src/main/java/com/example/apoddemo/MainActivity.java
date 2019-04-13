package com.example.apoddemo;

import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
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

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ImageView img = (ImageView) findViewById(R.id.podImg);
        final int imgDefault = getResources().getIdentifier("@drawable/rocket", null, getPackageName());
        img.setImageResource(imgDefault);

        TextView desc = (TextView) findViewById(R.id.imageDescription);
        desc.setMovementMethod(new ScrollingMovementMethod());
    }

    public void fetchInfo(View view) {
        final TextView title = (TextView) findViewById(R.id.imageTitle);
        final TextView copyright = (TextView) findViewById(R.id.imageCopyright);
        final TextView desc = (TextView) findViewById(R.id.imageDescription);

        final ImageView img = (ImageView) findViewById(R.id.podImg);

        //final int imgDefault = getResources().getIdentifier("@drawable/rocket", null, getPackageName());

        RequestQueue queue = Volley.newRequestQueue(this);
        //String url = "https://www.google.com";
        //String url = "https://api.nasa.gov/planetary/apod?api_key=DEMO_KEY";
        String url = "https://api.nasa.gov/planetary/apod?api_key=hCcahvUhc0xMW2H2mox6vYpS7jKPU2SM1Rv5xMhZ";

        JsonObjectRequest stringRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {

                            String imgurl = response.getString("url");

                            title.setText(response.getString("title"));
                            desc.setText(response.getString("explanation"));

                            if(response.has("copyright")) {
                                copyright.setText(response.getString("copyright"));
                            }
                            else {
                                copyright.setText("");
                            }

                            if(response.has("url")){

                                ImageRequest imgreq = new ImageRequest(imgurl, new Response.Listener<Bitmap>() {
                                    @Override
                                    public void onResponse(Bitmap bmp) {
                                        img.setImageBitmap(bmp);

                                    }
                                }, 0, 0, null, Bitmap.Config.RGB_565, new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        img.setImageResource(R.drawable.oops);
                                    }
                                });

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

}

