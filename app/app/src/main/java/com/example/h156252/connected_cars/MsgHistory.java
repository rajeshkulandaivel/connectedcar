package com.example.h156252.connected_cars;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MsgHistory extends AppCompatActivity {
    SessionManagement session;
    TextView tv;
    FloatingActionButton fab;
    Button bthistory;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.history);

        tv = (TextView)findViewById(R.id.history);
        fab = (FloatingActionButton) findViewById(R.id.clear);
      /*  bthistory = (Button) findViewById(R.id.History);
        bthistory.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                try{
                    Toast.makeText(getApplicationContext(), "Entered fab", Toast.LENGTH_SHORT).show();
                    session.clearHistory();
                    String msg = session.getMsg();
                    tv.setText("No history");}
                catch (Exception e){
                    Toast.makeText(getApplicationContext(), "exception in clearhistory()- history " + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });*/
       /* Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);*/
        //Toast.makeText(getApplicationContext(), "Entered activity", Toast.LENGTH_SHORT).show();
        try{
            session = new SessionManagement(this);
            String msg = session.getMsg();
            tv.setText(msg);
        }
        catch (Exception e){
            Toast.makeText(getApplicationContext(), "exception in getMsg()- history " + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.clear);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                    //Toast.makeText(getApplicationContext(), "Entered fab", Toast.LENGTH_SHORT).show();
                    session.clearHistory();
                    String msg = session.getMsg();
                    tv.setText(msg);}
                catch (Exception e){
                    Toast.makeText(getApplicationContext(), "exception in clearhistory()- history " + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}
