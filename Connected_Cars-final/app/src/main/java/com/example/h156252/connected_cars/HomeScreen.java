package com.example.h156252.connected_cars;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.location.LocationListener;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.MenuItem;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.ArrayList;
import java.util.Locale;

import android.widget.LinearLayout;

public class HomeScreen extends ListeningActivity implements
        TextToSpeech.OnInitListener {
    AlertDialogManager alert = new AlertDialogManager();
    SessionManagement session;// = new SessionManagement(getApplicationContext());
    Button bt;
    private TextToSpeech tts;
    private LinearLayout content;
    GPSTracker gps;
    String latitude_Str,longitude_Str;
    public final static String EXTRA_MESSAGE = "com.example.h156252.connected_cars.MESSAGE";
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_screen);
        try{
        Window window = getWindow();

// clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

// add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

// finally change the color
        window.setStatusBarColor(Color.rgb(0, 0, 0));
    }
    catch (Exception e){
        //Toast.makeText(getApplicationContext(),"Exception in actionbar  "+e.getLocalizedMessage(),Toast.LENGTH_SHORT).show();
    }
        View view = (View) findViewById(R.id.receive_command);
        tts = new TextToSpeech(this, this);
        int orientation = getResources().getConfiguration().orientation;
        if(orientation == Configuration.ORIENTATION_LANDSCAPE) {
            view.setBackgroundResource (R.drawable.car4);
            LinearLayout l1 = (LinearLayout) findViewById(R.id.linear);
            l1.setOrientation(LinearLayout.HORIZONTAL);
            ((LinearLayout.LayoutParams) l1.getLayoutParams()).gravity = Gravity.CENTER;
        } else {
            view.setBackgroundResource (R.drawable.background);

        }

        session = new SessionManagement(getApplicationContext());
        session.checkLogin();

        bt = (Button)findViewById(R.id.Connect);
        bt.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                new HttpAsyncTask().execute("http://connect-car.au-syd.mybluemix.net/api/Items");
            }
        });




        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent_update = new Intent(getApplicationContext(), UpdateProfile.class);
                startActivity(intent_update);

            }
        });

        FloatingActionButton fab_hist = (FloatingActionButton) findViewById(R.id.fab_hist);
        fab_hist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent_update = new Intent(getApplicationContext(), MsgHistory.class);
                //Intent intent_update = new Intent(getApplicationContext(), TopDriver.class);
                startActivity(intent_update);

            }

        });


        FloatingActionButton fab_rate = (FloatingActionButton) findViewById(R.id.fab_rate);
        fab_rate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Intent intent_rate = new Intent(getApplicationContext(), RateDriving.class);
                Intent intent_rate = new Intent(getApplicationContext(), TopDriver.class);
                startActivity(intent_rate);
            }

        });

        FloatingActionButton fab_alert = (FloatingActionButton) findViewById(R.id.group);
        fab_alert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Intent intent_rate = new Intent(getApplicationContext(), RateDriving.class);
                Intent intent_alert = new Intent(getApplicationContext(), RashDriveAlert.class);
                startActivity(intent_alert);
            }

        });

        content = (LinearLayout)findViewById(R.id.receive_command);
        // The following 3 lines are needed in every onCreate method of a ListeningActivity
        context = getApplicationContext(); // Needs to be set
        VoiceRecognitionListener.getInstance().setListener(this); // Here we set the current listener
        startListening(); // starts listening
        // Toast.makeText(getApplicationContext(),"Listener started!",Toast.LENGTH_SHORT).show();


        final Handler h = new Handler();
        final int delay = 7000; //milliseconds


        h.postDelayed(new Runnable() {
            public void run() {
                String id = session.pref.getString(session.KEY_ID,"");
                //Toast.makeText(getApplicationContext(),"Runnable works "+id,Toast.LENGTH_SHORT).show();
                new VoiceTask().execute("http://connect-car.au-syd.mybluemix.net/api/Items/"+id);
                //new LocationTask().execute("http://myfirst.au-syd.mybluemix.net/api/Items/"+id);
                h.postDelayed(this, delay);
            }
        }, delay);


        h.postDelayed(new Runnable() {
            public void run() {
                String flag_admin = session.getflag();
                if(flag_admin.equals("1"))
                {
                    new AlertTask().execute("http://connect-car.au-syd.mybluemix.net/api/Items/"+"10000000");
                    //Toast.makeText(getApplicationContext(),"You are an admin now",Toast.LENGTH_SHORT).show();
                }
                h.postDelayed(this, delay);
            }
        }, 10000);

        h.postDelayed(new Runnable() {
            public void run() {
                Double latitude, longitude;
                //String latitude_Str,longitude_Str;
                gps = new GPSTracker(HomeScreen.this);

                // check if GPS enabled
                if(gps.canGetLocation()){

                    latitude = gps.getLatitude();
                    longitude = gps.getLongitude();
                    //Toast.makeText(getApplicationContext(),"(Double) Your Location is - \nLat: " + latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show();
                    latitude_Str = new Double(latitude).toString();
                    longitude_Str = new Double(longitude).toString();
                    //Toast.makeText(getApplicationContext(),"(String) Your Location is - \nLat: " + latitude_Str + "\nLong: " + longitude_Str,Toast.LENGTH_LONG).show();
                }
                else
                    Toast.makeText(getApplicationContext(),"Please enable GPS..",Toast.LENGTH_SHORT).show();
                String id = session.pref.getString(session.KEY_ID,"");
                new LocationTask().execute("http://connect-car.au-syd.mybluemix.net/api/Items/"+id);
                h.postDelayed(this, 5000);
            }
        }, 5000);

    }


    @Override
    public void onDestroy() {
        // Don't forget to shutdown tts!
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
        super.onDestroy();
    }



    @Override
    public void onInit(int status) {

        if (status == TextToSpeech.SUCCESS) {

            int result = tts.setLanguage(Locale.US);

            if (result == TextToSpeech.LANG_MISSING_DATA
                    || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS", "This Language is not supported");
            } else {
                }

        } else {
            Log.e("TTS", "Initilization Failed!");
        }

    }

    @Override
    public void onResume()
    {
        super.onResume();
        startListening();
    }

    @Override
    public void processVoiceCommands(String... voiceCommands) {

        for (String command : voiceCommands) {
            String ctxt = "connect car";
            if(ctxt.equals(command))
            {
                new HttpAsyncTask().execute("http://connect-car.au-syd.mybluemix.net/api/Items");
            }


        }
        restartListeningService();
    }


    public String GET(String url){
        InputStream inputStream = null;
        JSONObject res = new JSONObject();
        String result = "";
        JSONObject location = new JSONObject();
        try {


            HttpClient client = new DefaultHttpClient();
            URI website = new URI(url);
            HttpGet request = new HttpGet();
            //request.setEntity(new StringEntity(location.toString()));
            request.addHeader("content-type", "application/json");
            request.setURI(website);
            HttpResponse response = client.execute(request);
            // receive response as inputStream
            inputStream = response.getEntity().getContent();

            // convert inputstream to string
            if(inputStream != null)
            {
                result = convertInputStreamToString(inputStream);
                JSONObject jObj = new JSONObject(result);
                String id = jObj.getString("id");
                String text = jObj.getString("text");
                String isDone = jObj.getString("isDone");
                String result_combine = id + text + isDone;
            }
            else
                result = "Did not work!";

        } catch (Exception e) {
            Log.d("InputStream", e.getLocalizedMessage());
        }

        return result;
    }
    // convert inputstream to String
    private static String convertInputStreamToString(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while((line = bufferedReader.readLine()) != null)
            result += line;

        inputStream.close();
        return result;

    }
    public boolean isConnected(){
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(this.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected())
            return true;
        else
            return false;
    }
    private class HttpAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {

            return GET(urls[0]);
        }

        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            //result = convertInputStreamToString(inputStream);
            String rr = "Success " + result;
            // Toast.makeText(getApplicationContext(), rr, Toast.LENGTH_SHORT).show();

            try
            {
                JSONArray jArray = new JSONArray(result);
            /*String id = jObj.getString("id");
            String text = jObj.getString("text");
            String isDone = jObj.getString("isDone");
            String result_combine = id + " " + text + " " + isDone;
            String rr = "Success " + result_combine;*/
                //Toast.makeText(getApplicationContext(),jArray.toString(),Toast.LENGTH_SHORT).show();
                Intent intent_new = new Intent(getApplicationContext(), CarGrid.class);
                intent_new.putExtra(EXTRA_MESSAGE, jArray.toString());
                //Toast.makeText(getApplicationContext(),"Starting Activity",Toast.LENGTH_SHORT).show();
                startActivity(intent_new);
            } catch (Exception e) {
                Log.d("InputStream", e.getLocalizedMessage());
                //Toast.makeText(getApplicationContext(),"exception calling activity - "+e.getLocalizedMessage(),Toast.LENGTH_SHORT).show();
            }

        }
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
                result = convertInputStreamToString1(inputStream);

            }
            else
                result = "Did not work!";

        } catch (Exception e) {
            Log.d("InputStream", e.getLocalizedMessage());
        }

        return result;
    }


    public String SETval(String url){
        InputStream inputStream = null;
        JSONObject res = new JSONObject();
        String result = "";
        String id = session.pref.getString(session.KEY_ID,"");
        String brand = session.KEY_BRAND;
        //Toast.makeText(getApplicationContext(),"Inside setval "+id,Toast.LENGTH_SHORT).show();
        try {
            JSONObject owner = new JSONObject();
            owner.put("id", id);
            owner.put("Message", "hello");
            owner.put("isDone", false);
            owner.put("text",brand);
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
                result = convertInputStreamToString1(inputStream);
            else
                result = "Did not work!";
      /*  String data = null;
        JSONArray jsonResponse = null;
        data = EntityUtils.toString(entity); //verify
            //Toast.makeText(getApplicationContext(),data,Toast.LENGTH_SHORT);
            //jsonResponse = new JSONArray(data);*/
        } catch (Exception e) {}


        return result;
    }



    // convert inputstream to String
    private static String convertInputStreamToString1(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while((line = bufferedReader.readLine()) != null)
            result += line;

        inputStream.close();
        return result;

    }

    private class VoiceTask extends AsyncTask<String, Void, String> {
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
            session = new SessionManagement(getApplicationContext());
            try
            {
                JSONObject jObj = new JSONObject(result);
                final String id = jObj.getString("id");
                String text = jObj.getString("Message");
                String isDone = jObj.getString("isDone");
                //Toast.makeText(getApplicationContext(),"OnPostExecute works",Toast.LENGTH_SHORT).show();
                if(!(text.equals("hello"))) {
                    //Toast.makeText(getApplicationContext(),text+" unchecked",Toast.LENGTH_SHORT).show();
                    tts.speak("You have a message from Connected Cars     " + text , TextToSpeech.QUEUE_FLUSH, null);
                    Toast.makeText(getApplicationContext(), text.toUpperCase() , Toast.LENGTH_SHORT).show();
                    final Handler h = new Handler();
                    final int delay = 3000; //milliseconds
                    h.postDelayed(new Runnable() {
                        public void run() {
                           // Toast.makeText(getApplicationContext(),"bef resettask" + id,Toast.LENGTH_SHORT).show();
                            try {
                                new ResetTask().execute("http://connect-car.au-syd.mybluemix.net/api/Items/"+id);
                            }catch (Exception e)
                            {
                                //Toast.makeText(getApplicationContext(),e.getLocalizedMessage(),Toast.LENGTH_SHORT).show();
                            }
                        }
                    }, delay);
                    try {
                        String msg = session.updateMsg(text.toUpperCase());
                        //Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
                    }catch (Exception e){
                        //Toast.makeText(getApplicationContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }

                }

            } catch (Exception e) {
                Log.d("InputStream", e.getLocalizedMessage());
                //Toast.makeText(getApplicationContext(),e.getLocalizedMessage(),Toast.LENGTH_SHORT).show();
            }

        }
    }





    public String GETloc(String url) {
        InputStream inputStream = null;
        JSONObject res = new JSONObject();
        String result = "";
        JSONObject location = new JSONObject();
        session = new SessionManagement(this);
        String text = session.KEY_BRAND;
        String id = session.KEY_NUMBER;
        //double latitude=5.5, longitude=5.5;
        //String latitude_Str="*",longitude_Str="*";

        gps = new GPSTracker(HomeScreen.this);

        // check if GPS enabled
        if(gps.canGetLocation()){

            //latitude = gps.getLatitude();
            //longitude = gps.getLongitude();
            //Toast.makeText(getApplicationContext(), "Your Location is - \nLat: " + latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show();
            //latitude_Str = latitude_Str + String.valueOf(latitude);
            //longitude_Str = longitude_Str + String.valueOf(longitude);
            // Toast.makeText(getApplicationContext(), "Your Location is - \nLat: " + latitude_Str + "\nLong: " + longitude_Str, Toast.LENGTH_LONG).show();

            String brand = session.KEY_BRAND;
            String id_new = session.pref.getString(session.KEY_ID,"");
            try {
                location.put("text", brand);
                location.put("isDone", false);
                location.put("id", id_new);
                //location.put("message", "No Message");
                location.put("latitude", latitude_Str);
                location.put("longitude", longitude_Str);
                HttpClient client = new DefaultHttpClient();
                URI website = new URI(url);
                HttpPut request = new HttpPut();
                request.addHeader("content-type", "application/json");
                request.setURI(website);
                request.setEntity(new StringEntity(location.toString()));
                HttpResponse response = client.execute(request);
                // receive response as inputStream
                inputStream = response.getEntity().getContent();

                // convert inputstream to string
                if (inputStream != null) {
                    result = convertInputStreamToString2(inputStream);

                } else
                    result = "Did not work!";

            } catch (Exception e) {
                Log.d("InputStream", e.getLocalizedMessage());
            }
        }
        else
            //Toast.makeText(getApplicationContext(),"Please enable GPS..",Toast.LENGTH_SHORT).show();
            result = "Please enable GPS..";
        return (result);
    }
    // convert inputstream to String
    private static String convertInputStreamToString2(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while((line = bufferedReader.readLine()) != null)
            result += line;

        inputStream.close();
        return result;

    }




    private class ResetTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {

            return SETval(urls[0]);

        }
        @Override
        protected void onPostExecute(String result) {
            String rr = "Success " + result;
            //Toast.makeText(getApplicationContext(), rr, Toast.LENGTH_SHORT).show();
            try
            {
                //Toast.makeText(getApplicationContext(),"Sent",Toast.LENGTH_SHORT).show();


            } catch (Exception e) {
                Log.d("InputStream", e.getLocalizedMessage());
                //Toast.makeText(getApplicationContext(),e.getLocalizedMessage(),Toast.LENGTH_SHORT).show();
            }

        }
    }




    private class LocationTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            //Double latitude = gps.getLatitude();
            // String str_lat = new Double(latitude).toString();
            //Toast.makeText(getApplicationContext(),str_lat,Toast.LENGTH_SHORT).show();
            return GETloc(urls[0]);
        }

        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            String id = session.KEY_ID;
            String id_pref = session.pref.getString(session.KEY_ID,"");
            if(gps.canGetLocation()) {

                Double latitude_post = gps.getLatitude();
                Double longitude_post = gps.getLongitude();
                //Toast.makeText(getApplicationContext(), " "+latitude_post+" "+longitude_post, Toast.LENGTH_SHORT).show();
            }
            String rr = "Success " + result;
            //Toast.makeText(getApplicationContext(), rr, Toast.LENGTH_SHORT).show();
            //result = convertInputStreamToString(inputStream);
          /*  session = new SessionManagement(getApplicationContext());
            try
            {

                JSONObject jObj = new JSONObject(result);
                String id = jObj.getString("id");
                String text = jObj.getString("text");
                String isDone = jObj.getString("isDone");
                String message = jObj.getString("message");
                String latitude = jObj.getString("latitude");
                String longitude = jObj.getString("longitude");
                Toast.makeText(getApplicationContext(),id+"\n"+text+"\n"+isDone+"\n"+message+"\n"+latitude+"\n"+longitude,Toast.LENGTH_SHORT).show();


            } catch (Exception e) {
                Log.d("InputStream", e.getLocalizedMessage());
                Toast.makeText(getApplicationContext(),"exception calling activity - "+e.getLocalizedMessage(),Toast.LENGTH_SHORT).show();
            }*/

        }
    }


    public String GETscore(String url){
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




    private class AlertTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {

            return GETscore(urls[0]);
        }

        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            //String rr = "Success " + result;
            //Toast.makeText(getApplicationContext(), rr, Toast.LENGTH_SHORT).show();
            //result = convertInputStreamToString(inputStream);
            //session = new SessionManagement(getApplicationContext());
            try
            {
                JSONObject jObj = new JSONObject(result);
                String id = jObj.getString("id");
                String good = jObj.getString("good");
                String bad = jObj.getString("bad");
                int good_val = Integer.valueOf(good);
                int bad_val = Integer.valueOf(bad);
                int score = good_val - bad_val;
                if(score<-5)
                {
                    Toast.makeText(getApplicationContext(),"Alert: Driver id: "+id+" Score: "+score+"  Needs regulation"
                            ,Toast.LENGTH_SHORT).show();
                }

            } catch (Exception e) {
                Log.d("InputStream", e.getLocalizedMessage());
                Toast.makeText(getApplicationContext(),e.getLocalizedMessage(),Toast.LENGTH_SHORT).show();
            }

        }
    }








}

