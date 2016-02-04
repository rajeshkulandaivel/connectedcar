package com.example.h156252.connected_cars;

/**
 * Created by H156252 on 1/20/2016.
 */
import java.util.HashMap;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.widget.Toast;

public class SessionManagement {
    // Shared Preferences
    SharedPreferences pref;

    // Editor for Shared preferences
    Editor editor;

    // Context
    Context _context;

    // Shared pref mode
    int PRIVATE_MODE = 0;

    // Sharedpref file name
    private static final String PREF_NAME = "AndroidHivePref";

    // All Shared Preferences Keys
    private static final String IS_LOGIN = "IsLoggedIn";

    // User name (make variable public to access from outside)
    public static final String KEY_NAME = "name";

    // Email address (make variable public to access from outside)
    public static final String KEY_NUMBER = "number";

    public static final String KEY_BRAND = "brand";

    public static final String KEY_COLOR = "color";

    public static final String KEY_TEXT = "text";

    public static final String KEY_MESSAGE = "";

    public static  String VALUE_NUMBER = "";

    public static  String VALUE_BRAND = "";

    public static  String VALUE_COLOR = "";

    public static  String VALUE_TEXT = "";

    public static String VALUE_MESSAGE = "";

    //public static final String KEY_EMAIL = "email";

    // Constructor
    public SessionManagement(Context context){
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    /**
     * Create login session
     * */
    public void createLoginSession(String number, String brand, String color, String text){




        // Storing login value as TRUE
        editor.putBoolean(IS_LOGIN, true);

        // Storing name in pref
        //editor.putString(KEY_NAME, name);

        // Storing email in pref
        editor.putString(KEY_NUMBER, number);

        editor.putString(KEY_BRAND, brand);

        editor.putString(KEY_COLOR, color);

        editor.putString(KEY_TEXT, text);

        // commit changes
        editor.commit();
    }


    public String getDetails(){
        VALUE_NUMBER = pref.getString(KEY_NUMBER, "");
        VALUE_BRAND = pref.getString(KEY_BRAND,"");
        VALUE_COLOR = pref.getString(KEY_COLOR,"");
        VALUE_TEXT = pref.getString(KEY_TEXT,"");

        return VALUE_NUMBER+"###"+VALUE_BRAND+"###"+VALUE_COLOR+"###"+VALUE_TEXT;
    }

    public String updateMsg(String msg){
        VALUE_MESSAGE = pref.getString(KEY_MESSAGE,"");
        editor.putString(KEY_MESSAGE,VALUE_MESSAGE+"\n\n"+msg);
        editor.commit();
        VALUE_MESSAGE = pref.getString(KEY_MESSAGE,"");
        //Toast.makeText(this.getApplicationContext(),VALUE_MESSAGE,Toast.LENGTH_SHORT).show();
        return VALUE_MESSAGE;
    }

    public String getMsg(){
        VALUE_MESSAGE = pref.getString(KEY_MESSAGE,"");

        return VALUE_MESSAGE;
    }
    public  String getID(){
        VALUE_NUMBER = pref.getString(KEY_NUMBER,"");

        return VALUE_NUMBER;
    }
    /**
     * Check login method wil check user login status
     * If false it will redirect user to login page
     * Else won't do anything
     * */
    public void checkLogin(){
        // Check login status
        if(!this.isLoggedIn()){
            // user is not logged in redirect him to Login Activity
            Intent i = new Intent(_context, LoginActivity.class);
            // Closing all the Activities
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            // Add new Flag to start new Activity
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            // Staring Login Activity
            _context.startActivity(i);
        }

    }



    /**
     * Get stored session data
     * */
    public HashMap<String, String> getUserDetails(){
        HashMap<String, String> user = new HashMap<String, String>();
        // user name
       // user.put(KEY_NAME, pref.getString(KEY_NAME, null));

        // user email id
        user.put(KEY_NUMBER, pref.getString(KEY_NUMBER, null));

        user.put(KEY_BRAND, pref.getString(KEY_BRAND, null));
        user.put(KEY_COLOR, pref.getString(KEY_COLOR, null));
        user.put(KEY_TEXT, pref.getString(KEY_TEXT, null));

        // return user
        return user;
    }

    public void clearHistory(){
        editor.putString(KEY_MESSAGE,"");
        editor.commit();
    }

    /**
     * Clear session details
     * */
    public void logoutUser(){
        // Clearing all data from Shared Preferences
        editor.clear();
        editor.commit();

        // After logout redirect user to Loing Activity
        Intent i = new Intent(_context, LoginActivity.class);
        // Closing all the Activities
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        // Add new Flag to start new Activity
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        // Staring Login Activity
        _context.startActivity(i);
    }

    /**
     * Quick check for login
     * **/
    // Get Login State
    public boolean isLoggedIn(){
        return pref.getBoolean(IS_LOGIN, false);
    }
}
