package com.example.h156252.connected_cars;

import android.content.Intent;
import android.content.SharedPreferences;
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
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
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
import java.util.Set;

public class RashDriveAlert extends AppCompatActivity {
    EditText et_member;
    Button bt_add,bt_grp;
    String member;
    String  carno, carno_updated;
    ArrayList<String> listdata;
    SessionManagement session;
    SharedPreferences pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_rash_drive_alert);
       // et_member = (EditText).findViewById(R.id.member_edit);
        et_member = (EditText) findViewById(R.id.member_edit);
        listdata = new ArrayList<String>();
        session = new SessionManagement(getApplicationContext());
        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);
        bt_add = (Button) findViewById(R.id.Member);
        bt_add.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                carno = et_member.getText().toString();
                int len = carno.length();
                for(int i=0;i<len;i++)
                {
                    if(carno.charAt(i)=='1'||carno.charAt(i)=='2'||carno.charAt(i)=='3'||carno.charAt(i)=='4'||carno.charAt(i)=='5'||carno.charAt(i)=='6'||carno.charAt(i)=='7'||carno.charAt(i)=='8'||carno.charAt(i)=='9'||carno.charAt(i)=='0')
                    {
                        String a= String.valueOf(carno.charAt(i));
                        carno_updated = carno_updated + a;
                    }
                    else
                    {
                        char character1 = carno.charAt(i);
                        int ascii1 = (int) character1;
                        String ascii_str = Integer.toString(ascii1);
                        carno_updated = carno_updated + ascii_str;
                    }
                }

                //new HttpAsyncTask().execute("http://connect-car.au-syd.mybluemix.net/api/Items");


            }});
        //String Group_created = "";
        bt_grp = (Button) findViewById(R.id.Group);
        bt_grp.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                try{
                    session.setflag("1");
                }
                catch (Exception e)
                {
                    Toast.makeText(getApplicationContext(),e.getLocalizedMessage(),Toast.LENGTH_SHORT).show();
                }


                Toast.makeText(getApplicationContext(),"Group created",Toast.LENGTH_SHORT).show();
                //new AlertTask().execute("http://connect-car.au-syd.mybluemix.net/api/Items/"+"10000000");
            }});

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
                //JSONObject jObj = new JSONObject(result);
               /* String id = jObj.getString("id");
                String text = jObj.getString("text");
                String isDone = jObj.getString("isDone");
                String result_combine = id + text + isDone;*/
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

            try {

                JSONArray jArray = new JSONArray(result);
                if (jArray != null) {
                    for (int i = 0; i < jArray.length(); i++) {

                        String jstr = jArray.get(i).toString();

                        JSONObject jObj = new JSONObject(jstr);
                        String id_car = jObj.getString("id");
                        //String phone = jObj.getString("phone");
                        //String brand = jObj.getString("text");
                        //String text = jObj.getString("cartext");
                        String carnumber = jObj.getString("carnum");
                        if(!(id_car.equals(carno_updated)))
                        {
                         //   Toast.makeText(getApplicationContext(),"This user is not available in our app",Toast.LENGTH_SHORT).show();
                        }
                        else{
                            //int f =1;
                            listdata.add(carnumber);
                        }

                    }
                }
                Toast.makeText(getApplicationContext(),"The members in the group are "+listdata,Toast.LENGTH_SHORT).show();
            }
            catch (Exception e)
            {
                //Toast.makeText(getApplicationContext(),e.getLocalizedMessage(),Toast.LENGTH_SHORT).show();
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

            return GETval(urls[0]);
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
