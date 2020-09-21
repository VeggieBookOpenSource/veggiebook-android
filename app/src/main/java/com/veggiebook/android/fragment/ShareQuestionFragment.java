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
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import com.veggiebook.R;
import com.veggiebook.android.activity.InterviewActivity;
import com.veggiebook.android.adapter.ShareRecipesAdapter;
import com.veggiebook.android.view.CascadeItemView;
import com.veggiebook.model.BookBuilder;
import com.veggiebook.model.CoverSelectQuestion;
import com.veggiebook.model.Question;
import com.veggiebook.model.ShareQuestion;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import roboguice.fragment.RoboFragment;
import roboguice.inject.InjectView;

public class ShareQuestionFragment extends RoboFragment implements QuestionUi, AdapterView.OnItemClickListener {
    public static Logger log = LoggerFactory.getLogger(ShareQuestionFragment.class);
    public static final String QUESTION_ID_KEY = "questionId";
    private String questionId;

    @InjectView(R.id.gridView)
    GridView gridView;

    @InjectView(R.id.textView)
    TextView textView;



    public static ShareQuestionFragment newInstance(ShareQuestion question) {
        ShareQuestionFragment fragment = new ShareQuestionFragment();
        Bundle args = new Bundle();
        args.putString(QUESTION_ID_KEY, question.getQuestionIdentifier());
        fragment.setArguments(args);

        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        questionId = getArguments() != null ? getArguments().getString(QUESTION_ID_KEY) : null;

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.share_question_fragment, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        log.debug("onActivityCreated");

        //Read from the saved state if there is one
        //most of our state is stored in the InterviewActivity's Bundle
        //via the BookBuilder, but we do save off the fileUri, and
        //current questionId
        if (savedInstanceState != null) {
            questionId = savedInstanceState.getString(QUESTION_ID_KEY, questionId);
        }

        getActivity().getActionBar().show();
        gridView.setAdapter(ShareRecipesAdapter.newInstance(getBookBuilder()));
        gridView.setOnItemClickListener(this);
        gridView.setNumColumns(2);



        //get the question from the book builder
        Question q = getQuestion();

        //Get the current state from the question
        CoverSelectQuestion.Answer answer = (CoverSelectQuestion.Answer) q.getAnswer();

        if(getBookBuilder().getAvailableBook().getBookType().equals("SECRETS_BOOK")){
            textView.setText(R.string.share_question_instructions_secret);
        }


    }

    protected BookBuilder getBookBuilder() {
        if (getActivity() == null) {
            return null;
        }
        return ((InterviewActivity) getActivity()).getBookBuilder();
    }


    @Override
    public Question getQuestion() {
        return getBookBuilder().getQuestion(questionId);
    }


    @Override
    public boolean displayImageTitle() {
        return false;
    }

    @Override
    public boolean isValidAnswer() {
        return true;
    }


    @Override
    public void answerQuestion() {

    }

    @Override
    public boolean displayKeepDrop() {
        return false;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        ((ShareRecipesAdapter) gridView.getAdapter()).setSelection(position, ((CascadeItemView) view).toggleCheckBox());
    }
}
