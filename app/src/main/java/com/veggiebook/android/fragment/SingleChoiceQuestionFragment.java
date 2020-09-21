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
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.veggiebook.R;
import com.veggiebook.model.SingleChoiceQuestion;

/**
 * Created with IntelliJ IDEA.
 * User: danieldipasquo
 * Date: 2/12/13
 * Time: 12:15 PM
 *
 */
public class SingleChoiceQuestionFragment extends QuestionFragment implements AdapterView.OnItemSelectedListener{
    private int itemSelected;

    public static QuestionFragment newInstance(SingleChoiceQuestion question){
        QuestionFragment questionFragment = new SingleChoiceQuestionFragment();
        Bundle args = new Bundle();
        args.putString(QUESTION_ID_KEY, question.getQuestionIdentifier());
        questionFragment.setArguments(args);

        return questionFragment;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        SingleChoiceQuestion question = (SingleChoiceQuestion) getQuestion();

        Spinner spinner = new Spinner(getActivity());
        spinner.setPromptId(R.string.select_pantry_question);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_item);
        adapter.add(getString(R.string.select_pantry_default));
        adapter.addAll(question.getChoiceTexts());
        spinner.setOnItemSelectedListener(this);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setGravity(Gravity.CENTER_HORIZONTAL);
        questionFormLayout.addView(spinner);

        Integer selection = (Integer) question.getAnswer();
        if(selection==null){
            selection = (Integer) question.getDefault();
        }
        if(selection==null){
            itemSelected=0;
        }
        else{
            itemSelected=selection+1;
            spinner.setSelection(itemSelected);
        }

    }

    @Override
    public boolean isValidAnswer() {
        return itemSelected>0;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        int previousSelection = itemSelected;
        itemSelected=position;
        if((itemSelected == 0) || (previousSelection == 0)){
            getActivity().invalidateOptionsMenu();
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        itemSelected = 0;
    }

    @Override
    public void answerQuestion() {
        SingleChoiceQuestion question = (SingleChoiceQuestion) getQuestion();
        if(question==null) return;
        //todo: should this throw an exception (probably)
        if(itemSelected==0)  {
            question.answer(null);
        }
        else {
            question.answer(itemSelected - 1);
        }
    }

    @Override
    public boolean displayKeepDrop() {
        return false;
    }

}
