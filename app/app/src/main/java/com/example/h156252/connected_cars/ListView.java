package com.example.h156252.connected_cars;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.SimpleExpandableListAdapter;

import java.util.ArrayList;
import java.util.HashMap;

public class ListView extends AppCompatActivity {

    private ExpandableListView mOuterList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_list_view);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

      //  mOuterList = (ExpandableListView) findViewById(R.id.gatt_services_list);
     //   mOuterList.setOnChildClickListener(servicesListClickListner);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

 /*   private final ExpandableListView.OnChildClickListener servicesListClickListner =
            new ExpandableListView.OnChildClickListener() {
                @Override
                public boolean onChildClick(ExpandableListView parent, View v, int groupPosition,
                                            int childPosition, long id) {

                    final BluetoothGattCharacteristic characteristic =
                            mGattCharacteristics.get(groupPosition).get(childPosition);
                    final int charaProp = characteristic.getProperties();
                    if ((charaProp | BluetoothGattCharacteristic.PROPERTY_READ) > 0) {
                        // If there is an active notification on a characteristic, clear
                        // it first so it doesn't update the data field on the user interface.

                        mBluetoothLeService.readCharacteristic(characteristic);
                    }

                    return true;
                }
            };*/
    public void displayList()
    {
        ArrayList<HashMap<String, String>> outerlistData = new ArrayList<HashMap<String, String>>();
       // ArrayList<HashMap<String, String>> innerlistData1 = new ArrayList<HashMap<String, String>>();
        ArrayList<ArrayList<HashMap<String, String>>> innerlistData
                = new ArrayList<ArrayList<HashMap<String, String>>>();
        String txt[] = {"1abc","2def","3ghi","4jkl","5mno","6pqr"};
        String id[] = {"1","2","3","4","5","6"};
        for(int i=0; i<5 ; i++)
        {

            HashMap<String, String> currentouterData = new HashMap<String, String>();
            currentouterData.put(
                    "Name", txt[i]);
            currentouterData.put("ID", id[i]);
            outerlistData.add(currentouterData);

            ArrayList<HashMap<String, String>> innerlistGroupData =
                    new ArrayList<HashMap<String, String>>();


            for (int j=0; j<3; j++) {

                HashMap<String, String> currentinnerData = new HashMap<String, String>();
                currentinnerData.put(
                        "Name", txt[i]);
                currentinnerData.put("ID", id[j]);
                innerlistGroupData.add(currentinnerData);
            }

            innerlistData.add(innerlistGroupData);
        }
        SimpleExpandableListAdapter listAdapter = new SimpleExpandableListAdapter(
                this,
                outerlistData,
                android.R.layout.simple_expandable_list_item_2,
                new String[] {"Name","ID"},
                new int[] { android.R.id.text1, android.R.id.text2 },
                innerlistData,
                android.R.layout.simple_expandable_list_item_2,
                new String[] {"Name", "ID"},
                new int[] { android.R.id.text1, android.R.id.text2 });
        mOuterList.setAdapter(listAdapter);
    }

}
