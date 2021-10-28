package com.applex.verifier.modules.utilities.preferenceManager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;

import com.applex.verifier.modules.utilities.CommonUtils.Constants;
import com.google.gson.Gson;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;

public class PreferenceManager {

    private SharedPreferences preferences;
    private final SharedPreferences.Editor editor;
    private static final String PREF_NAME = "com.applex.authenticationreimagined.users";
    private static final String IS_FIRST_TIME_LAUNCH = "firstTime";
    private static final String IS_FIRST_TIME = "firsttime";
    private final Gson gson;
    private UserModel currentUser;
    private String sharecode;


    @SuppressLint("CommitPrefEdits")
    public PreferenceManager(Context context){
        if(context != null) {
            preferences = context.getSharedPreferences(Constants.PREF_NAME, 0);
        }
        editor = preferences.edit();
        gson = new Gson();
    }

    public void setIsFirstTimeLaunch(boolean firstTimeLaunch) {
        editor.putBoolean(Constants.IS_FIRST_TIME_LAUNCH, firstTimeLaunch);
        editor.commit();
    }

    public boolean isFirstTimeLaunch(){
        return preferences.getBoolean(Constants.IS_FIRST_TIME_LAUNCH,true);
    }

    public void setIsFirstTime(boolean firstTimeLaunch) {
        editor.putBoolean(Constants.IS_FIRST_TIME, firstTimeLaunch);
        editor.commit();
    }

    public boolean isFirstTime(){
        return preferences.getBoolean(Constants.IS_FIRST_TIME,true);
    }

    public void setCurrentUser(UserModel userModel) {
        Gson gson = new Gson();
        String userJson = gson.toJson(userModel);
        editor.putString(Constants.PROPERTY_CURRENT_USER, userJson);
        editor.apply();
    }

    public UserModel getCurrentUser(){
        Gson gson = new Gson();
        String json = preferences.getString(Constants.PROPERTY_CURRENT_USER, "");
        return gson.fromJson(json, UserModel.class);
    }

    public void setSharecode(String code) {
        editor.putString(Constants.SHARECODE, code);
        editor.apply();
    }

    public String getSharecode(){
        return preferences.getString(Constants.SHARECODE,null);
    }


    public static class UriSerializer implements JsonSerializer<Uri> {
        public JsonElement serialize(Uri src, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(src.toString());
        }
    }

    public static class UriDeserializer implements JsonDeserializer<Uri> {
        @Override
        public Uri deserialize(final JsonElement src, final Type srcType,
                               final JsonDeserializationContext context) throws JsonParseException {
            return Uri.parse(src.getAsString());
        }
    }
}
