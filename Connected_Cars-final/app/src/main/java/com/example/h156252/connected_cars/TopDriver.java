package com.example.h156252.connected_cars;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import android.webkit.WebView;

public class TopDriver extends AppCompatActivity {
    WebView browser;
    String web_output = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.webpage);
        try{
            Window window = getWindow();

// clear FLAG_TRANSLUCENT_STATUS flag:
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

// add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

// finally change the color
            window.setStatusBarColor(Color.rgb(0, 0, 0));
            //android.support.v7.app.ActionBar actionBar = getSupportActionBar();
            //actionBar.setBackgroundDrawable(new ColorDrawable(Color.rgb(0,0,0)));
            }
        catch (Exception e){
            //Toast.makeText(getApplicationContext(),"Exception in actionbar  "+e.getLocalizedMessage(),Toast.LENGTH_SHORT).show();
        }
        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);
        new HttpAsyncTask().execute("http://connect-car.au-syd.mybluemix.net/api/Items");
        /*FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new HttpAsyncTask().execute("http://connect-car.au-syd.mybluemix.net/api/Items");
            }
        });*/
        browser=(WebView)findViewById(R.id.webkit);
        //Toast.makeText(getApplicationContext(),web_output,Toast.LENGTH_SHORT).show();
        //browser.loadData("<html><body>Hello World!</body></html>", "text/html", "UTF-8");
       // browser.loadData("<html><body>"+ web_output +"</body></html>","text/html", "UTF-8");
    }


    public String GET(String url){
        InputStream inputStream = null;
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
    public static <K, V extends Comparable<? super V>> Map<K, V>
    sortByValue( Map<K, V> map )
    {
        List<Map.Entry<K, V>> list =
                new LinkedList<Map.Entry<K, V>>( map.entrySet() );
        Collections.sort(list, new Comparator<Map.Entry<K, V>>() {
            public int compare(Map.Entry<K, V> o1, Map.Entry<K, V> o2) {
                return (o1.getValue()).compareTo(o2.getValue());
            }
        });

        Map<K, V> result = new LinkedHashMap<K, V>();
        for (Map.Entry<K, V> entry : list)
        {
            result.put( entry.getKey(), entry.getValue() );
        }
        return result;
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
            //String rr = "Success " + result;
            // Toast.makeText(getApplicationContext(), rr, Toast.LENGTH_SHORT).show();

            try
            {
                JSONArray jArray = new JSONArray(result);
                int good_int, bad_int,score;
                String best = "Best drive: \n", worst = "Worst drive: \n";
                //ArrayList<String> listdata = new ArrayList<String>();
                Map<String, Integer> testMap = new HashMap<String, Integer>(50);
                Map<String, Integer> testMap_sort = new HashMap<String, Integer>(50);
                try {

                    //JSONArray jArray = new JSONArray(result);
                    if (jArray != null) {
                        for (int i = 0; i < jArray.length(); i++) {

                            String jstr = jArray.get(i).toString();
                            JSONObject jObj = new JSONObject(jstr);
                            String id = jObj.getString("id");
                            String good = jObj.getString("good");
                            String bad = jObj.getString("bad");
                            good_int = Integer.valueOf(good);
                            bad_int = Integer.valueOf(bad);
                            score = good_int - bad_int;
                            testMap.put(id,score);
                            //Toast.makeText(getApplicationContext(),rr,Toast.LENGTH_SHORT).show();
                            //String own_id = session.getID();
                            //if(!(id.equals(own_id)))
                                //listdata.add(result_combine);
                        }
                        testMap_sort = sortByValue( testMap );
                        //Toast.makeText(getApplicationContext(),testMap.toString(),Toast.LENGTH_SHORT).show();
                        int i= 1;
                        String testMap_list = "";
                        for (Map.Entry<String, Integer> entry : testMap_sort.entrySet())
                        {
                            testMap_list = testMap_list+"<p>"+i + ". " + entry.getKey() + "    -    " + entry.getValue()+"</p>";
                            //System.out.println(entry.getKey() + "/" + entry.getValue());
                            i++;
                        }

                        //Toast.makeText(getApplicationContext(),testMap_list,Toast.LENGTH_SHORT).show();

                        int maxValueInMap=(Collections.max(testMap.values()));  // This will return max value in the Hashmap
                        for (Map.Entry<String, Integer> entry : testMap.entrySet()) {  // Itrate through hashmap
                            if (entry.getValue()==maxValueInMap) {
                                best = best +"<p>"+ entry.getKey() +" with score "+entry.getValue()+"</p>";
                                //Toast.makeText(getApplicationContext(),"Best drive: "+entry.getKey() +" with score "+entry.getValue(),Toast.LENGTH_SHORT).show();
                               // System.out.println(entry.getKey());     // Print the key with max value
                            }
                        }

                        int minValueInMap=(Collections.min(testMap.values()));  // This will return max value in the Hashmap
                        for (Map.Entry<String, Integer> entry : testMap.entrySet()) {  // Itrate through hashmap
                            if (entry.getValue()==minValueInMap) {
                                worst =worst +"<p>"+ entry.getKey() +" with score "+entry.getValue()+"</p>";
                               // Toast.makeText(getApplicationContext(),"Worst drive: "+entry.getKey() +" with score "+entry.getValue(),Toast.LENGTH_SHORT).show();
                                //System.out.println(entry.getKey());     // Print the key with max value
                            }
                        }
                        web_output =  best + "\n" + worst + "\n"+ testMap_list ;
                        //Toast.makeText(getApplicationContext(),web_output,Toast.LENGTH_SHORT).show();
                        browser.loadData("<html><head><title>Connected Cars Survey:</title></head><body><p><b>'Connected Cars' User Scores:</b></p><p>"+best+"</p><p>"+worst+"</p><p>List of users:</p><p>"+testMap_list+"</p></body></html>","text/html", "UTF-8");
                    }

                }
                catch (Exception e)
                {
                    //Toast.makeText(getApplicationContext(),e.getLocalizedMessage(),Toast.LENGTH_SHORT).show();
                }
                //Toast.makeText(getApplicationContext(),jArray.toString(),Toast.LENGTH_SHORT).show();
                //Intent intent_new = new Intent(getApplicationContext(), CarGrid.class);
                //intent_new.putExtra(EXTRA_MESSAGE, jArray.toString());
                //Toast.makeText(getApplicationContext(),"Starting Activity",Toast.LENGTH_SHORT).show();
                //startActivity(intent_new);
            } catch (Exception e) {
                Log.d("InputStream", e.getLocalizedMessage());
                //Toast.makeText(getApplicationContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }

        }
    }



}
