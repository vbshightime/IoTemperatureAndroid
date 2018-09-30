package com.example.webczar.takenotes.SharedPreferanceHelper;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.webczar.takenotes.Model.User;

/**
 * Created by webczar on 9/29/2018.
 */

public class SharedPreferanceHelper {

    private static SharedPreferanceHelper instance = null;
    private static SharedPreferences sharedPreferences;
    private static SharedPreferences.Editor editor;

    private static String MY_PREFERENCES = "preferances";
    private static String SHARE_KEY_DEVICE = "device";
    private static String SHARE_KEY_SENSOR = "sensor";
    private static String SHARE_KEY_TEMP = "temp";
    private static String SHARE_KEY_HUMID = "humid";

    public SharedPreferanceHelper() {
    }

    public static SharedPreferanceHelper getInstance(Context context) {

        if (instance == null){
            instance = new SharedPreferanceHelper();
            sharedPreferences = context.getSharedPreferences(MY_PREFERENCES,context.MODE_PRIVATE);
            editor = sharedPreferences.edit();
        }
        return instance;
    }

    public void saveUserInfo(User user){

        editor.putString(SHARE_KEY_DEVICE, user.device);
        editor.putString(SHARE_KEY_TEMP, user.temp_value);
        editor.putString(SHARE_KEY_SENSOR,user.sensor);
        editor.putString(SHARE_KEY_HUMID, user.humid_value);
        editor.apply();
    }

    public User getUserSavedValue(){
        String device = sharedPreferences.getString(SHARE_KEY_DEVICE, "");
        String temp = sharedPreferences.getString(SHARE_KEY_TEMP, "");
        String sensor = sharedPreferences.getString(SHARE_KEY_SENSOR, "");
        String humid = sharedPreferences.getString(SHARE_KEY_HUMID,"");



        User user = new User();
        user.device =device;
        user.temp_value = temp;
        user.humid_value =humid;
        user.sensor =sensor;
        return user;
    }
}
