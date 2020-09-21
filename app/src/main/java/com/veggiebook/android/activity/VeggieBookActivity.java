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

import android.accounts.AccountManager;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.GooglePlayServicesAvailabilityException;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.common.AccountPicker;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.veggiebook.BuildConfig;
import com.veggiebook.R;
import com.veggiebook.android.AppSettings;
import com.veggiebook.android.fragment.MyBooksFragment;
import com.veggiebook.android.fragment.ProgressDialogFragment;
import com.veggiebook.android.util.Constant;
import com.veggiebook.model.BookManager;
import com.veggiebook.service.dto.RegisterResponse;
import com.veggiebook.service.rest.http.VbHttpException;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import roboguice.activity.RoboFragmentActivity;
import roboguice.inject.InjectFragment;
import roboguice.inject.InjectView;


public class VeggieBookActivity extends RoboFragmentActivity implements View.OnClickListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {
    public static final String APP_ID = "9917173e31f5018e7bbc22828e24f35b";
    public static final String PROGRESS_TAG = "progress";
    public static final int ACCOUNT_PICKER_CODE = 666;
    static final int REQUEST_CODE_RECOVER_FROM_AUTH_ERROR = 1001;
    static final int REQUEST_CODE_RECOVER_FROM_PLAY_SERVICES_ERROR = 1002;
    protected static final String USER_PROFILE_SCOPE = "oauth2:https://www.googleapis.com/auth/userinfo.profile";
    protected static final String BACKEND_TOKEN_SCOPE = "audience:server:client_id:1080211826488.apps.googleusercontent.com";
    public static final String NEW_VB_ID = "new_vb_id";
    GetTokenAndUserInfoTask currentTask;
    private static Logger log  = LoggerFactory.getLogger(VeggieBookActivity.class);
    private ProgressDialogFragment progressDialog;
    GoogleApiClient mGoogleApiClient;
    LocationRequest mLocationRequest;


    @InjectFragment(R.id.list)
    MyBooksFragment fragment;

    @InjectView(R.id.button)
    Button newVeggieBookButton;

    @InjectView(R.id.button1)
    Button newSecretsBookButton;

    MenuItem secretsMenuItem;
    MenuItem mffMenuItem;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTheme(R.style.Theme_vbt);
        setContentView(R.layout.vb_fragment_activity);

        getActionBar().setLogo(R.drawable.logo_reversed);
        getActionBar().setTitle("");

        progressDialog = ProgressDialogFragment.newInstance(getString(R.string.logging_in), false);
        progressDialog.show(getSupportFragmentManager(), "LOGGING_IN_DIALOG");
        launchAppIfReady();

        newVeggieBookButton.setOnClickListener(this);
        newSecretsBookButton.setOnClickListener(this);
        newSecretsBookButton.setVisibility(View.VISIBLE);

