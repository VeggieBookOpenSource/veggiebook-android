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

import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.DialogFragment;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.squareup.picasso.Picasso;
import com.veggiebook.R;
import com.veggiebook.android.AppSettings;
import com.veggiebook.android.adapter.QuestionPagerAdapter;
import com.veggiebook.android.fragment.ConnectionErrorDialog;
import com.veggiebook.android.fragment.ProgressDialogFragment;
import com.veggiebook.android.fragment.QuestionUi;
import com.veggiebook.android.util.Constant;
import com.veggiebook.android.view.CascadeItemView;
import com.veggiebook.android.view.InterviewViewPager;
import com.veggiebook.model.AvailableBook;
import com.veggiebook.model.BookBuilder;
import com.veggiebook.model.BookManager;
import com.veggiebook.model.VbNotReadyException;
import com.veggiebook.model.orm.VeggieBook;
import com.veggiebook.service.rest.http.VbHttpException;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;

import roboguice.activity.RoboFragmentActivity;
import roboguice.inject.InjectView;


/**
 * Created with IntelliJ IDEA.
 * User: danieldipasquo
 * Date: 2/8/13
 * Time: 4:08 PM
 * <p/>
 * This activity allows the creation of a new VeggieBook.
 */
public class InterviewActivity extends RoboFragmentActivity implements ViewPager.OnPageChangeListener, ConnectionErrorDialog.ConnectionErrorDialogListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {
    public static final String BUILDER_JSON = "builderJson";
    static Logger log = LoggerFactory.getLogger(InterviewActivity.class);


    @InjectView(R.id.image_and_title)
    CascadeItemView imageAndTitleView;


    @InjectView(R.id.question_view_pager)
    InterviewViewPager viewPager;


    AvailableBook availableBook;
    BookBuilder bookBuilder;


    MenuItem next;
    MenuItem create;
    MenuItem keep;
    MenuItem drop;

    boolean progressBarVisible;

    boolean relaunched;
    boolean wasScrolled;
    GoogleApiClient mGoogleApiClient;
    LocationRequest mLocationRequest;


    //this variable is used to be able to handle paging from either the next button on swipes
    int position = 0;

    public int getPreviousPosition() {
        return previousPosition;
    }

    public void setPreviousPosition(int previousPosition) {
        this.previousPosition = previousPosition;
    }

    int previousPosition = -1;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setTheme(R.style.Theme_vbt);
        setContentView(R.layout.interview_activity);

        log.trace("Enter onCreate()");

        setProgressSpinnerVisible(true);

        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setLogo(R.drawable.logo_reversed);

        Bundle extras = getIntent().getExtras();
        String bookId = extras.getString(Constant.AVAILABLE_BOOK_ID);
        int startedFrom = extras.getInt(Constant.STARTED_FROM, 0);
        log.debug("bookId: {}", bookId);

        availableBook = getBookManager().getAvailableBook(bookId);

        //track the Start Creation event
        JSONObject props = new JSONObject();
        try {
            props.put("Book Type", availableBook.getBookType());
            props.put("Book Id", bookId);
            if (startedFrom == Constant.NEW_VB)
                props.put("Started From", "New");
            else
                props.put("Started From", "Edit");

        } catch (JSONException e) {
            log.error(e.getMessage(), e);
        }


        viewPager.setOnPageChangeListener(this);

        if (availableBook.getBookType().equals("RECIPE_BOOK")) {
            setImageAndTitle(availableBook.getLargeUrl(), availableBook.getTitle());
            getActionBar().setLogo(R.drawable.logo_reversed);
        } else {
            imageAndTitleView.setVisibility(View.GONE);

            getActionBar().setLogo(R.drawable.secretsbook_logo_reversed);
        }
        relaunched = false;
        if (savedInstanceState != null) {
            relaunched = true;


            bookBuilder = BookManager.bookBuilder;
            if (bookBuilder != null) {
                int currentItem = savedInstanceState.getInt("curPage", 0);


                QuestionPagerAdapter adapter = new QuestionPagerAdapter(getSupportFragmentManager(), bookBuilder);
                viewPager.setAdapter(adapter);

                viewPager.setCurrentItem(currentItem);
                setProgressSpinnerVisible(false);
            }

        }

        if (bookBuilder == null)
            new BuilderLoader().execute(savedInstanceState);


