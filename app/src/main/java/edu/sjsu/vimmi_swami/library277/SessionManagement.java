package edu.sjsu.vimmi_swami.library277;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import java.util.HashMap;

/**
 * Created by t0u000c on 12/2/17.
 */

public class SessionManagement {
    // Shared Preferences
    SharedPreferences pref;

    // Editor for Shared preferences
    SharedPreferences.Editor editor;

    // Context
    Context ctx;

    // Shared pref mode
    int PRIVATE_MODE = 0;

    // Sharedpref file name
    private static final String PREF_NAME = "AndroidHivePref";

    // All Shared Preferences Keys
    private static final String IS_LOGIN = "IsLoggedIn";

    // User name (make variable public to access from outside)
    public static final String KEY_TOKEN = "token";

    // Email address (make variable public to access from outside)
    public static final String KEY_EMAIL = "email";

    // Constructor
    public SessionManagement(Context context){
        this.ctx = context;
        pref = ctx.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void saveLoginSession( String email, String token){
        editor.putBoolean(IS_LOGIN,true);
        editor.putString(KEY_EMAIL,email);
        editor.putString(KEY_TOKEN, token);
        editor.commit();
    }

    public HashMap<String, String> getSessionDetails(){
        HashMap<String, String> sessionDetails = new HashMap<String, String>();
        // user email id
        sessionDetails.put(KEY_EMAIL, pref.getString(KEY_EMAIL, null));
        // user token
        sessionDetails.put(KEY_TOKEN, pref.getString(KEY_TOKEN, null));
        // return user
        return sessionDetails;
    }

    public void logoutSession(){
        // Clearing all data from Shared Preferences
        editor.clear();
        editor.commit();

        // After logout redirect user to Loing Activity
        Intent intent = new Intent(ctx, LoginActivity.class);

        ctx.startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
    }

    public void loginValidation(){
        if(!pref.getBoolean(IS_LOGIN,false)){
            Intent intent = new Intent(ctx, LoginActivity.class);
            ctx.startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        }
    }
}
