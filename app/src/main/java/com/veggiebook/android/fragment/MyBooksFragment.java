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

import com.veggiebook.R;
import com.veggiebook.android.activity.VeggieBookIndex;
import com.veggiebook.android.adapter.VeggieBookAdapter;
import com.veggiebook.android.util.Constant;
import com.veggiebook.model.BookManager;
import com.veggiebook.model.RecipeBook;

import java.sql.SQLException;

import roboguice.fragment.RoboListFragment;

/**
 * Created with IntelliJ IDEA.
 * User: danieldipasquo
 * Date: 2/6/13
 * Time: 2:13 PM
 *
 */

public class MyBooksFragment extends RoboListFragment{


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        return inflater.inflate(R.layout.my_books_listview,container,false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        try {
            setListAdapter(new VeggieBookAdapter(getActivity(), BookManager.getBookManager()));
        } catch (SQLException e) {
            e.printStackTrace();
        }


    }

    public void resetAdapter(){
        try {
            setListAdapter(new VeggieBookAdapter(getActivity(), BookManager.getBookManager()));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        RecipeBook veggieBook = (RecipeBook) getListAdapter().getItem(position);
        Intent intent = new Intent(getActivity(), VeggieBookIndex.class);
        intent.putExtra(Constant.BOOK_INFO_ID, veggieBook.getBookInfoId());
        startActivity(intent);



    }
}
