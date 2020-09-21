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

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.veggiebook.android.fragment.QuestionUiFactory;
import com.veggiebook.android.fragment.SelectionQuestionFragment;
import com.veggiebook.model.BookBuilder;
import com.veggiebook.model.Question;

/**
 * Created with IntelliJ IDEA.
 * User: danieldipasquo
 * Date: 2/8/13
 * Time: 7:09 PM
 *
 */
public class QuestionPagerAdapter extends FragmentStatePagerAdapter {

    BookBuilder bookBuilder;

    public QuestionPagerAdapter(FragmentManager fm, BookBuilder bookBuilder) {
        super(fm);
        this.bookBuilder=bookBuilder;
    }

    @Override
    public Fragment getItem(int i) {
        if(bookBuilder==null)return null;

        Question q = bookBuilder.getQuestion(i);
        Fragment f = QuestionUiFactory.newInstance(q);

        return f;
    }

    @Override
    public int getCount() {
        return bookBuilder!=null?bookBuilder.getNumQuestions():0;

    }


    @Override
    public int getItemPosition(Object object){
        if(object instanceof SelectionQuestionFragment){
            return POSITION_NONE;
        }
        else return POSITION_UNCHANGED;
    }


}
