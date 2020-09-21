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

import androidx.fragment.app.Fragment;

import com.veggiebook.model.CoverSelectQuestion;
import com.veggiebook.model.LoadingQuestion;
import com.veggiebook.model.MultipleChoiceQuestion;
import com.veggiebook.model.Question;
import com.veggiebook.model.SelectionQuestion;
import com.veggiebook.model.ShareQuestion;
import com.veggiebook.model.SingleChoiceQuestion;

/**
 * Created with IntelliJ IDEA.
 * User: danieldipasquo
 * Date: 2/19/13
 * Time: 6:31 PM
 *
 */
public class QuestionUiFactory {

    public static Fragment newInstance(Question question){
        if(question instanceof SingleChoiceQuestion){
            return SingleChoiceQuestionFragment.newInstance((SingleChoiceQuestion) question);
        }
        if(question instanceof MultipleChoiceQuestion){
            return MultipleChoiceQuestionFragment.newInstance((MultipleChoiceQuestion) question);
        }
        if(question instanceof SelectionQuestion){
            return SelectionQuestionFragment.newInstance((SelectionQuestion) question);
        }
        if(question instanceof CoverSelectQuestion){
            return CoverSelectionFragment.newInstance((CoverSelectQuestion) question);
        }
        if(question instanceof LoadingQuestion){
            return LoadingFragment.newInstance((LoadingQuestion) question);
        }
        if(question instanceof ShareQuestion){
            return ShareQuestionFragment.newInstance((ShareQuestion) question);
        }

        return null;
    }
}
