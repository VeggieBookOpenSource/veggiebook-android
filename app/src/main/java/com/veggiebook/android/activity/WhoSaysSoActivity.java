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

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.MenuItem;
import android.view.WindowManager;
import android.webkit.WebView;

import com.veggiebook.R;
import com.veggiebook.model.BookManager;

/**
 * Created with IntelliJ IDEA.
 * User: danieldipasquo
 * Date: 3/10/14
 * Time: 11:23 AM
 */

public class WhoSaysSoActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.who_says_so);

        ActionBar actionBar = getActionBar();
        actionBar.setLogo(R.drawable.secretsbook_logo_reversed);
        actionBar.setTitle("");
        actionBar.setDisplayHomeAsUpEnabled(true);

        WebView webView = (WebView) findViewById(R.id.webView);
        webView.setPadding(0, 0, 0, 0);
        webView.setInitialScale(getScale());
        if(BookManager.getBookManager().getLanguage().equals("es"))
            webView.loadUrl("https://www.veggiebook.mobi/static/whosaysso_es.html?v=3");
        else
            webView.loadUrl("https://www.veggiebook.mobi/static/whosaysso_en.html?v=3");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; go home
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private int getScale() {
        Display display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();

        Point size = new Point();
        display.getSize(size);
        int width = size.x;

        Double val = new Double(width) / new Double(310);
        val = val * 100d;
        return val.intValue();
    }




}