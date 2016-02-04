package com.example.h156252.connected_cars;

import android.os.Bundle;
import android.widget.LinearLayout;

public class MainActivity extends ListeningActivity {

    private LinearLayout content;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        content = (LinearLayout)findViewById(R.id.commands);

        // The following 3 lines are needed in every onCreate method of a ListeningActivity
        context = getApplicationContext(); // Needs to be set
        VoiceRecognitionListener.getInstance().setListener(this); // Here we set the current listener
        startListening(); // starts listening
    }

    // Here is where the magic happens
    @Override
    public void processVoiceCommands(String... voiceCommands) {
        content.removeAllViews();
        for (String command : voiceCommands) {
            String ctxt = "hello";
            if(ctxt.equals(command))
            {

            }
               /* TextView txt = new TextView(getApplicationContext());
                txt.setText(command);
                txt.setTextSize(20);
                txt.setGravity(Gravity.CENTER);
            String ctxt = "hello";
            String comm = command;
            //boolean res = ctxt.equals(comm);
            //String result = res.toString();
            if(ctxt.equals(comm))
            {
                //Toast.makeText(getApplicationContext(),res,Toast.LENGTH_SHORT).show();
                txt.setTextColor(Color.GREEN);
            }
            else {
                Toast.makeText(getApplicationContext(),"Command found: "+ ctxt + " " + command,Toast.LENGTH_SHORT).show();
                txt.setTextColor(Color.BLUE);
            }
                content.addView(txt);*/

        }
        restartListeningService();
    }
}
