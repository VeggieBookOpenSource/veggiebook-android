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


package com.veggiebook.android.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.veggiebook.R;
import com.veggiebook.android.activity.InterviewActivity;
import com.veggiebook.android.activity.SelectActivity;
import com.veggiebook.android.adapter.SelectBookTypeAdapter;
import com.veggiebook.android.util.Constant;
import com.veggiebook.model.BookManager;

import roboguice.fragment.RoboListFragment;
import roboguice.inject.InjectView;

/**
 * Created with IntelliJ IDEA.
 * User: danieldipasquo
 * Date: 2/6/13
 * Time: 2:23 PM
 */
public class SelectBookFragment extends RoboListFragment{
    private BookManager bookManager;
    private String bookType;

    @InjectView(R.id.textView)
    TextView textView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        return inflater.inflate(R.layout.select_listview,container,false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

       bookManager =   BookManager.getBookManager();
       bookType = ((SelectActivity) getActivity()).getBookType();

       if(bookType.equals("SECRETS_BOOK")){
           textView.setText(R.string.select_secretsbook);
       }
        else {
           textView.setText((R.string.select_veggiebook));
       }


       setListAdapter(new SelectBookTypeAdapter(getActivity(),
               bookManager.getAvailableBooks(getActivity(), bookType)));

    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        Intent intent = new Intent(getActivity(), InterviewActivity.class);
        intent.putExtra(Constant.AVAILABLE_BOOK_ID, bookManager.getAvailableBooks(getActivity(), bookType).get(position).getBookId());
        intent.putExtra(Constant.STARTED_FROM, Constant.NEW_VB);
        startActivity(intent);
    }
}
