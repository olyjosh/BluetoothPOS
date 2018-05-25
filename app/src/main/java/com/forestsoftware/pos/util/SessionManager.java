package com.forestsoftware.pos.util;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import java.util.HashMap;

/**
 * Created by HP-PC on 3/23/2018.
 */

public class SessionManager
{
    private SharedPreferences pref;

    private SharedPreferences.Editor editor;


    private Context _context;

    // Shared pref mode
    int PRIVATE_MODE = 0;



    // All Shared Preferences Keys
    private static final String IS_LOGIN = "IsLoggedIn";

    public static final String KEY_TOKEN = "token";
    private static  String TOKEN = "";

    // Constructor
    public SessionManager(Context context){
        this._context = context;
        pref = _context.getSharedPreferences(Constant.PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }


    public void createLoginSession(String name, String token, String email,String fullname, String photo){
        // Storing login value as TRUE
        editor.putBoolean(IS_LOGIN, true);

        // Storing name in pref

        // commit changes
        editor.commit();
        Log.wtf("working now ",""+token);
    }

    /**
     * Check login method wil check user login status
     * If false it will redirect user to login page
     * Else won't do anything
     * */
    public void checkLogin(){
        // Check login status
//        if(!this.isLoggedIn()){
//            // user is not logged in redirect him to Login Activity
//            Intent i = new Intent(_context, LoginActivity.class);
//            // Closing all the Activities
//            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//
//            // Add new Flag to start new Activity
//            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//
//            // Staring Login Activity
//            _context.startActivity(i);
    //    }

    }



    /**
     * Get stored session data
     * */
    public HashMap<String, String> getUserDetails()
    {
        HashMap<String, String> user = new HashMap<String, String>();
        user.put(KEY_TOKEN, pref.getString(KEY_TOKEN, null));
        // return user
        return user;
    }

    /**
     * Clear session details
     * */
    public void logoutUser(){
        // Clearing all data from Shared Preferences
//        editor.clear();
//        editor.commit();
//
//        // After logout redirect user to Loing Activity
//        Intent i = new Intent(_context, LoginActivity.class);
//        // Closing all the Activities
//        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//
//        // Add new Flag to start new Activity
//        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//
//        // Staring Login Activity
//        _context.startActivity(i);
    }

    /**
     * Quick check for login
     * **/
    // Get Login State

    public boolean isLoggedIn()
    {
        Log.wtf("Logged in: ",""+IS_LOGIN);
        return pref.getBoolean(IS_LOGIN, false);
    }

    public static String getTOKEN() {
        return TOKEN;
    }

    public static void setTOKEN(String TOKEN) {
        SessionManager.TOKEN = TOKEN;
    }


    //    public  void setToken(String token)
//    {
//        this.TOKEN = token;
//    }
//    public String getToken()
//    {
//        return TOKEN;
//    }
}
