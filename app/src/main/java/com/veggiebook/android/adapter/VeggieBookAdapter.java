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

package com.veggiebook.android.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.veggiebook.R;
import com.veggiebook.android.view.CascadeItemView;
import com.veggiebook.model.BookManager;
import com.veggiebook.model.RecipeBook;

import java.sql.SQLException;

public class VeggieBookAdapter extends ArrayAdapter<RecipeBook> {

    public VeggieBookAdapter(Context context, BookManager bookManager) throws SQLException {
        super(context, R.id.selectItemTextView, bookManager.getMyBooks());
    }


    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        CascadeItemView imageView;
        if (convertView == null) {  // if it's not recycled, initialize some attributes
            imageView = new CascadeItemView(getContext());
        } else {

            imageView = (CascadeItemView) convertView;
        }

        imageView.setLayoutParams(new GridView.LayoutParams(GridView.LayoutParams.MATCH_PARENT, parent.getWidth()/2));
        imageView.setPadding(0, 6, 0, 6);
        imageView.getImgView().setScaleType(ImageView.ScaleType.CENTER_CROP);


        RecipeBook vb = getItem(position);
        String imgUrl = vb.getCoverImageUrl();

        String title = vb.getTitle();

        imageView.setTitle(title);
        Picasso.with(getContext()).load(vb.getVeggieImgUrl()).resize(parent.getWidth(), parent.getWidth()/2).centerCrop().into(imageView.getImgView());
        if (vb.getVeggieImgUrl().equals(imgUrl)) {
            imageView.getSmallImageview().setVisibility(View.GONE);
        } else {
            Picasso.with(getContext()).load(imgUrl).resize(300, 300).centerCrop().into(imageView.getSmallImageview());
            imageView.getSmallImageview().setVisibility(View.VISIBLE);
        }


        return imageView;
    }



}
