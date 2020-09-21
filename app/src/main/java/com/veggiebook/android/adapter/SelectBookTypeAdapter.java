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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.veggiebook.R;
import com.veggiebook.model.AvailableBook;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: danieldipasquo
 * Date: 2/6/13
 * Time: 3:32 PM
 *
 */
public class SelectBookTypeAdapter extends ArrayAdapter<AvailableBook> {
    public static Logger log = LoggerFactory.getLogger(SelectBookTypeAdapter.class);

    public SelectBookTypeAdapter(Context context, List<AvailableBook> objects) {
        super(context, R.id.selectItemTextView, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        if(row==null){
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.select_list_item, parent, false);
        }

        ImageView imageView = (ImageView) row.findViewById(R.id.selectVeggieImageView);
        TextView textView = (TextView) row.findViewById(R.id.selectItemTextView);
        AvailableBook availableBook = getItem(position);
        textView.setText(availableBook.getTitle());
        imageView.setScaleType(ImageView.ScaleType.FIT_CENTER   );
        log.debug("imageView width: {}", parent.getWidth() /3);

        if(availableBook.getUrl() == null || availableBook.getUrl().isEmpty()){
            imageView.setVisibility(View.GONE);
            row.setMinimumHeight((parent.getWidth()/3) * 2/3);
        }
        else{
            imageView.setMinimumHeight((parent.getWidth()/3) * 2/3);

            Picasso.with(getContext()).load(availableBook.getUrl()).into(imageView);
        }
        return row;
    }
}
