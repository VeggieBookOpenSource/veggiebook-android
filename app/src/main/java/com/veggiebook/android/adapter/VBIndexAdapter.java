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
import android.content.res.Configuration;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.GridView;

import com.squareup.picasso.Picasso;
import com.veggiebook.R;
import com.veggiebook.android.view.CascadeItemView;
import com.veggiebook.model.RecipeBook;

import java.util.ArrayList;
import java.util.List;

public class VBIndexAdapter extends ArrayAdapter<RecipeBook.Recipe> {


    public static VBIndexAdapter newInstance(Context context, RecipeBook recipeBook){
        List<RecipeBook.Recipe> objectList = new ArrayList<RecipeBook.Recipe>(recipeBook.getRecipes().size()+1);
        if(recipeBook.getTipsUrl() != null && !recipeBook.getTipsUrl().isEmpty()){
            objectList.add(
                    new RecipeBook.Recipe(
                            "TIPS",
                            R.drawable.light_bulb,
                            String.format(context.getString(R.string.tips_for_format), recipeBook.getTitle()),
                            recipeBook.getTipsUrl(),recipeBook.getTipsUrl()));
        }

        objectList.addAll(recipeBook.getRecipes());


        return new VBIndexAdapter(context, R.id.selectItemTextView, objectList);
    }

    protected VBIndexAdapter(Context context, int textViewResourceId, List<RecipeBook.Recipe> objects) {
        super(context, textViewResourceId, objects);
    }


    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        CascadeItemView imageView;
        if (convertView == null) {  // if it's not recycled, initialize some attributes
            imageView = new CascadeItemView(getContext());
        } else {

            imageView = (CascadeItemView) convertView;
        }

        if(getContext().getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
            imageView.setLayoutParams(new GridView.LayoutParams(parent.getHeight() / 2 - 12, parent.getHeight() / 2 - 12));
        }
        else{
            imageView.setLayoutParams(new GridView.LayoutParams(parent.getWidth() / 2 - 12, parent.getWidth() / 2 - 12));
        }
        imageView.setPadding(0, 6, 0, 6);


        RecipeBook.Recipe recipe = getItem(position);
        imageView.getTitleView().setTextSize(TypedValue.COMPLEX_UNIT_DIP, (float) 14);

        if(recipe.hasImageUrl()){
            Picasso.with(getContext()).load(recipe.getPhotoUrl()).into(imageView.getImgView());
        }
        else{
            imageView.getImgView().setImageResource(recipe.getImageId());
        }
        imageView.setGradientEnabled(false);
        imageView.getImgView().setAlpha((float) 1.0);
        imageView.getTitleView().setBackgroundResource(R.color.title_background);
        imageView.getTitleView().setGravity(Gravity.CENTER | Gravity.START);
        imageView.getTitleView().setTextAlignment(View.TEXT_ALIGNMENT_CENTER );
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        lp.setMargins(2,2,2,2);

        imageView.getTitleView().setLayoutParams(lp);
        imageView.getTitleView().setPadding(4,0,4,0);

        imageView.setTitle(recipe.getName());



        return imageView;
    }

    private void setCreateButton(CascadeItemView cascadeItemView){
        cascadeItemView.setImageAndTitle(R.drawable.new_veggie_book,R.string.new_veggie_book);
    }


}
