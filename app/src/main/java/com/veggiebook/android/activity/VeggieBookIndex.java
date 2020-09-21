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

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Point;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.Display;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ShareActionProvider;
import android.widget.TextView;
import android.widget.Toast;

import com.veggiebook.BuildConfig;
import com.veggiebook.R;
import com.veggiebook.android.AppSettings;
import com.veggiebook.android.adapter.VBIndexAdapter;
import com.veggiebook.android.fragment.PrintDialogFragment;
import com.veggiebook.android.task.RecordEvent;
import com.veggiebook.android.util.Constant;
import com.veggiebook.model.BookManager;
import com.veggiebook.model.RecipeBook;
import com.veggiebook.service.dto.PrintResponse;
import com.veggiebook.service.dto.RecordEventRequest;
import com.veggiebook.service.rest.http.VbHttpException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.UUID;

import roboguice.activity.RoboFragmentActivity;
import roboguice.inject.InjectView;
import static com.veggiebook.BuildConfig.WEB_SERVICES_HOST;

/**
 * Created with IntelliJ IDEA.
 * User: danieldipasquo
 * Date: 3/19/13
 * Time: 4:56 PM
 */
public class VeggieBookIndex extends RoboFragmentActivity implements AdapterView.OnItemClickListener, ShareActionProvider.OnShareTargetSelectedListener {
    public static Logger log = LoggerFactory.getLogger(VeggieBookIndex.class);
    private static final String SELECTED_POSITION = "selectedPosition";

    @InjectView(R.id.textView)
    TextView textView;

    GridView gridView;
    int selectedPosition = -1;

    @InjectView(R.id.webView)
    WebView webView;

    @InjectView(R.id.progressBar)
    ProgressBar progressBar;

    @InjectView(R.id.webLayout)
    LinearLayout webLayout;

    private ShareActionProvider mShareActionProvider;

    RecipeBook mRecipeBook;

    MenuItem shareMenuItem;
    MenuItem printAtPantryItem;

