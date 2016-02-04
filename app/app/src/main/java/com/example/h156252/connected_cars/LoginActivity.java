package com.example.h156252.connected_cars;

/**
 * Created by H156252 on 1/19/2016.
 */
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Calendar;

public class LoginActivity extends Activity {

    String Number, brand, color, cartext;
    EditText txtNumber, txtBrand, txtColor, txtText;
    // login button
    Button btnLogin;

    // Alert Dialog Manager
    AlertDialogManager alert = new AlertDialogManager();

    // Session Manager Class
    SessionManagement session;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //GPS update every 5seconds
        // Session Manager
      /*  Intent myIntent = new Intent(LoginActivity.this, MyAlarmService.class);
        PendingIntent pendingIntent = PendingIntent.getService(LoginActivity.this, 0, myIntent, 0);
        AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        //calendar.add(Calendar.SECOND, 10);
        //alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), 5 * 1000, pendingIntent);
        Toast.makeText(LoginActivity.this, "Location synced..", Toast.LENGTH_LONG).show();
*/


        session = new SessionManagement(getApplicationContext());

        txtNumber = (EditText) findViewById(R.id.txtNumber);
        txtBrand = (EditText) findViewById(R.id.txtBrand);
        txtColor = (EditText) findViewById(R.id.txtColor);
        txtText = (EditText) findViewById(R.id.txtText);
        txtText = (EditText) findViewById(R.id.txtText);

       // Toast.makeText(getApplicationContext(), "User Login Status: " + session.isLoggedIn(), Toast.LENGTH_LONG).show();


        // Login button
        btnLogin = (Button) findViewById(R.id.btnLogin);


        // Login button click event
        btnLogin.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                Number = txtNumber.getText().toString();
                brand = txtBrand.getText().toString();
                color = txtColor.getText().toString();
                cartext = txtText.getText().toString();



                // Check if username, password is filled
                if(Number.trim().length() > 0 && brand.trim().length() > 0 && color.trim().length() > 0 && cartext.trim().length() > 0 ){

                        session.createLoginSession(Number, brand, color, cartext);
                    /*
                   Web service access to create new entry in DB
                     */

                    new HttpAsyncTask().execute("http://myfirst.au-syd.mybluemix.net/api/Items");

                        // Staring MainActivity
                       // Intent i = new Intent(getApplicationContext(), AfterReg.class);
                      //  startActivity(i);
                        finish();

                }else{
                    // user didn't entered username or password
                    // Show alert asking him to enter the details
                    alert.showAlertDialog(LoginActivity.this, "Registration failed..", "Fill all details", false);
                }

            }
        });
    }


    public String GET(String url){ //need to add static

        InputStream inputStream = null;
        String result = "";
        try {
        JSONObject owner = new JSONObject();
        owner.put("id", Number);
        owner.put("text", brand);
        owner.put("isDone", false);
        owner.put("Message","hello");
       // owner.put("cartext", cartext);

        HttpClient client = new DefaultHttpClient();
        URI website = new URI(url);
        HttpPost request = new HttpPost();
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
            //String rr = "Successfully added " + result;
            //Toast.makeText(getApplicationContext(),rr,Toast.LENGTH_SHORT).show();

        }
    }
}
