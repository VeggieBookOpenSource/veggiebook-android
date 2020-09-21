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

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.veggiebook.R;
import com.veggiebook.android.activity.InterviewActivity;
import com.veggiebook.model.BookBuilder;
import com.veggiebook.model.Question;

import roboguice.fragment.RoboFragment;
import roboguice.inject.InjectView;

/**
 * Created with IntelliJ IDEA.
 * User: danieldipasquo
 * Date: 2/12/13
 * Time: 12:05 PM
 *
 */
public abstract class QuestionFragment extends RoboFragment implements QuestionUi{
    public static final String QUESTION_ID_KEY="questionId";
    public static final String POSITION_KEY="position";

    @InjectView(R.id.questionText)
    TextView questionTextView;

    @InjectView(R.id.questionForm)
    LinearLayout questionFormLayout;

    @InjectView(R.id.subQuestionText)
    TextView subQuestionTextView;

    private String questionId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.question_fragment,container,false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //get the question from the book manager
        Bundle args = getArguments();

        questionId = args.getString(QUESTION_ID_KEY);
        Question q = getQuestion();

        getQuestionTextView().setText(q!=null?q.getQuestionText():"");

    }




    protected TextView getQuestionTextView() {
        return questionTextView;
    }

    protected LinearLayout getQuestionFormLayout() {
        return questionFormLayout;
    }

    protected BookBuilder getBookBuilder() {
        if(getActivity()==null)
            return null;
        return ((InterviewActivity) getActivity()).getBookBuilder();
    }



    protected TextView getSubQuestionTextView() {
        return subQuestionTextView;
    }


    public Question getQuestion() {
        BookBuilder bookBuilder =  getBookBuilder();
        return bookBuilder!=null ? getBookBuilder().getQuestion(questionId): null;
    }



    public boolean displayImageTitle(){
        return true;
    }

    public abstract boolean isValidAnswer();
    public abstract void answerQuestion();

    @Override
    public boolean displayKeepDrop() {
        return false;
    }


}
