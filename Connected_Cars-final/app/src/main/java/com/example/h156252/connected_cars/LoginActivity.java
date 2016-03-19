package com.example.h156252.connected_cars;

/**
 * Created by H156252 on 1/19/2016.
 */
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.support.v7.app.AppCompatActivity;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
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

public class LoginActivity extends AppCompatActivity {

    String Number, brand, color, cartext,carnum, carno_updated;
    EditText txtNumber, txtBrand, txtColor, txtText,txtcarnum;
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
        try{
            Window window = getWindow();

// clear FLAG_TRANSLUCENT_STATUS flag:
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

// add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

// finally change the color
            window.setStatusBarColor(Color.rgb(128, 0, 0));
            android.support.v7.app.ActionBar actionBar = getSupportActionBar();
            actionBar.setBackgroundDrawable(new ColorDrawable(Color.rgb(128,0,0)));}
        catch (Exception e){
            //Toast.makeText(getApplicationContext(),"Exception in actionbar  "+e.getLocalizedMessage(),Toast.LENGTH_SHORT).show();
        }
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
        txtcarnum = (EditText) findViewById(R.id.carnum);

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
                carnum = txtcarnum.getText().toString();


                // Check if username, password is filled
                if(Number.trim().length() > 0 && brand.trim().length() > 0 && color.trim().length() > 0 && cartext.trim().length() > 0 && carnum.trim().length() > 0 ){

                    String carno = carnum,id="";
                    carno_updated = "";
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

                    session.createLoginSession(carno_updated, Number, carnum, brand, color, cartext);
                    /*
                   Web service access to create new entry in DB
                     */

                    new HttpAsyncTask().execute("http://connect-car.au-syd.mybluemix.net/api/Items");


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
        Double latitude = 0.0, longitude = 0.0;
        int good=0, bad=0;


        //id = Number + carno_updated;
        try {
            JSONObject owner = new JSONObject();
            owner.put("id", carno_updated);
            owner.put("phone", Number);
            owner.put("text", brand);
            owner.put("color",color);
            owner.put("cartext",cartext);
            owner.put("carnum",carnum);
            owner.put("latitude",latitude);
            owner.put("longitude",longitude);
            owner.put("good",good);
            owner.put("bad",bad);
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
            String rr = "Successfully added " + result;
            Toast.makeText(getApplicationContext(),rr,Toast.LENGTH_SHORT).show();

        }
    }
}
