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

import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.veggiebook.R;
import com.veggiebook.android.AppSettings;
import com.veggiebook.model.BookManager;
import com.veggiebook.model.Pantry;
import com.veggiebook.model.SingleChoiceQuestion;
import com.veggiebook.service.dto.PantriesResponse;
import com.veggiebook.service.rest.http.VbHttpException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: danieldipasquo
 * Date: 5/6/13
 * Time: 9:20 AM
 *
 **/

public class PrintDialogFragment extends DialogFragment implements SingleChoiceQuestion.Handler, AdapterView.OnItemSelectedListener, View.OnClickListener {
    private static Logger log = LoggerFactory.getLogger(PrintDialogFragment.class);
    private final static String BOOK_ID = "book_id";
    private final static String BOOK_TYPE = "book_type";



    Spinner spinner;
    List<Pantry> pantries = new ArrayList<Pantry>(10);
    String selectedPantryId;
    SingleChoiceQuestion question;
    private String bookId;
    ProgressBar progressBar;
    TextView textView;
    Button printButton;
    String type;


    public static PrintDialogFragment newInstance(String bookId, String type){
        PrintDialogFragment fragment = new PrintDialogFragment();
        Bundle args = new Bundle();
        args.putString(BOOK_ID,bookId);
        args.putString(BOOK_TYPE,type);
        fragment.setArguments(args);
        return fragment;

    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NO_FRAME, android.R.style.Theme_Holo_Light);
        bookId = getArguments().getString(BOOK_ID);
        type = getArguments().getString(BOOK_TYPE);

    }


    //Handler for SingleChoiceQuestion.Handler
    @Override
    public void handleSCAnswer(SingleChoiceQuestion question, Integer selectionId) {
        selectedPantryId = question.getChoice(selectionId)==null?null:question.getChoice(selectionId).getId();
        if(selectedPantryId != null) {
            AppSettings.setDefaultPantryId(selectedPantryId);
            AppSettings.setDefaultPantryName(question.getChoice(selectionId).getDisplayText());
        }
    }


    public void createPantryQuestion() {
        question = new SingleChoiceQuestion(this, "pantryQuestion");

        question.setQuestionText(AppSettings.getContext().getString(R.string.select_pantry_question));
        for (Pantry pantry : pantries) {
            question.addChoice(pantry.getDisplayName(), pantry.getId());
        }

        String defaultId = AppSettings.getDefaultPantryId();
        if (defaultId != null) {
            boolean defaultPantryHere = false;
            for (int i = 0; i < question.getChoices().size(); i++) {
                if (question.getChoices().get(i).getId().equals(defaultId)) {
                    defaultPantryHere = true;
                    question.setDefaultSelectionId(i);
                    question.answer(i);
                }
            }
            if (!defaultPantryHere) {
                question.addChoice(AppSettings.getDefaultPantryName(), defaultId);
                question.setDefaultSelectionId(question.getChoices().size() - 1);
                question.answer(question.getChoices().size() - 1);
            }
        }


    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.print_dialog, container, false);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(0));



        textView = (TextView) v.findViewById(R.id.textView);
        textView.setVisibility(View.INVISIBLE);

        spinner = (Spinner) v.findViewById(R.id.spinner);
        spinner.setVisibility(View.INVISIBLE);



        Button cancelButton = (Button) v.findViewById(R.id.button_no_print);
        printButton = (Button) v.findViewById(R.id.button_print);

        cancelButton.setOnClickListener(this);
        printButton.setOnClickListener(this);
        printButton.setVisibility(View.INVISIBLE);

        progressBar = (ProgressBar) v.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);

        new GetPantries().execute();

        return v;
    }


    private void pantriesReturned(){
        createPantryQuestion();
        textView.setVisibility(View.VISIBLE);
        spinner.setPromptId(R.string.select_pantry_question);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_item);
        adapter.add(getString(R.string.select_pantry_default));

        selectedPantryId = AppSettings.getDefaultPantryId();

        adapter.addAll(question.getChoiceTexts());
        spinner.setOnItemSelectedListener(this);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setVisibility(View.VISIBLE);

        if(selectedPantryId != null && question.getChoice(selectedPantryId) != null){
            spinner.setSelection(question.getChoices().indexOf(question.getChoice(selectedPantryId))+1);
        }


        progressBar.setVisibility(View.INVISIBLE);
        printButton.setVisibility(View.VISIBLE);

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        question.answer(position);

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.button_print && selectedPantryId != null)
            new PrintTask().execute();

        if(v.getId()==R.id.button_no_print || v.getId()==R.id.closebutton){
            this.dismiss();
        }
    }


    public class GetPantries extends AsyncTask<Void,Void,Void>{

        @Override
        protected Void doInBackground(Void... params) {
            BookManager bookManager = BookManager.getBookManager();
            try {
                for (PantriesResponse p : bookManager.getRestService().pantries()){
                    pantries.add(new Pantry(p.getName(), p.getId()));
                }
            } catch (VbHttpException e) {
                e.printStackTrace();
            }
            return null;        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if(pantries.size() == 0){
                textView.setText(R.string.no_printers);
                textView.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.INVISIBLE);
            }
            else {
                pantriesReturned();
            }

        }
    }

    public class PrintTask extends AsyncTask<Void,Void,Void>{

        @Override
        protected void onPreExecute() {
            if(getActivity()==null) return;
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(Void... params) {
            BookManager bookManager = BookManager.getBookManager();
            try {
                bookManager.getRestService().printVeggieBook(bookId, bookManager.getLanguage(), selectedPantryId, type );
            } catch (VbHttpException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if(getActivity()==null) return;
            progressBar.setVisibility(View.INVISIBLE);
            Toast toast = Toast.makeText(getActivity(), getString(R.string.print_complete) , Toast.LENGTH_LONG);
            toast.show();
            dismiss();
        }
    }

}
