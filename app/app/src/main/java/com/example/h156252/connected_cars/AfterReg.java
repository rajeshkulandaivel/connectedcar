package com.example.h156252.connected_cars;


import android.app.Activity;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.app.Activity;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Locale;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.speech.RecognizerIntent;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;


import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class AfterReg extends Activity {

    AlertDialogManager alert = new AlertDialogManager();
    SessionManagement session;
    Button btnLogout,btnLogout1;
    GPSTracker gps;
    TextToSpeech tts;
    private final int REQ_CODE_SPEECH_INPUT = 100;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listening);

       /* btnLogout = (Button) findViewById(R.id.btnLogout);
        btnLogout1 = (Button) findViewById(R.id.btnLogout1);*/

       // Session class instance
        session = new SessionManagement(getApplicationContext());
/*
        TextView lblNumber = (TextView) findViewById(R.id.lblNumber);
        TextView lblBrand = (TextView) findViewById(R.id.lblBrand);
        TextView lblColor = (TextView) findViewById(R.id.lblColor);
        TextView lblText = (TextView) findViewById(R.id.lblText);*/

        //Call this function whenever you want to check user login. This will redirect user to LoginActivity is he is not logged in
        session.checkLogin();

        // get user data from session
        HashMap<String, String> user = session.getUserDetails();

        String number = user.get(SessionManagement.KEY_NUMBER);
        String brand = user.get(SessionManagement.KEY_BRAND);
        String color = user.get(SessionManagement.KEY_COLOR);
        String text = user.get(SessionManagement.KEY_TEXT);

      /*  // displaying user data
        lblNumber.setText(Html.fromHtml("Phone No.: <b>" + number + "</b>"));
        lblBrand.setText(Html.fromHtml("Brand: <b>" + brand + "</b>"));
        lblColor.setText(Html.fromHtml("Color: <b>" + color + "</b>"));
        lblText.setText(Html.fromHtml("Text: <b>" + text + "</b>")); */


        new HttpAsyncTask().execute("http://localhost:53804/RestServiceImpl.svc/Nearby_Devices");


        btnLogout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                promptSpeechInput();
            }
        });

        btnLogout1.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                promptSpeechInput();
            }
        });

    }
    public String GET(String url){ //need to add static

        InputStream inputStream = null;
        String result = "";
        JSONObject location = new JSONObject();
        try {

            gps = new GPSTracker(this);

            // check if GPS enabled
            if(gps.canGetLocation()){

                double latitude = gps.getLatitude();
                double longitude = gps.getLongitude();
                String number = session.KEY_NUMBER;
                location.put("number", number);
                location.put("latitude", latitude);
                location.put("longitude", longitude);
            }


            HttpClient client = new DefaultHttpClient();
            URI website = new URI(url);
            HttpPost request = new HttpPost();
            request.setEntity(new StringEntity(location.toString()));
            request.addHeader("content-type", "application/json");
            request.setURI(website);
            HttpResponse response = client.execute(request);
            inputStream = response.getEntity().getContent();
            if(inputStream != null)
                result = convertInputStreamToString(inputStream);
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

    private String ReceiveString(String url)
    {
        InputStream inputStream = null;
        String result = "";
        try {
        HttpClient client = new DefaultHttpClient();
        HttpResponse response = client.execute(new HttpPost(url));
            inputStream = response.getEntity().getContent();
            if(inputStream != null)
                result = convertInputStreamToString(inputStream);
            else
                result = "Did not work!";
        }catch (Exception e)
        {

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
           /* String rr = "Successfully retrieved" + result;
            Toast.makeText(getApplicationContext(),rr,Toast.LENGTH_SHORT).show();*/

        }
    }
    private class ReceiveTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {

            return ReceiveString(urls[0]);
        }

        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            String rr = "Received Message: "  + result;
            Toast.makeText(getApplicationContext(),rr,Toast.LENGTH_SHORT).show();
            tts.speak(rr, TextToSpeech.QUEUE_FLUSH, null);

        }
    }
    private void promptSpeechInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                "What's the message?!");
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(),
                    "Your device does not support speech input",
                    Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Receiving speech input
     * */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && null != data) {

                    ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
                    scheduler.scheduleAtFixedRate(new Runnable() {
                                                      @Override
                                                      public void run() {
                                                          new ReceiveTask().execute("http://localhost:53804/RestServiceImpl.svc/Receiver_Message");
                                                      }
                                                  }, 0,5000, TimeUnit.MILLISECONDS);


                            ArrayList < String > result = data
                                    .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    //textView.setText(result.get(0));
                    Toast.makeText(getApplicationContext(),"Sending message: " + result.get(0).toString(),Toast.LENGTH_LONG ).show();

                }
                break;
            }

        }
    }


}
