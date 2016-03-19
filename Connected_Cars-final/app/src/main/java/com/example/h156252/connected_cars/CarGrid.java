package com.example.h156252.connected_cars;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v7.app.AppCompatActivity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
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
import android.speech.tts.TextToSpeech;

public class CarGrid extends AppCompatActivity implements
        TextToSpeech.OnInitListener{

    private final int REQ_CODE_SPEECH_INPUT = 100;
    private TextToSpeech tts;
    public String message_to_be_sent;
    public String receiver_id = "";
    SessionManagement session;
    public final static String EXTRA_MESSAGE = "com.example.h156252.connected_cars.MESSAGE";
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_car_grid);
        try{
            Window window = getWindow();

// clear FLAG_TRANSLUCENT_STATUS flag:
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

// add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

// finally change the color
            window.setStatusBarColor(Color.rgb(0,0,0));
            android.support.v7.app.ActionBar actionBar = getSupportActionBar();
            actionBar.setBackgroundDrawable(new ColorDrawable(Color.rgb(0,0,0)));}
        catch (Exception e){
           // Toast.makeText(getApplicationContext(),"Exception in actionbar  "+e.getLocalizedMessage(),Toast.LENGTH_SHORT).show();
        }
        session = new SessionManagement(this);
        tts = new TextToSpeech(this, this);
        //Toast.makeText(getApplicationContext(),"Entering activity",Toast.LENGTH_SHORT).show();
        Intent intent = getIntent();
        String result = intent.getStringExtra(HomeScreen.EXTRA_MESSAGE);
        //Toast.makeText(getApplicationContext(),"obtaining result: " +result,Toast.LENGTH_SHORT).show();
        ArrayList<String> listdata = new ArrayList<String>();

    try {

    JSONArray jArray = new JSONArray(result);
        if (jArray != null) {
            for (int i = 0; i < jArray.length(); i++) {

                String jstr = jArray.get(i).toString();
                JSONObject jObj = new JSONObject(jstr);
                String id = jObj.getString("id");
                //String phone = jObj.getString("phone");
                String brand = jObj.getString("text");
                String text = jObj.getString("cartext");
                String carno = jObj.getString("carnum");
                String color = jObj.getString("color");
                //String isDone = jObj.getString("isDone");
                String result_combine = "CAR " + i + ":\nCar id : #" + id + "*\n" + "License No.   : " + carno + "\n" + "Color   : " + color + "\n" + "Brand   : " + brand + "\n" + "Text behind car   : " + text;
                String rr = "Success " + result_combine;
                //Toast.makeText(getApplicationContext(),rr,Toast.LENGTH_SHORT).show();
                String own_id = session.getID();
                if(!(id.equals(own_id)))
                    listdata.add(result_combine);
            }
        }
    }
    catch (Exception e)
    {
        //Toast.makeText(getApplicationContext(),e.getLocalizedMessage(),Toast.LENGTH_SHORT).show();
    }

        //Toast.makeText(getApplicationContext(),"out of json",Toast.LENGTH_SHORT).show();
        final GridView gridview = (GridView) findViewById(R.id.gridview);
       // final String[] items = new String[] { "Item1", "Item2", "Item3","Item4", "Item5", "Item6", "Item7", "Item8" };

        ArrayAdapter<String> ad = new ArrayAdapter<String>(
                getApplicationContext(), android.R.layout.simple_list_item_1,
                listdata);
        //Toast.makeText(getApplicationContext(),"setting array adapter",Toast.LENGTH_SHORT).show();
        //gridview.setBackgroundColor(Color.GRAY);

        gridview.setNumColumns(2);
        gridview.setGravity(Gravity.CENTER);
        gridview.setAdapter(ad);
        gridview.setBackgroundColor(Color.GRAY);
        //Toast.makeText(getApplicationContext(),"setting grid view",Toast.LENGTH_SHORT).show();
        gridview.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View v, int position,
                                    long arg3) {
                // TODO Auto-generated method stub
               // Toast.makeText(getApplicationContext(), "" + arg2,Toast.LENGTH_SHORT).show();
                //Toast.makeText(getApplicationContext(),"Prompting speech",Toast.LENGTH_SHORT).show();


               try {
                   String s = ((TextView) v).getText().toString();

                   int start = 0; // '(' position in string
                   int end = 0; // ')' position in string
                   for (int i = 0; i < s.length(); i++) {
                       if (s.charAt(i) == '#') // Looking for '(' position in string
                           start = i;
                       else if (s.charAt(i) == '*') // Looking for ')' position in  string
                           end = i;
                   }
                   receiver_id = s.substring(start + 1, end);

                   //Toast.makeText(getApplicationContext(), "ID: " + receiver_id, Toast.LENGTH_SHORT).show();
                   //promptSpeechInput();

               }catch (Exception e)
               {
                  // Toast.makeText(getApplicationContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
               }

                //receiver_id = grid_text.substring(grid_text.indexOf("#") + 1, grid_text.indexOf("#"));
                //Toast.makeText(getApplicationContext(),receiver_id,Toast.LENGTH_SHORT).show();

                try{
                promptSpeechInput();
                }
                catch(Exception e){
                //Toast.makeText(getApplicationContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                }


                try{
                    Intent intent_rate = new Intent(getApplicationContext(), RateDriving.class);
                    intent_rate.putExtra(EXTRA_MESSAGE, receiver_id);
                    startActivity(intent_rate);

                }
                catch(Exception e){
                    //Toast.makeText(getApplicationContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }


                //new VoiceTask().execute("http://myfirst.au-syd.mybluemix.net/api/Items");
            }
        });

    }
    /**
     * Showing google speech input dialog
     * */
    private void promptSpeechInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                "Whats your message?!");
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(),
                    "Speech not supported",
                    Toast.LENGTH_SHORT).show();
        }
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
                /*btnSpeak.setEnabled(true);
                speakOut();*/
            }

        } else {
            Log.e("TTS", "Initilization Failed!");
        }

    }

  /*  private void speakOut() {

        String text = txtText.getText().toString();

        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
    }*/

    /**
     * Receiving speech input
     * */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && null != data) {

                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    message_to_be_sent = result.get(0);
                  /*  Toast.makeText(getApplicationContext(),
                            "Sending message: " + message_to_be_sent.toUpperCase(),
                            Toast.LENGTH_SHORT).show();*/
                    new VoiceTask().execute("http://connect-car.au-syd.mybluemix.net/api/Items/"+receiver_id);
                }
                break;
            }

        }
    }





//-------------------------------------------------------------------------------------------------------------------------------------

    private class VoiceTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {

            return GETval(urls[0]);

        }

        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            //result = convertInputStreamToString(inputStream);

            try
            {
                Toast.makeText(getApplicationContext(),"Sent",Toast.LENGTH_SHORT).show();



            } catch (Exception e) {
                Log.d("InputStream", e.getLocalizedMessage());
                //Toast.makeText(getApplicationContext(),e.getLocalizedMessage(),Toast.LENGTH_SHORT).show();
            }

        }
    }

    public String GETval(String url){
        InputStream inputStream = null;
        JSONObject res = new JSONObject();
        String result = "";
        String brand =  session.KEY_BRAND;

        try {
            JSONObject owner = new JSONObject();
            owner.put("id", receiver_id);
            owner.put("text",brand );
            owner.put("Message",message_to_be_sent );
            owner.put("isDone", false);
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
      /*  String data = null;
        JSONArray jsonResponse = null;
        data = EntityUtils.toString(entity); //verify
            //Toast.makeText(getApplicationContext(),data,Toast.LENGTH_SHORT);
            //jsonResponse = new JSONArray(data);*/
        } catch (Exception e) {}


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








}