    private String itemUUID;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.Theme_vbt);
        setContentView(R.layout.vb_index);

        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setLogo(R.drawable.logo_reversed);
        getActionBar().setTitle("");

        Bundle extras = getIntent().getExtras();
        String bookId = extras.getString(Constant.BOOK_INFO_ID);
        log.debug("bookId: {}", bookId);

        mRecipeBook = null;
        try {
            mRecipeBook = new RecipeBook(bookId);

        } catch (SQLException e) {
            e.printStackTrace();
            //todo: bail out of app here
        }

        if (savedInstanceState != null) {
            selectedPosition = savedInstanceState.getInt(SELECTED_POSITION, -1);
        }

        gridView = (GridView) findViewById(R.id.gridView);
        //textView.setText(String.format(getString(R.string.created_by_format), AppSettings.getFirstName()));
        textView.setVisibility(View.GONE);

        getActionBar().setTitle(mRecipeBook.getTitle());

        webView.setPadding(0, 0, 0, 0);
        webView.setInitialScale(getScale(isLandscape() ? 70 : 100));
        webView.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int progress) {

                if (progress < 100) {
                    progressBar.setVisibility(ProgressBar.VISIBLE);
                    webView.setVisibility(View.INVISIBLE);
                }
                progressBar.setProgress(progress);
                if (progress == 100) {
                    progressBar.setVisibility(ProgressBar.GONE);
                    webView.setVisibility(View.VISIBLE);
                }
            }

        });
        webView.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                // do your handling codes here, which url is the requested url
                // probably you need to open that url rather than redirect:
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(intent);
                return true;
            }
        });

        VBIndexAdapter adapter = VBIndexAdapter.newInstance(this, mRecipeBook);

        gridView.setAdapter(adapter);
        gridView.setOnItemClickListener(this);


        if(mRecipeBook.getTipsUrl()== null || mRecipeBook.getTipsUrl().isEmpty()){
            getActionBar().setLogo(R.drawable.secretsbook_logo_reversed);

        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (selectedPosition != -1) {

            if (!isLandscape()) {
                gridView.setVisibility(View.GONE);
                //   webView.setInitialScale(getScale(100));
            }

            gridView.setSelection(selectedPosition);
            RecipeBook.Recipe recipe = (RecipeBook.Recipe) gridView.getAdapter().getItem(selectedPosition);
            webView.loadUrl(recipe.getUrl());

            webLayout.setVisibility(View.VISIBLE);
            itemUUID = UUID.randomUUID().toString();
            new RecordEvent().execute(
                    RecordEventRequest.createViewEvent(
                            AppSettings.getProfileId(),
                            mRecipeBook.getBookInfoId(),
                            recipe.getId(),
                            itemUUID,
                            mRecipeBook.getServerUid()));

        } else {
            //if nothing is selected, hide the web view
            webLayout.setVisibility(View.GONE);
            gridView.setVisibility(View.VISIBLE);
            if (isLandscape()) {
                gridView.setNumColumns(4);
            } else {
                gridView.setNumColumns(2);
            }
            itemUUID = null;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(itemUUID != null && selectedPosition != -1){
            RecipeBook.Recipe recipe = (RecipeBook.Recipe) gridView.getAdapter().getItem(selectedPosition);
            new RecordEvent().execute(
                    RecordEventRequest.createViewCompleteEvent(
                            AppSettings.getProfileId(),
                            mRecipeBook.getBookInfoId(),
                            recipe.getId(),
                            itemUUID,
                            mRecipeBook.getServerUid()
                    )
            );
            itemUUID = null;
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.veb_index_menu, menu);
        shareMenuItem = menu.findItem(R.id.menu_share);
        mShareActionProvider = (ShareActionProvider) shareMenuItem.getActionProvider();
        mShareActionProvider.setShareIntent(getShareIntent());
        mShareActionProvider.setOnShareTargetSelectedListener(this);
        //set the shareMenuItem to be visible if we are on a recipe.
        shareMenuItem.setVisible(webLayout.getVisibility()==View.VISIBLE && selectedPosition <= mRecipeBook.getRecipes().size());

        printAtPantryItem = menu.findItem(R.id.menu_print);
        printAtPantryItem.setVisible(BuildConfig.PRINT_AT_PANTRY);

        return true;
    }

    private Intent getShareIntent(){
        Intent intent=new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);

        // Add data to the intent, the receiving app will decide what to do with it.

        //If a recipe is displayed share that recipe
        if(webLayout.getVisibility()==View.VISIBLE && selectedPosition <= mRecipeBook.getRecipes().size()){
            if(shareMenuItem != null){
                shareMenuItem.setVisible(true);
            }

            //if its a secrets book
            if(mRecipeBook.getTipsUrl()== null || mRecipeBook.getTipsUrl().isEmpty()){
                if(selectedPosition >= 0){
                    RecipeBook.Recipe recipe = mRecipeBook.getRecipes().get(selectedPosition);
                    intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.subject_share_secret, recipe.getName()));
                    intent.putExtra(Intent.EXTRA_TEXT, getString(R.string.format_share_secret, mRecipeBook.getTitle(), recipe.getShareUrl()));
                }
                return intent;
            }

            //If selectedPosition is 0 then this is the tips we should share
            if(selectedPosition<=0){
                intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.subject_share_tips, mRecipeBook.getTitle()));
                intent.putExtra(Intent.EXTRA_TEXT, getString(R.string.format_share_tips, mRecipeBook.getTitle(), mRecipeBook.getTipsUrl()));

            } else{
                RecipeBook.Recipe recipe = mRecipeBook.getRecipes().get(selectedPosition-1);
                intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.subject_share_recipe, recipe.getName()));
                intent.putExtra(Intent.EXTRA_TEXT, getString(R.string.format_share_recipe, mRecipeBook.getTitle(), recipe.getName(), recipe.getShareUrl()));
            }
        }



        return intent ;
    }




    private int getScale(double percent) {
        Display display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();

        Point size = new Point();
        display.getSize(size);
        int width = size.x;

        Double val = new Double(width) / new Double(320);
        val = val * percent;
        return val.intValue();
    }

    private boolean isLandscape() {
        return getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
    }

    private void dismissRecipe() {
        if (isLandscape()) {
            gridView.setNumColumns(4);
        } else {
            gridView.setNumColumns(2);
        }

        webLayout.setVisibility(View.GONE);
        gridView.setVisibility(View.VISIBLE);
        int wasSelected = selectedPosition;
        selectedPosition = -1;
        mShareActionProvider.setShareIntent(getShareIntent());
        invalidateOptionsMenu();
        if(itemUUID != null && wasSelected != -1){
            RecipeBook.Recipe recipe = (RecipeBook.Recipe) gridView.getAdapter().getItem(wasSelected);
            new RecordEvent().execute(
                    RecordEventRequest.createViewCompleteEvent(
                            AppSettings.getProfileId(),
                            mRecipeBook.getBookInfoId(),
                            recipe.getId(),
                            itemUUID,
                            mRecipeBook.getServerUid()
                            )
            );
            itemUUID = null;
        }
    }

    private void selectRecipe(int position) {
        RecipeBook.Recipe recipe = (RecipeBook.Recipe) gridView.getAdapter().getItem(position);
        int wasSelected = selectedPosition;
        selectedPosition = position;
        if (isLandscape()) {
            gridView.setNumColumns(1);
            gridView.setSelection(selectedPosition);
        } else {
            gridView.setVisibility(View.GONE);
        }

        webLayout.setVisibility(View.VISIBLE);
        webView.loadUrl(recipe.getUrl());
        mShareActionProvider.setShareIntent(getShareIntent());
        invalidateOptionsMenu();
        if(itemUUID != null && wasSelected != -1){
            RecipeBook.Recipe old = (RecipeBook.Recipe) gridView.getAdapter().getItem(wasSelected);
            new RecordEvent().execute(
                    RecordEventRequest.createViewCompleteEvent(
                            AppSettings.getProfileId(),
                            mRecipeBook.getBookInfoId(),
                            old.getId(),
                            itemUUID,
                            mRecipeBook.getServerUid()
                    )
            );
            itemUUID = null;
        }

        itemUUID = UUID.randomUUID().toString();
        new RecordEvent().execute(
                RecordEventRequest.createViewEvent(
                        AppSettings.getProfileId(),
                        mRecipeBook.getBookInfoId(),
                        recipe.getId(),
                        itemUUID,
                        mRecipeBook.getServerUid()));

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (selectedPosition != -1) {
                    dismissRecipe();
                } else {
                    // app icon in action bar clicked; go home
                    finish();
                }
                return true;
            case R.id.menu_edit:
                Intent intent = new Intent(this, InterviewActivity.class);

                intent.putExtra(Constant.AVAILABLE_BOOK_ID, mRecipeBook.getAvailableBookId());
                startActivity(intent);
                return true;
            case R.id.menu_print:
//               new PrintVeggieBook().execute();
                String type = "RECIPE_BOOK";
                if(mRecipeBook.getTipsUrl()== null || mRecipeBook.getTipsUrl().isEmpty()){
                    type = "SECRETS_BOOK";
                }

                PrintDialogFragment.newInstance(mRecipeBook.getServerUid(), type).show(getSupportFragmentManager(),"printDialog");
                return true;
            case R.id.download_pdf:
                String urlString =  String.format("https://%s/qhmobile/veggieBookPdf/%s/%s/", WEB_SERVICES_HOST, BookManager.getBookManager().getLanguage(), mRecipeBook.getServerUid());
                if(mRecipeBook.getTipsUrl()== null || mRecipeBook.getTipsUrl().isEmpty()){
                    urlString = String.format("https://%s/qhmobile/secretsBookPdf/%s/%s/", WEB_SERVICES_HOST, BookManager.getBookManager().getLanguage(), mRecipeBook.getServerUid());
                }
                Uri uri = Uri.parse(urlString);

                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                        .mkdirs();


                DownloadManager.Request req=new DownloadManager.Request(uri);

                req.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI
                        | DownloadManager.Request.NETWORK_MOBILE)
                        .setAllowedOverRoaming(false)
                        .setTitle(getString(R.string.app_name))
                        .setDescription(mRecipeBook.getTitle())
                        .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,
                                mRecipeBook.getTitle() + ".pdf")
                        .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

                DownloadManager downloadManager =   (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
                downloadManager.enqueue(req);


                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }



    @Override
    public void onBackPressed() {
        if (selectedPosition != -1) {
            dismissRecipe();
        } else {
            // app icon in action bar clicked; go home
            finish();
        }
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        selectRecipe(position);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(SELECTED_POSITION, selectedPosition);

    }

    @Override
    public boolean onShareTargetSelected(ShareActionProvider source, Intent intent) {
        if(selectedPosition == -1){
            return false;
        }
        RecipeBook.Recipe recipe = (RecipeBook.Recipe) gridView.getAdapter().getItem(selectedPosition);
        new RecordEvent().execute(RecordEventRequest.createShareEvent(
                AppSettings.getProfileId(),
                mRecipeBook.getBookInfoId(),
                recipe.getId(),
                intent.toString(),
                mRecipeBook.getServerUid()
        ));

        return false;
    }

    public class PrintVeggieBook extends AsyncTask<Void, Void, Void> {
        boolean success = false;

        @Override
        protected Void doInBackground(Void... params) {
            BookManager bookManager = BookManager.getBookManager();

            try {
                String type = "RECIPE_BOOK";
                if(mRecipeBook.getTipsUrl()== null || mRecipeBook.getTipsUrl().isEmpty()){
                    type = "SECRETS_BOOK";
                }
                PrintResponse printResponse = bookManager.getRestService().printVeggieBook(mRecipeBook.getVeggieBookId(), bookManager.getLanguage(),"0",type );
                success = true;
            } catch (VbHttpException e) {
                e.printStackTrace();
                success = false;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            Toast toast = Toast.makeText(getParent(), success ? getString(R.string.veggiebook_printing) : getString(R.string.print_not_available), Toast.LENGTH_SHORT);


        }
    }



}