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

package com.veggiebook.android.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import com.veggiebook.BuildConfig;
import com.veggiebook.R;
import com.veggiebook.android.AppSettings;
import com.veggiebook.model.AvailableBook;
import com.veggiebook.model.BookManager;

import java.util.List;


public class SplashScreenActivity extends Activity {


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);


        AppSettings.setBackendToken(null);
        new StartupTasks().execute();

    }

    private SplashScreenActivity getSelf(){
        return this;
    }

    public class StartupTasks extends AsyncTask<Void,Void,List<AvailableBook>> {


        @Override
        protected List<AvailableBook> doInBackground(Void... params) {
            BookManager bookManager = BookManager.getBookManager();

            bookManager.synchronizeWithServer();



            bookManager.getAvailableBooks(getApplicationContext(), "RECIPE_BOOK");
            bookManager.getAvailableBooks(getApplicationContext(), "SECRETS_BOOK");
            return null;

        }


        @Override
        protected void onPostExecute(List<AvailableBook> books) {
            Intent intent = new Intent(getSelf(),VeggieBookActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            //Set the known account to null at startup to force the user to pick and account.
            if(BuildConfig.ALWAYS_CHOOSE_ACCOUNT) {
                AppSettings.setGoogleAccount(null);
                AppSettings.setProfileId(null);
                AppSettings.setAuthToken(null);
                AppSettings.setBackendToken(null);
                AppSettings.setFirstName(null);
                AppSettings.setLastName(null);
            }
            startActivity(intent);

        }
    }
}