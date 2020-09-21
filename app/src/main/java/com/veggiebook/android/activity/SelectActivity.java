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
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.veggiebook.R;
import com.veggiebook.android.util.Constant;
import com.veggiebook.model.BookManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import roboguice.activity.RoboFragmentActivity;
import roboguice.inject.InjectView;

public class SelectActivity extends RoboFragmentActivity implements View.OnClickListener {

    private BookManager bookManager;
    public static Logger log = LoggerFactory.getLogger(SelectActivity.class);
    private String bookType;

    @InjectView(R.id.button)
    Button whoSaysSo;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.Theme_vbt);
        setContentView(R.layout.vb_select_activity);

        Bundle extras = getIntent().getExtras();
        bookType = extras.getString(Constant.BOOK_TYPE_ID);
        log.debug("bookType: {}", bookType);

        ActionBar actionBar = getActionBar();
        actionBar.setLogo(R.drawable.logo_reversed);
        actionBar.setTitle("");
        actionBar.setDisplayHomeAsUpEnabled(true);
        if(bookType.equals("SECRETS_BOOK")){
            whoSaysSo.setVisibility(View.VISIBLE);
            whoSaysSo.setOnClickListener(this);
            getActionBar().setLogo(R.drawable.secretsbook_logo_reversed);
        }else{
            whoSaysSo.setVisibility(View.GONE);
            getActionBar().setLogo(R.drawable.logo_reversed);
        }

    }

    public String getBookType() {
        return bookType;
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


    @Override
    public void onClick(View v) {
        Intent intent = new Intent(this, WhoSaysSoActivity.class);
        startActivity(intent);
    }
}
