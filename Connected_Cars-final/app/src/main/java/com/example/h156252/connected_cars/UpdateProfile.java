package com.example.h156252.connected_cars;

import android.app.ActionBar;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;

public class UpdateProfile extends AppCompatActivity {
    EditText edtNumber, edtText, edtBrand, edtColor;
    SessionManagement session;
    Button btUpdate;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.update_profile);
        try{
            Window window = getWindow();

// clear FLAG_TRANSLUCENT_STATUS flag:
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

// add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

// finally change the color
            window.setStatusBarColor(Color.rgb(128,0,0));
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.rgb(128,0,0)));}
        catch (Exception e){
            //Toast.makeText(getApplicationContext(),"Exception in actionbar  "+e.getLocalizedMessage(),Toast.LENGTH_SHORT).show();
        }
        session = new SessionManagement(this);
        edtNumber = (EditText)findViewById(R.id.txtNumber);
        edtText = (EditText)findViewById(R.id.txtText);
        edtBrand = (EditText)findViewById(R.id.txtBrand);
        edtColor = (EditText)findViewById(R.id.txtColor);
        try {
            String details = session.getDetails();
            String[] detail_list = details.split("###");

            edtNumber.setText(detail_list[0]);
            edtBrand.setText(detail_list[1]);
            edtColor.setText(detail_list[2]);
            edtText.setText(detail_list[3]);
        }
        catch(Exception e)
        {
            //Toast.makeText(getApplicationContext(),e.getLocalizedMessage(),Toast.LENGTH_SHORT).show();
        }

        btUpdate = (Button)findViewById(R.id.btnUpdate);
        btUpdate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                String number = edtNumber.getText().toString();
                String id = session.KEY_ID;
                new HttpAsyncTask().execute("http://connect-car.au-syd.mybluemix.net/api/Items/"+id);
                finish();
            }
            });

    }
    public String GET(String url){ //need to add static
        String Number = edtNumber.getText().toString();
        String brand = edtBrand.getText().toString();
        String id = session.KEY_ID;
        //verify
        session.VALUE_BRAND = brand;

        InputStream inputStream = null;
        String result = "";
        try {
            JSONObject owner = new JSONObject();
            owner.put("id", id);
            owner.put("phone",Number);
            owner.put("text", brand);
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
            //Toast.makeText(getApplicationContext(), rr, Toast.LENGTH_SHORT).show();

        }
    }
}