        createLocationRequest();
        connectToGoogleApiClient();


    }


    public void setImageAndTitleViewVisible(boolean visible) {
        int visibility = View.GONE;
        if (visible) visibility = View.VISIBLE;
        imageAndTitleView.setVisibility(visibility);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        next = menu.add(Menu.NONE, R.id.menu_next, 0, R.string.next);
        next.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        create = menu.add(Menu.NONE, R.id.menu_create, 0, R.string.create_veggiebook);
        create.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        keep = menu.add(Menu.NONE, R.id.menu_keep, 0, R.string.included);
        keep.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        drop = menu.add(Menu.NONE, R.id.menu_drop, 1, R.string.excluded);
        drop.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);


        configureMenu();

        return true;
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
//        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }

    protected void stopLocationUpdates() {
//        LocationServices.FusedLocationApi.removeLocationUpdates(
//                mGoogleApiClient, this);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        configureMenu();
        return true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onConnected(Bundle bundle) {
       AppSettings.setLastLatitude(0);
       AppSettings.setLastLongitude(0);
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
        if(location==null) return;
        AppSettings.setLastLatitude(location.getLatitude());
        AppSettings.setLastLongitude(location.getLongitude());
        log.error("LOC {}, {}", location.getLatitude(), location.getLongitude());
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

    private void configureMenu() {
        next.setVisible(!progressBarVisible && shouldDisplayNext());
        //create.setVisible(progressBarVisible ? false : shouldDisplayCreate());
        create.setVisible(false);
        if(availableBook.getBookType().equals("SECRETS_BOOK")){
            keep.setTitle(R.string.included_masc);
            drop.setTitle(R.string.excluded_masc);
        }
        keep.setVisible(!progressBarVisible && shouldDisplayKeepDrop());
        drop.setVisible(!progressBarVisible && shouldDisplayKeepDrop());

        PagerAdapter adapter = viewPager.getAdapter();


    }

    public AvailableBook getAvailableBook() {
        return availableBook;
    }

    public BookManager getBookManager() {
        return BookManager.getBookManager();
    }

    public BookBuilder getBookBuilder() {
        return bookBuilder;
    }


    public void setProgressSpinnerVisible(boolean visible) {
        progressBarVisible = visible;
        setProgressBarIndeterminateVisibility(visible);
        invalidateOptionsMenu();

    }

    public void setImageAndTitle(String url, String title) {
        imageAndTitleView.setVisibility(View.VISIBLE);
        imageAndTitleView.setTitle(title);
        Picasso.with(this).load(url).into(imageAndTitleView.getImgView());
    }

    @Override
    public void onPageScrolled(int i, float v, int i2) {


    }

    @Override
    public void onPageSelected(int i) {
        invalidateOptionsMenu();
        setImageTitleVisible();
        //if the previous position was set
        //then the page is changing so answer the question on the previous page
        if(position >=0){
            QuestionUi questionFragment = (QuestionUi) viewPager.getAdapter().instantiateItem(viewPager, position);
            questionFragment.answerQuestion();
        }

        setPreviousPosition(position);

        //set the position to the current page
        position=i;

    }

    @Override
    public void onPageScrollStateChanged(int i) {

    }

    public void pageForward() {
        viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
    }

    public InterviewActivity getSelf() {
        return this;
    }







    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        new BuilderLoader().execute((Bundle) null);
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
        this.finish();
    }

    public class BuilderLoader extends AsyncTask<Bundle, Void, Bundle> {
        boolean succeeded;

        @Override
        protected Bundle doInBackground(Bundle... bundles) {

            if (bookBuilder == null || !bookBuilder.getAvailableBook().equals(getAvailableBook())) {
                succeeded = loadInterview(3);
            }
            return bundles[0];

        }

        protected boolean loadInterview(int retries) {
            if (retries == 0)
                return false;

            try {
                bookBuilder = BookBuilder.instantiate(availableBook);
                return true;
            } catch (VbHttpException e) {
                return loadInterview(retries - 1);
            }
        }

        @Override
        protected void onPostExecute(Bundle bundle) {
            if (!succeeded) {
                ConnectionErrorDialog alert = new ConnectionErrorDialog();
                alert.setListener(getSelf());
                alert.show(getSupportFragmentManager(), "CONNECTION_ERROR");
            }

            int currentItem = 0;
            if (bundle != null) {
                currentItem = bundle.getInt("curPage", 0);
            }
            QuestionPagerAdapter adapter = new QuestionPagerAdapter(getSupportFragmentManager(), getBookBuilder());
            viewPager.setAdapter(adapter);

            viewPager.setCurrentItem(currentItem);
            setProgressSpinnerVisible(false);
        }


    }

    private void nextPage(){
        viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);

    }

    //Handle options menu clicks for previous and next
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menu_next:
                nextPage();
                break;
            case android.R.id.home:
                if (viewPager.getCurrentItem() == 0) {
                    //todo: warning
                    finish();
                } else {
                    viewPager.setCurrentItem(viewPager.getCurrentItem() - 1);
                }
                return true;
            case R.id.menu_create:
                //When this button is hit we are creating the book
                QuestionUi fragement = (QuestionUi) viewPager.getAdapter().instantiateItem(viewPager, viewPager.getCurrentItem());
                fragement.answerQuestion();
                new CreateVeggieBookTask().execute();
                break;
            default:
                return super.onOptionsItemSelected(item);

        }

        return false;
    }

    public void createVeggieBook(){
        new CreateVeggieBookTask().execute();
    }

    @Override
    public void onBackPressed() {
        getActionBar().show();
        if (viewPager.getCurrentItem() == 0) {
            //todo: warning
            super.onBackPressed();
        } else {
            viewPager.setCurrentItem(viewPager.getCurrentItem() - 1);
        }

    }

    public boolean shouldDisplayNext() {
        PagerAdapter adapter = viewPager.getAdapter();
        if (adapter == null || bookBuilder == null) {
            return false;
        }
        QuestionUi questionFragment = (QuestionUi) adapter.instantiateItem(viewPager, viewPager.getCurrentItem());
        return !shouldDisplayCreate() && questionFragment.isValidAnswer() && !questionFragment.displayKeepDrop();
    }


    public boolean shouldDisplayKeepDrop() {
        PagerAdapter adapter = viewPager.getAdapter();
        if (adapter == null || bookBuilder == null) {
            return false;
        }
        QuestionUi questionFragment = (QuestionUi) adapter.instantiateItem(viewPager, viewPager.getCurrentItem());
        return !shouldDisplayCreate() && questionFragment.isValidAnswer() && questionFragment.displayKeepDrop();
    }

    public boolean shouldDisplayCreate() {
        PagerAdapter adapter = viewPager.getAdapter();
        if (adapter == null || bookBuilder == null) {
            return false;
        }
        QuestionUi questionFragment = (QuestionUi) adapter.instantiateItem(viewPager, viewPager.getCurrentItem());
        return questionFragment.isValidAnswer() && (viewPager.getCurrentItem() == (adapter.getCount() - 1));

    }

    public void setImageTitleVisible() {
        PagerAdapter adapter = viewPager.getAdapter();
        if (adapter == null || bookBuilder == null) {
            return;
        }
        QuestionUi questionFragment = (QuestionUi) adapter.instantiateItem(viewPager, viewPager.getCurrentItem());
        setImageAndTitleViewVisible(questionFragment.displayImageTitle());
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        log.debug("onSaveInstanceState");
        outState.putInt("curPage", viewPager.getCurrentItem());
        BookManager.bookBuilder = this.bookBuilder;


    }

    public InterviewViewPager getViewPager() {
        return viewPager;
    }

    private class CreateVeggieBookTask extends AsyncTask<Void, Void, VeggieBook> {
        ProgressDialogFragment progress = ProgressDialogFragment.newInstance(getString(availableBook.getBookType().equals("SECRETS_BOOK")?R.string.creating_secretsbook:R.string.creating_veggiebook), false);
        boolean succeeded = false;
        VeggieBook mVeggieBook = null;


        @Override
        protected VeggieBook doInBackground(Void... params) {
            log.debug("Creating VeggieBook");

            succeeded = createVeggieBook(3);
            return null;
        }

        private boolean createVeggieBook(int retries) {
            if (retries == 0)
                return false;

            try {
                mVeggieBook = getBookManager().createVeggieBook(getBookBuilder());
                //track veggieBook creation
                JSONObject props = new JSONObject();
                try {
                    props.put("Book Type", getBookBuilder().getAvailableBook().getBookType());
                    props.put("Book Id", getBookBuilder().getAvailableBook().getBookId());
                    props.put("RecipeBook Id", mVeggieBook.getServerUid());
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                return true;
            } catch (VbHttpException e) {
                log.error(e.getMessage(), e);
                return createVeggieBook(retries - 1);
            } catch (VbNotReadyException e) {
                log.error(e.getMessage(), e);
                return false;
            } catch (SQLException e) {
                log.error(e.getMessage(), e);
                return false;
            }

        }

        @Override
        protected void onPreExecute() {
            log.debug("Showing Progress Dialog");
            progress.show(getSupportFragmentManager(), "CREATE_PROGRESS");

        }

        @Override
        protected void onPostExecute(VeggieBook veggieBook) {
            progress.dismiss();

            if (!succeeded) {
                ConnectionErrorDialog alert = new ConnectionErrorDialog();
                alert.setListener(getSelf());
                alert.show(getSupportFragmentManager(), "CONNECTION_ERROR");
                return;
            }

            Intent intent = new Intent(getApplicationContext(), VeggieBookActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            if(mVeggieBook != null){
                intent.putExtra(VeggieBookActivity.NEW_VB_ID, mVeggieBook.getId());
            }

            startActivity(intent);
            finish();
        }
    }

}






