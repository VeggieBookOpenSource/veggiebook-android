//  Copyright © 2020 Quick Help For Meals, LLC. All rights reserved.
//
//  This file is part of VeggieBook.
//
//  VeggieBook is free software: you can redistribute it and/or modify
//  it under the terms of the GNU General Public License as published by
//  the Free Software Foundation, version 3 of the license only.
//
//  VeggieBook is distributed in the hope that it will be useful,
//  but WITHOUT ANY WARRANTY; without even the implied warranty of
//  MERCHANTABILITY or fitness for a particular purpose. See the
//  GNU General Public License for more details.

package com.veggiebook.android;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.http.HttpResponseCache;
import android.util.Log;
import android.view.ViewConfiguration;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.veggiebook.model.BookManager;
import com.veggiebook.model.Pantry;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.List;

import roboguice.RoboGuice;

/**
 * Created with IntelliJ IDEA.
 * User: danieldipasquo
 * Date: 2/26/13
 * Time: 3:26 PM
 *
 */
public class AppSettings extends Application {
    private static Context context;
    private static final String PREF_FILE = "com.veggiebook.prefFile";
    private static final String PROFILE_ID_KEY = "profileId";
    private static final String GOOGLE_ACCOUNT = "googleAccount";
    private static final String AUTH_TOKEN = "authToken";
    private static final String FIRST_NAME = "firstName";
    private static final String LAST_NAME = "lastName";
    private static final String BACKEND_TOKEN = "backendToken";
    private static final String LIBRARY_VERSION = "libraryVersion";
    private static final String LAST_LATITUDE= "lastLat";
    private static final String LAST_LONGITUDE= "lastLon";
    private static final String DEFAULT_PANTRY_NAME = "defaultPantryName";
    private static final String DEFAULT_PANTRY_ID = "defaultPantryId";
    private static final String PANTRY_LIST = "pantryList";
    private static final String SECRETS_ENABLED = "secretsEnabled";

    //mixpanel token for analytics
    public static final String MIXPANEL_TOKEN = "fcd4150579bb489de6f5a69e991f1da2";


    private static SharedPreferences sharedPreferences;



    public static AppSettings getApplication(Context context) {
        return (AppSettings) context.getApplicationContext();
    }

    @Override
    public void onCreate(){
        super.onCreate();
        AppSettings.context = getApplicationContext();
        RoboGuice.setUseAnnotationDatabases(false);
        try {
            File httpCacheDir = new File(context.getCacheDir(), "http");
            long httpCacheSize = 10 * 1024 * 1024; // 10 MiB
            HttpResponseCache.install(httpCacheDir, httpCacheSize);

        }catch (IOException e) {
            Log.i("CACHING THINGER", "HTTP response cache installation failed:" + e);
        }


        //Make the overflow menu show even if there is a hard menu button
        try {
            ViewConfiguration config = ViewConfiguration.get(this);
            Field menuKeyField = ViewConfiguration.class.getDeclaredField("sHasPermanentMenuKey");
            if(menuKeyField != null) {
                menuKeyField.setAccessible(true);
                menuKeyField.setBoolean(config, false);
            }
        } catch (Exception ex) {
            // Ignore
        }




    }

    private static SharedPreferences getSharedPreferences(){
        if(sharedPreferences==null){
            sharedPreferences = context.getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE);
        }
        return sharedPreferences;
    }

    public static String getProfileId(){
        return getSharedPreferences().getString(PROFILE_ID_KEY,null);
    }

    public static void setProfileId(String profileId){
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.putString(PROFILE_ID_KEY, profileId);
        editor.commit();
    }

    public static String getGoogleAccount(){
        return getSharedPreferences().getString(GOOGLE_ACCOUNT,null);
    }

    public static void setGoogleAccount(String email){
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.putString(GOOGLE_ACCOUNT, email);
        editor.commit();
    }

    public static String getFirstName(){
        return getSharedPreferences().getString(FIRST_NAME,null);
    }

    public static void setFirstName(String name){
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.putString(FIRST_NAME, name);
        editor.commit();
    }

    public static String getLastName(){
        return getSharedPreferences().getString(LAST_NAME,null);
    }

    public static void setLastName(String name){
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.putString(LAST_NAME, name);
        editor.commit();
    }

    public static String getAuthToken(){
        return getSharedPreferences().getString(AUTH_TOKEN,null);
    }

    public static void setAuthToken(String token){
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.putString(AUTH_TOKEN, token);
        editor.commit();
    }



    public static String getBackendToken(){
        return getSharedPreferences().getString(BACKEND_TOKEN,null);
    }

    public static void setBackendToken(String token){
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.putString(BACKEND_TOKEN, token);
        editor.commit();
    }

    public static String getLibraryVersion(){
        return getSharedPreferences().getString(LIBRARY_VERSION,null);
    }

    public static void setLibraryVersion(String version){
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.putString(LIBRARY_VERSION, version);
        editor.commit();
    }

    public static double getLastLatitude(){
        return (double) getSharedPreferences().getFloat(LAST_LATITUDE, (float) 34.0522); //default to Los Angeles
    }

    public static double getLastLongitude(){
        return (double) getSharedPreferences().getFloat(LAST_LONGITUDE, (float) -118.2428); //default to Los Angeles
    }

    public static void setLastLatitude(double latitude){
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.putFloat(LAST_LATITUDE, (float) latitude);
        editor.commit();
    }

    public static void setLastLongitude(double longitude){
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.putFloat(LAST_LONGITUDE, (float) longitude);
        editor.commit();
    }

    public static BookManager getBookManager(){
        return BookManager.getBookManager();
    }

    public static Context getContext(){
        return context;
    }

    public static String getDefaultPantryName(){
        return getSharedPreferences().getString(DEFAULT_PANTRY_NAME, null);
    }

    public static void setDefaultPantryName(String name){
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.putString(DEFAULT_PANTRY_NAME, name);
        editor.commit();
    }

    public static String getDefaultPantryId(){
        return getSharedPreferences().getString(DEFAULT_PANTRY_ID, null);
    }

    public static void setDefaultPantryId(String id){
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.putString(DEFAULT_PANTRY_ID, id);
        editor.commit();
    }

    public static void setPantryList(List<Pantry> pantries){
        Gson gson = new Gson();

        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.putString(PANTRY_LIST, gson.toJson(pantries));
        editor.commit();

    }

    public static List<Pantry> getPantryList(){
        Gson gson = new Gson();
        return gson.fromJson(getSharedPreferences().getString(PANTRY_LIST,""), new TypeToken<List<Pantry>>(){}.getType());
    }

    public static boolean getSecretsEnabled(){
        return getSharedPreferences().getBoolean(SECRETS_ENABLED, false);
    }

    public static void setSecretsEnabled(boolean enabled){
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.putBoolean(SECRETS_ENABLED, enabled);
        editor.commit();
    }


}
