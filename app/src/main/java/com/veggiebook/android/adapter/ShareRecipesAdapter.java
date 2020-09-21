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

import android.content.res.Configuration;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.GridView;

import com.squareup.picasso.Picasso;
import com.veggiebook.R;
import com.veggiebook.android.view.CascadeItemView;
import com.veggiebook.model.BookBuilder;
import com.veggiebook.model.Question;
import com.veggiebook.model.SelectionQuestion;

import java.util.ArrayList;
import java.util.List;

public class ShareRecipesAdapter extends BaseAdapter {
    private List<Question> questions;


    public static ShareRecipesAdapter newInstance(BookBuilder bookBuilder){
        List<Question> objectList = new ArrayList<Question>(bookBuilder.getNumQuestions());
        for(int i=0; i<bookBuilder.getNumQuestions();i++){
            objectList.add(bookBuilder.getQuestion(i));
        }
        return new ShareRecipesAdapter(objectList);
    }

    protected ShareRecipesAdapter(List<Question> objects) {
       super();
        this.questions = objects;
    }


    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        CascadeItemView imageView;
        if (convertView == null) {  // if it's not recycled, initialize some attributes
            imageView = new CascadeItemView(parent.getContext());
        } else {

            imageView = (CascadeItemView) convertView;
        }

        if(parent.getContext().getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
            imageView.setLayoutParams(new GridView.LayoutParams(parent.getHeight() / 2 - 40, parent.getHeight() / 2 - 40));
        }
        else{
            imageView.setLayoutParams(new GridView.LayoutParams(parent.getWidth() / 2 - 40, parent.getWidth() / 2 - 40));
        }
        imageView.setPadding(0, 6, 0, 6);


        SelectionQuestion question = (SelectionQuestion) getItem(position);

        imageView.getTitleView().setTextSize(TypedValue.COMPLEX_UNIT_DIP, (float) 14);

        Picasso.with(parent.getContext()).load(question.getPhotoUrl()).into(imageView.getImgView());
        imageView.getImgView().setAlpha((float) 1.0);
        imageView.getTitleView().setBackgroundResource(R.color.title_background);
        imageView.getTitleView().setGravity(Gravity.CENTER | Gravity.START);
        imageView.getTitleView().setTextAlignment(View.TEXT_ALIGNMENT_CENTER );
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        lp.setMargins(2,2,2,2);

        imageView.getTitleView().setLayoutParams(lp);
        imageView.getTitleView().setPadding(4,0,4,0);

        imageView.setTitle(question.getQuestionText());
        imageView.enableCheckBox();
        SelectionQuestion.Answer answer = (SelectionQuestion.Answer)question.getAnswer();
        imageView.setChecked(answer != null && answer.getExtraCopies() > 0);
        imageView.setClickable(false);

        return imageView;
    }


    @Override
    public int getCount() {
        int count = 0;
        for(Question question : questions){
            if(question instanceof SelectionQuestion && question.getAnswer() != null && ((SelectionQuestion.Answer) question.getAnswer()).isIncluded())
                ++count;
        }

        return count;

    }

    @Override
    public Object getItem(int position) {
        int index = -1;
        for(Question question : questions){
            if(question instanceof SelectionQuestion && question.getAnswer() != null && ((SelectionQuestion.Answer) question.getAnswer()).isIncluded())
                ++index;
            if(index==position)
                return question;
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        int index = -1;
        for(Question question : questions){
            if(question instanceof SelectionQuestion && question.getAnswer() != null && ((SelectionQuestion.Answer) question.getAnswer()).isIncluded())
                ++index;
            if(index==position)
                return index;
        }
        return -1;
    }

    public void setSelection(int position, boolean selected){
        SelectionQuestion question = (SelectionQuestion) getItem(position);
        ((SelectionQuestion.Answer) question.getAnswer()).setExtraCopies(selected? 1 : 0);


    }

}
