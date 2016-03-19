package com.example.h156252.connected_cars;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;

public class RateDriving extends AppCompatActivity {
    SessionManagement session;
    int flag = 0,good_loc = 0,bad_loc= 0;
    String id = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_rate_driving);
        setContentView(R.layout.content_rate_driving);
       // Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);
        session = new SessionManagement(getApplicationContext());

        Intent intent = getIntent();
        id = intent.getStringExtra(CarGrid.EXTRA_MESSAGE);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.like);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                flag = 1;
                //String id = session.pref.getString(session.KEY_ID,"");
                //Intent intent = getIntent();
                //String id = intent.getStringExtra(CarGrid.EXTRA_MESSAGE);
                //Toast.makeText(getApplicationContext(),"Like id :" + id, Toast.LENGTH_SHORT).show();
                new ObtainRateTask().execute("http://connect-car.au-syd.mybluemix.net/api/Items/" + id);


                final Handler h = new Handler();
                final int delay = 2000; //milliseconds


                h.postDelayed(new Runnable() {
                    public void run() {
                        new RateTask().execute("http://connect-car.au-syd.mybluemix.net/api/Items/" + id);

                }
                }, delay);


                h.postDelayed(new Runnable() {
                    public void run() {
                        finish();
                    }
                }, 4000);
                //Toast.makeText(getApplicationContext(),"Like id bef ratetask: " + id, Toast.LENGTH_SHORT).show();
                //new RateTask().execute("http://connect-car.au-syd.mybluemix.net/api/Items/" + id);

            }
        });

        FloatingActionButton fab1 = (FloatingActionButton) findViewById(R.id.dislike);
        fab1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                flag =2 ;
                //String id = session.pref.getString(session.KEY_ID,"");
                //Intent intent = getIntent();
                //String id = intent.getStringExtra(CarGrid.EXTRA_MESSAGE);
                //Toast.makeText(getApplicationContext(),"Dislike id :" + id, Toast.LENGTH_SHORT).show();
                new ObtainRateTask().execute("http://connect-car.au-syd.mybluemix.net/api/Items/" + id);
                //Toast.makeText(getApplicationContext(),"disLike id bef ratetask: " + id, Toast.LENGTH_SHORT).show();

                final Handler h = new Handler();
                final int delay = 2000; //milliseconds
                h.postDelayed(new Runnable() {
                    public void run() {
                        new RateTask().execute("http://connect-car.au-syd.mybluemix.net/api/Items/" + id);

                    }
                }, delay);


                h.postDelayed(new Runnable() {
                    public void run() {
                        finish();
                    }
                }, 4000);
            }
        });

    }


    public String Rateval(String url){
        InputStream inputStream = null;
        JSONObject res = new JSONObject();
        String result = "";

        //String id = session.pref.getString(session.KEY_ID,"");
        String brand = session.KEY_BRAND;

        try {
            JSONObject owner = new JSONObject();
            owner.put("id", id);
            //owner.put("Message", "hello");
            owner.put("isDone", false);
            owner.put("text",brand);
            if(flag==1) {
                //good_loc = good_loc+1;
                String good_new = String.valueOf(good_loc);
                owner.put("good", good_new);
                //flag= 0;
            }
            else if(flag==2) {
                //bad_loc=bad_loc+1;
                String bad_new = String.valueOf(bad_loc);
                owner.put("bad", bad_new);
                //flag= 0;
            }
            //owner.put("mess")
            // owner.put("cartext", cartext);

            HttpClient client = new DefaultHttpClient();
            URI website = new URI(url);
            HttpPut request = new HttpPut();
            request.setEntity(new StringEntity(owner.toString()));
            request.addHeader("content-type", "application/json");
            request.setURI(website);
            HttpResponse response = client.execute(request);
            inputStream = response.getEntity().getContent();
            if(inputStream != null)
                result = convertInputStreamToString(inputStream);
            else
                result = "Did not work!";

        } catch (Exception e) {}


        return result;
    }

    public String GETval(String url){
        InputStream inputStream = null;
        JSONObject res = new JSONObject();
        String result = "";

        try {

            HttpClient client = new DefaultHttpClient();
            URI website = new URI(url);
            HttpGet request = new HttpGet();

            request.addHeader("content-type", "application/json");
            request.setURI(website);
            HttpResponse response = client.execute(request);
            // receive response as inputStream
            inputStream = response.getEntity().getContent();

            // convert inputstream to string
            if(inputStream != null)
            {
                result = convertInputStreamToString(inputStream);

            }
            else
                result = "Did not work!";

        } catch (Exception e) {
            Log.d("InputStream", e.getLocalizedMessage());
        }

        return result;
    }


    private static String convertInputStreamToString(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while((line = bufferedReader.readLine()) != null)
            result += line;

        inputStream.close();
        return result;

    }

    private class RateTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {

            return Rateval(urls[0]);

        }
        @Override
        protected void onPostExecute(String result) {
            try
            {

                String rr = "Success RateTask "+" Good: "+good_loc+" Bad: "+bad_loc+" Flag: "+flag;
                //Toast.makeText(getApplicationContext(), rr, Toast.LENGTH_SHORT).show();


            } catch (Exception e) {
                Log.d("InputStream", e.getLocalizedMessage());
                //Toast.makeText(getApplicationContext(),"exception calling activity - "+e.getLocalizedMessage(),Toast.LENGTH_SHORT).show();
            }

        }
    }


    private class ObtainRateTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {

            return GETval(urls[0]);
        }

        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            String rr = "Success " + result;
            //Toast.makeText(getApplicationContext(), rr, Toast.LENGTH_SHORT).show();
            //result = convertInputStreamToString(inputStream);
           // session = new SessionManagement(getApplicationContext());
            try
            {
                JSONObject jObj = new JSONObject(result);
                //final String id = jObj.getString("id");
                String text = jObj.getString("Message");
                String isDone = jObj.getString("isDone");
                String good_old = jObj.getString("good");
                String bad_old = jObj.getString("bad");
                good_loc = Integer.valueOf(good_old);
                good_loc = good_loc+1;
                bad_loc = Integer.valueOf(bad_old);
                bad_loc = bad_loc+1;
                //Toast.makeText(getApplicationContext(),"OnPostExecute Good: "+good_loc+" BAd: "+ bad_loc+" Flag: "+flag,Toast.LENGTH_SHORT).show();


            } catch (Exception e) {
                Log.d("InputStream", e.getLocalizedMessage());
                //Toast.makeText(getApplicationContext(),"exception calling activity - "+e.getLocalizedMessage(),Toast.LENGTH_SHORT).show();
            }

        }
    }


}
