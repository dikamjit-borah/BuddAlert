package com.hobarb.locatadora.utilities;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPrefs {

    SharedPreferences sharedpreferences;
    public SharedPrefs(Context context)
    {
        sharedpreferences = context.getSharedPreferences(CONSTANTS.SHARED_PREF_KEYS.APP_PREFERENCES, Context.MODE_PRIVATE);
    }

    public void writePrefs(String key, String value){
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public String readPrefs(String key ){
        if (sharedpreferences.contains(key))
            return sharedpreferences.getString(key, "");
        else
            return "undefined key";
    }
}