        createLocationRequest();
        connectToGoogleApiClient();


    }

    protected void connectToGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    protected void startLocationUpdates() {
        // LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }

    protected void stopLocationUpdates() {
        //LocationServices.FusedLocationApi.removeLocationUpdates(
        //        mGoogleApiClient, this);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mGoogleApiClient.isConnected()){
            startLocationUpdates();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        mGoogleApiClient.disconnect();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_main, menu);
        mffMenuItem = menu.findItem(R.id.non_descrimination);
        mffMenuItem.setVisible(BuildConfig.FLAVOR=="mff"?true:false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.change_language:
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.setClassName("com.android.settings", "com.android.settings.LanguageSettings");
                startActivity(intent);
                return true;
            case R.id.non_descrimination:
                String url = getString(R.string.mff_non_discrimination_url);
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            case R.id.privacy_policy:
                String uri = getString(R.string.privacy_policy_url);
                Intent it = new Intent(Intent.ACTION_VIEW);
                it.setData(Uri.parse(uri));
                startActivity(it);
        }
        return false;
    }


    public void onActivityResult(final int requestCode, final int resultCode,
                                 final Intent data) {
        if (requestCode == ACCOUNT_PICKER_CODE && resultCode == Activity.RESULT_OK) {
            AppSettings.setGoogleAccount(data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME));
        }
        launchAppIfReady();
    }

    public void launchAppIfReady() {
        log.trace("Enter launchAppIffReady");

        log.trace("Is Google Play service available and up to date?");
        int statusCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);

        if(statusCode != ConnectionResult.SUCCESS){
            progressDialog.dismiss();
            GooglePlayServicesUtil.getErrorDialog(statusCode, this, 1010).show();
            return;
        }


        //First step get the email account
        log.trace("Google App Email: {}", AppSettings.getGoogleAccount());
        if (AppSettings.getGoogleAccount() == null) {
            Intent intent = AccountPicker.newChooseAccountIntent(null, null, new String[]{"com.google"},
                    false, null, null, null, null);
            startActivityForResult(intent, ACCOUNT_PICKER_CODE);
            return;
        }

        //next get an auth token for that email account
        //Also get the first and last name if they are available.
        log.trace("AuthToken: {}", AppSettings.getAuthToken());
        if (AppSettings.getAuthToken() == null) {
            if(currentTask != null)
                return;

            currentTask = new GetTokenAndUserInfoTask(AppSettings.getGoogleAccount(), USER_PROFILE_SCOPE, REQUEST_CODE_RECOVER_FROM_AUTH_ERROR, false);
            currentTask.execute();
            return;
        }
        else{
            Toast toast = Toast.makeText(this, String.format(getString(R.string.welcome), AppSettings.getFirstName()) , Toast.LENGTH_SHORT);
            toast.show();
        }

        log.trace("Backend Token: {}", AppSettings.getBackendToken());
        if(AppSettings.getBackendToken() == null){
            if(currentTask != null)
                return;
            currentTask = new GetTokenAndUserInfoTask(AppSettings.getGoogleAccount(), BACKEND_TOKEN_SCOPE, REQUEST_CODE_RECOVER_FROM_AUTH_ERROR, true);
            currentTask.execute();
            return;
        }

        if(AppSettings.getProfileId() == null){
            new RegisterTask().execute();
            return;
        }

        //create super properties
        JSONObject superProperties = new JSONObject();

        try {
            superProperties.put("Library Version", AppSettings.getLibraryVersion());
            superProperties.put("Lat", AppSettings.getLastLatitude());
            superProperties.put("Lon", AppSettings.getLastLongitude());

        } catch (JSONException e) {
            //on json error, ignore, still track without super properties
            log.error(e.getMessage(),e);
        }


        fragment.resetAdapter();
        progressDialog.dismiss();

    }

    @Override
    protected void onPause() {
        super.onPause();
//        if(mGoogleApiClient.isConnected()){
//            //stopLocationUpdates();
//        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /**
     * This method is a hook for background threads and async tasks that need to launch a dialog.
     * It does this by launching a runnable under the UI thread.
     */
    public void showErrorDialog(final int code) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Dialog d = GooglePlayServicesUtil.getErrorDialog(
                        code,
                        VeggieBookActivity.this,
                        REQUEST_CODE_RECOVER_FROM_PLAY_SERVICES_ERROR);
                d.show();
            }
        });
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.button){
            Intent intent = new Intent(this, SelectActivity.class);
            intent.putExtra(Constant.BOOK_TYPE_ID, "RECIPE_BOOK");
            startActivity(intent);
        }

        if(v.getId() == R.id.button1){
            Intent intent = new Intent(this, SelectActivity.class);
            intent.putExtra(Constant.BOOK_TYPE_ID, "SECRETS_BOOK");
            startActivity(intent);
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        Location lastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        AppSettings.setLastLatitude(lastLocation!=null?lastLocation.getLatitude():0);
        AppSettings.setLastLongitude(lastLocation!=null?lastLocation.getLongitude():0);
        //startLocationUpdates();
    }

    @Override
    public void onConnectionSuspended(int i) {
        log.debug("Google API Connection Suspended");

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        log.debug("Google API Connection Failed");

    }

    @Override
    public void onLocationChanged(Location location) {
        AppSettings.setLastLatitude(location.getLatitude());
        AppSettings.setLastLongitude(location.getLongitude());
        log.error("LOC {}, {}", location.getLatitude(), location.getLongitude());
    }

    public class GetTokenAndUserInfoTask extends AsyncTask<Void, Void, Void> {
        private static final String TAG = "TokenInfoTask";
        private static final String NAME_KEY = "given_name";
        private static final String LAST_NAME_KEY = "family_name";

        protected String mScope;
        protected String mEmail;
        protected int mRequestCode;
        protected boolean backend;

        public GetTokenAndUserInfoTask(String email, String scope, int requestCode, boolean backend) {
            this.mScope = scope;
            this.mEmail = email;
            this.mRequestCode = requestCode;
            this.backend = backend;
        }

        @Override
        protected Void doInBackground(Void... params) {

            try {
                fetchNameFromProfileServer();
            } catch (IOException ex) {
                onError("Following Error occured, please try again. " + ex.getMessage(), ex);
            } catch (JSONException e) {
                onError("Bad response: " + e.getMessage(), e);
            }
            return null;
        }

        protected void onError(String msg, Exception e) {
            if (e != null) {
                Log.e(TAG, "Exception: ", e);
            }
            //        mActivity.show(msg);  // will be run in UI thread
        }

        /**
         * Get a authentication token if one is not available. If the error is not recoverable then
         * it displays the error message on parent activity.
         */
        protected String fetchToken() throws IOException {
            try {
                return GoogleAuthUtil.getToken(getApplicationContext(), mEmail, mScope);
            } catch (GooglePlayServicesAvailabilityException playEx) {
                // GooglePlayServices.apk is either old, disabled, or not present.
                showErrorDialog(playEx.getConnectionStatusCode());
            } catch (UserRecoverableAuthException userRecoverableException) {
                // Unable to authenticate, but the user can fix this.
                // Forward the user to the appropriate activity.
                startActivityForResult(userRecoverableException.getIntent(), mRequestCode);
            } catch (GoogleAuthException fatalException) {
                onError("Unrecoverable error " + fatalException.getMessage(), fatalException);
            }
            return null;
        }

        /**
         * Contacts the user info server to get the profile of the user and extracts the first name
         * of the user from the profile. In order to authenticate with the user info server the method
         * first fetches an access token from Google Play services.
         *
         * @throws IOException    if communication with user info server failed.
         * @throws JSONException if the response from the server could not be parsed.
         */
        private void fetchNameFromProfileServer() throws IOException, JSONException {
            String token = fetchToken();
            if (token == null) {
                // error has already been handled in fetchToken()
                return;
            }
            if(backend){
                AppSettings.setBackendToken(token);
                return;
            }
            AppSettings.setAuthToken(token);
            URL url = new URL("https://www.googleapis.com/oauth2/v1/userinfo?access_token=" + token);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            int sc = con.getResponseCode();
            if (sc == 200) {
                InputStream is = con.getInputStream();
                JSONObject profile = new JSONObject(readResponse(is));
                AppSettings.setFirstName(profile.getString(NAME_KEY));
                AppSettings.setLastName(profile.getString(LAST_NAME_KEY));
                is.close();
                return;
            } else if (sc == 401) {
                GoogleAuthUtil.invalidateToken(getApplicationContext(), token);
                onError("Server auth error, please try again.", null);
                Log.i(TAG, "Server auth error: " + readResponse(con.getErrorStream()));
                return;
            } else {
                onError("Server returned the following error code: " + sc, null);
                return;
            }
        }

        /**
         * Reads the response from the input stream and returns it as a string.
         */
        private String readResponse(InputStream is) throws IOException {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            byte[] data = new byte[2048];
            int len = 0;
            while ((len = is.read(data, 0, data.length)) >= 0) {
                bos.write(data, 0, len);
            }
            return new String(bos.toByteArray(), "UTF-8");
        }



        @Override
        protected void onPostExecute(Void aVoid) {
            currentTask=null;
            if(mRequestCode == REQUEST_CODE_RECOVER_FROM_AUTH_ERROR
                    && AppSettings.getAuthToken() != null)
                launchAppIfReady();

            if(mRequestCode == REQUEST_CODE_RECOVER_FROM_PLAY_SERVICES_ERROR
                    && AppSettings.getBackendToken() != null)
                launchAppIfReady();

        }
    }

    public class RegisterTask extends AsyncTask<Void, Void, Void>{

        @Override
        protected Void doInBackground(Void... params) {
            try {
                RegisterResponse response = BookManager.getBookManager().getRestService().register(AppSettings.getBackendToken(), AppSettings.getFirstName(), AppSettings.getLastName());
                AppSettings.setProfileId(response.getProfileId());
            } catch (VbHttpException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            launchAppIfReady();
        }
    }


}
