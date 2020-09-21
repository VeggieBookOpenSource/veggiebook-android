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
import android.view.View;
import android.widget.CheckBox;
import android.widget.Space;

import com.veggiebook.R;
import com.veggiebook.android.AppSettings;
import com.veggiebook.model.ChoiceQuestion;
import com.veggiebook.model.MultipleChoiceQuestion;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: danieldipasquo
 * Date: 2/12/13
 * Time: 12:07 PM
 *
 */
public class MultipleChoiceQuestionFragment extends QuestionFragment implements View.OnClickListener{
    List<CheckboxHolder> checkBoxes;
    Map<String,Boolean> answers;
    static final int ID_OFFSET = 100;

    public static QuestionFragment newInstance(MultipleChoiceQuestion question){
        QuestionFragment questionFragment = new MultipleChoiceQuestionFragment();
        Bundle args = new Bundle();
        args.putString(QUESTION_ID_KEY, question.getQuestionIdentifier());
        questionFragment.setArguments(args);

        return questionFragment;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //get the question
        MultipleChoiceQuestion question = (MultipleChoiceQuestion) getQuestion();

        checkBoxes = new ArrayList<CheckboxHolder>(question.getChoices().size());

        answers = (Map<String, Boolean>) question.getAnswer();
        if(answers==null){
            answers = (Map<String,Boolean>) question.getDefault();
        }

        //Set up check boxes
        for(ChoiceQuestion.Choice choice : question.getChoices()){
            CheckBox checkBox = new CheckBox(getActivity());
//            checkBox.setButtonDrawable(R.drawable.btn_check_holo_light);
            checkBox.setText(choice.getDisplayText());
            checkBox.setId(getCheckBoxId(checkBoxes.size()));
            checkBox.setOnClickListener(this);
            checkBox.setChecked(answers.get(choice.getId()));
            checkBoxes.add(new CheckboxHolder(checkBox, choice));
            questionFormLayout.addView(checkBox);
            Space space = new Space(getActivity());
            space.setMinimumHeight(Math.round(checkBox.getLineHeight()/3));
            questionFormLayout.addView(space);
        }

        getSubQuestionTextView().setText(R.string.select_multiple_sub);



    }

    private int getCheckBoxId(int position){
        return position+ID_OFFSET;
    }

    private int getPosition(int checkboxId){
        return checkboxId-ID_OFFSET;
    }

    @Override
    public boolean isValidAnswer() {
        //if no selection is required, the answer is always valid
        if( !((MultipleChoiceQuestion) getQuestion()).getRequireSelection()){
            return true;
        }

        //to be valid at least one checkbox must be checked
        boolean isValid = false;
        for(CheckboxHolder cb : checkBoxes){
            if(cb.getCheckBox().isChecked()){
                isValid = true;
                break;
            }
        }
        return isValid;
    }

    @Override
    public void answerQuestion() {
        MultipleChoiceQuestion question = (MultipleChoiceQuestion) getQuestion();
        if(question==null) return;
        question.answer(answers);
        //track the answering of this question
        JSONObject props = new JSONObject(answers);
        try {
            props.put("Book Type", "Veggie");
            props.put("Book Id", getBookBuilder().getAvailableBook().getBookId());
            props.put("Question Id", getQuestion().getQuestionIdentifier());
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public boolean displayKeepDrop() {
        return false;
    }



    @Override
    public void onClick(View v) {
        int position = getPosition(v.getId());
        String id = checkBoxes.get(position).getChoice().getId();
        boolean checked = ((CheckBox) v).isChecked();
        answers.put(id,checked);
        getActivity().invalidateOptionsMenu();

    }


    private class CheckboxHolder{
        private CheckBox checkBox;
        private ChoiceQuestion.Choice choice;

        private CheckboxHolder(CheckBox checkBox, ChoiceQuestion.Choice choice) {
            this.checkBox = checkBox;
            this.choice = choice;
        }

        public CheckBox getCheckBox() {
            return checkBox;
        }

        public ChoiceQuestion.Choice getChoice() {
            return choice;
        }
    }
}
